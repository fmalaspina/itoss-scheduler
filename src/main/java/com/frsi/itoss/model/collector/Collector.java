package com.frsi.itoss.model.collector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.ct.CtProjection;
import com.frsi.itoss.model.profile.EventRule;
import com.frsi.itoss.model.profile.Instrumentation;
import com.frsi.itoss.model.profile.Monitor;
import com.frsi.itoss.shared.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.envers.Audited;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Audited
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Collector extends EntityBase implements Serializable {
    /**
     *
     */

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "collector_gen")
    @SequenceGenerator(name = "collector_gen", sequenceName = "collector_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;


    @ManyToMany
    @ToString.Exclude
    private Set<Instrumentation> instrumentations = new HashSet<>();

    private String endpoint;
    @Enumerated(EnumType.STRING)
    private CollectorConfigurationMode mode;


    public Collector() {
        super();
    }




    @JsonIgnore
    @Transient
    @PostConstruct
    public CollectorConfiguration getConfiguration(CtProjection ct,
                                                   Optional<Monitor> monitor, String scoringExpression) {
        return this.buildConfig(Collections.singletonList(ct), monitor, scoringExpression);
    }

    @JsonIgnore
    @Transient
    public CollectorConfiguration getConfiguration(List<CtProjection> ctSet,
                                                   Optional<Monitor> monitor, String scoringExpression) {
        return this.buildConfig(ctSet, monitor, scoringExpression);

    }
    @SuppressWarnings("unchecked")
    @JsonIgnore
    @Transient
    public CollectorConfiguration buildConfig(List<CtProjection> ctSet, Optional<Monitor> monitor, String scoringExpression) {
        final CollectorConfiguration configuration = new CollectorConfiguration();
        MonitorConfiguration mc;
        Set<Monitor> monitorSet = new HashSet<>();
        for (var ct : ctSet) {
            if (ct.getMonitoringProfile() != null) {

                if (monitor.isPresent()) {


                    monitorSet.add(monitor.get());
                } else {

                    monitorSet = ct.getMonitoringProfile().getMonitors();

                }
                for (Monitor m : monitorSet) {

                    Optional<MonitorConfiguration> foundMonitor = configuration.getMonitor(m.getId());

                    if (foundMonitor.isPresent()) {
                        mc = foundMonitor.get();
                    } else {
                        mc = new MonitorConfiguration();
                        configuration.getMonitors().add(mc);
                    }

                    mc.setMonitorId(m.getId());

                    List<EventRuleConfig> eventRuleList = new ArrayList<>();

                    for (EventRule e : m.getEventRules().stream().filter(EventRule::isActive).toList()) {
                        if (e.isActive()) {
                            EventRuleConfig erc = new EventRuleConfig();
                            erc.setId(e.getId());
                            erc.setName(e.getName());
                            erc.setPhase(e.getPhase());
                            erc.setDescription(e.getDescription());
                            erc.setCondition(e.getCondition());
                            erc.setActions(e.getActions());
                            erc.setPriority(e.getPriority());
                            erc.setCtId(e.getCtId());
                            erc.setActive(e.isActive());
                            erc.setCounterThreshold(e.getCounterThreshold());
                            erc.setCounterResetTimeInterval(e.getCounterResetTimeInterval());
                            erc.setCounterResetTimeIntervalUnit(e.getCounterResetTimeIntervalUnit());
                            erc.setSuppressActive(e.getSuppressActive());
                            erc.setSendFirst(e.getSendFirst());
                            eventRuleList.add(erc);

                        }
                    }
                    mc.setEventRules(eventRuleList);
                    //mc.setRuleEvaluationMode(m.getRuleEvaluationMode());
                    mc.setSkipOnFirstAppliedRule(m.isSkipOnFirstAppliedRule());
                    mc.setSkipOnFirstFailedRule(m.isSkipOnFirstFailedRule());
                    mc.setSkipOnFirstNonTriggeredRule(m.isSkipOnFirstNonTriggeredRule());
                    mc.setFrequencyExpression(m.getFrequencyExpression());
                    mc.setMetricName(m.getMetric().getName());
                    mc.setMetricId(m.getMetric().getId());
                    mc.setContainerId((m.getContainer() == null) ? 0L : m.getContainer().getId());
                    mc.setMonitorName(m.getName());
                    mc.setMetricCategory(m.getMetric().getMetricCategory());
                    mc.setMetricPayloadAttributes(m.getMetric().getMetricPayloadAttributes());
                    mc.setScoringExpression(scoringExpression);
                    //mc.setStatusMetric(m.getMetric().isStatusMetric());
                    InstrumentationAdapter ia = new InstrumentationAdapter();
                    ia.setDescription(m.getMetric().getInstrumentation().getDescription());
                    ia.setInstrumentationParameters(m.getMetric().getInstrumentation().getInstrumentationParameters());
                    ia.setName(m.getMetric().getInstrumentation().getName());
                    mc.setInstrumentationAdapter(ia);
                    mc.setMonitorInstrumParamValues(m.getInstrumentationParameterValues());
                    // testing instrumentation parameter value map

                    mc.setMetricInstrumParamValues(m.getMetric().getInstrumentationParameterValues());
                    CtConfiguration ctc = new CtConfiguration();
                    ctc.setCompanyAttributes(ct.getCompany().getAttributes());
                    ctc.setCtId(ct.getId().toString());
                    ctc.setCtAttributes((List<Attribute>)ct.getAttributes());
                    ctc.setCompanyId(ct.getCompany().getId());
                    ctc.setCtName(ct.getName());
                    ctc.setCtType(ct.getType().getName());
                    ctc.setTypeAttributes(ct.getType().getTypeAttributes());
                    ctc.setEnvironment(ct.getEnvironment());
                    ctc.setCtInstrumParamValues((List<InstrumentationParameterValue>)ct.getInstrumentationParameterValues());
                    if (ct.getWorkgroup() != null) {
                        ctc.setWorkgroup(ct.getWorkgroup().getName());
                    } else {
                        ctc.setWorkgroup("<none>");
                    }
                    if (ct.getSupportUser() != null) {
                        ctc.setSupportUser(ct.getSupportUser().getName());
                    } else {
                        ctc.setSupportUser("<none>");
                    }
                    if (ct.getCompany() != null) {
                        ctc.setCompany(ct.getCompany().getName());
                    } else {
                        ctc.setCompany("<none>");
                    }

                    ctc.setProfileId(ct.getMonitoringProfile().getId().toString());
                    ctc.setProfileName(ct.getMonitoringProfile().getName());
                    ctc.setEnvironment(ct.getEnvironment());
                    if (ct.getCompany() != null) {
                        ctc.setCompany(ct.getCompany().getName());
                    }
                    mc.getCtConfiguration().add(ctc);
                    mc.setProfileName(ctc.getProfileName());
                    mc.setProfileId(ctc.getProfileId());

                }
            }
        }


        return configuration;

    }


    @Override
    public String toString() {
        return "Collector [name=" + name + ", endpoint=" + endpoint + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Collector collector = (Collector) o;
        return getId() != null && Objects.equals(getId(), collector.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
