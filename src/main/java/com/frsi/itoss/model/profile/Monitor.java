package com.frsi.itoss.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.SpringContext;
import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.dashboard.Container;
import com.frsi.itoss.model.repository.ContainerRepo;
import com.frsi.itoss.model.repository.EventRuleRepo;
import com.frsi.itoss.model.repository.MonitorRepo;
import com.frsi.itoss.shared.InstrumentationParameterValue;
import com.frsi.itoss.shared.RuleEvaluationMode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Monitor extends EntityBase implements Serializable {
    /**
     *
     */

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "monitor_gen")
    @SequenceGenerator(name = "monitor_gen", sequenceName = "monitor_seq", initialValue = 200000, allocationSize = 1)

    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;

    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Metric metric;

    private String frequencyExpression;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<InstrumentationParameterValue> instrumentationParameterValues;
    @Enumerated(EnumType.STRING)
    private RuleEvaluationMode ruleEvaluationMode;

    private boolean skipOnFirstAppliedRule = false;
    private boolean skipOnFirstFailedRule = false;
    private boolean skipOnFirstNonTriggeredRule = false;

    @OneToMany(fetch = FetchType.EAGER

    )
    @JoinColumn(name = "monitor_id")
    private Set<EventRule> eventRules = new HashSet<>();

    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Container container;

    public void addEventRule(EventRule er) {
        this.eventRules.add(er);
    }

    @JsonIgnore
    public Set<EventRule> getNonCustomRules() {
        return this.eventRules.stream().filter(r -> r.getCtId() == null).collect(Collectors.toSet());
    }

    @JsonIgnore
    public Set<EventRule> getCustomRules(Long ctId) {
        return this.eventRules.stream().filter(r -> r.getCtId() == ctId).collect(Collectors.toSet());
    }

    @PreRemove
    @Transactional
    @Modifying
    public void deleteContainer() {
        this.eventRules = null;
        this.container = null;
        MonitorRepo monitorRepo = SpringContext.getBean(MonitorRepo.class);
        monitorRepo.deleteByMonitorId(this.getId());

    }

    @PreUpdate
    @PrePersist
    @Transactional
    @Modifying
    public void updateContainer() {

        ContainerRepo containerRepo = SpringContext.getBean(ContainerRepo.class);
        //EventRuleRepo eventRuleRepo = SpringContext.getBean(EventRuleRepo.class);
        if (this.getId() == null || this.getId() == 0) {
            this.container = new Container();
            this.container.setName(this.getName());

            this.container = containerRepo.save(this.container);
        }


    }

}
