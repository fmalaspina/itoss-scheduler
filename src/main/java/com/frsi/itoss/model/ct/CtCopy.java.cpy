package com.frsi.itoss.model.ct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frsi.itoss.model.SpringContext;
import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.baseclasses.ValidationListener;
import com.frsi.itoss.model.collector.Collector;
import com.frsi.itoss.model.company.Company;
import com.frsi.itoss.model.company.Contact;
import com.frsi.itoss.model.location.Location;
import com.frsi.itoss.model.profile.Monitor;
import com.frsi.itoss.model.profile.MonitoringProfile;
import com.frsi.itoss.model.repository.CtRelationRepo;
import com.frsi.itoss.model.repository.MonitoringProfileRepo;
import com.frsi.itoss.model.repository.UserAccountRepo;
import com.frsi.itoss.model.statemachine.CtEvent;
import com.frsi.itoss.model.statemachine.CtState;
import com.frsi.itoss.model.user.UserAccount;
import com.frsi.itoss.model.workgroup.Workgroup;
import com.frsi.itoss.shared.Attribute;
import com.frsi.itoss.shared.CtStatus;
import com.frsi.itoss.shared.InstrumentationParameter;
import com.frsi.itoss.shared.InstrumentationParameterValue;
import com.google.common.net.InetAddresses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log
@Data
@AllArgsConstructor
//@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Audited
@EnableAsync
@EntityListeners(ValidationListener.class)
/*
 * Componente tecnológico: Unidad administrable de un sistema o servicio
 * informático.
 */
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}), indexes = {
        @Index(name = "IDX_COLLECTOR", columnList = "collector_id"),
        @Index(name = "IDX_COLLECTOR_STATE", columnList = "collector_id,state")})

@JsonIgnoreProperties(value = "status", allowGetters = true, allowSetters = false)

public class CtCopy extends EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ct_gen")
    @SequenceGenerator(name = "ct_gen", sequenceName = "ct_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    private String integrationId;
    private String environment;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<InstrumentationParameterValue> instrumentationParameterValues;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)

    private MonitoringProfile monitoringProfile;
    @RestResource(exported = false)
    @ManyToOne

    private Collector collector;
    @RestResource(exported = false)
    @ManyToOne

    private CtType type;
    @RestResource(exported = false)
    @ManyToOne
    private Company company;
    @RestResource(exported = false)
    @ManyToOne
    private Workgroup workgroup;
    @RestResource(exported = false)
    @ManyToOne
    private Contact contact;
    @RestResource(exported = false)
    @ManyToOne
    private UserAccount supportUser;
    @RestResource(exported = false)
    @ManyToOne
    private Location location;
    @Enumerated(EnumType.STRING)
    private CtState state = CtState.DELIVERY;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ct_id")
    private Set<CtHistory> ctHistory = new HashSet<>();
    @NotAudited

    @RestResource(exported = false)
    @OneToOne
    @JoinColumn(name = "id")
    private CtStatus status;
    @JsonIgnore
    private String oldPassword;
    @JsonIgnore
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> oldCryptedPropertyValues = new HashMap<>();

    public CtCopy() {
        super();
    }


    @JsonIgnore
    @Transient
    public static Predicate<InstrumentationParameter> isIn(List<String> attributes) {
        return p -> attributes.contains(p.getName());
    }

    @JsonIgnore
    private CtKeyConstructor getCtKeyConstructor() {
        return SpringContext.getBean(CtKeyConstructor.class);
    }


    @PrePersist
    @PreUpdate

    // @PostLoad
    private void setKeyDefault() throws Exception {
        if (this.id == null) {

            UserAccountRepo userRepo = SpringContext.getBean(UserAccountRepo.class);
            Long userId = userRepo.findByUsername(this.createdBy).getId();
            CtHistory ctHistory = new CtHistory(new Date(), userId, CtEvent.DELIVER, "Created and set to DELIVERY state.",
                    true);
            this.ctHistory.add(ctHistory);
        }
        CtKeyConstructor nameKey = SpringContext.getBean(CtKeyConstructor.class);
        String keyGen = nameKey.getKeyDefault(type, this);
        super.setKey(keyGen);
        this.setName(keyGen);
    }

    @JsonIgnore
    private void createUndefinedAttributes() throws Exception {
        if (!this.validateAttributes()) {

        }
    }

    @JsonIgnore
    public List<Long> getDependsOn(CtRelationRepo ctRelationRepo) {
        return ctRelationRepo.findByRelatedToId(this.getId()).stream()
                .filter(ctRelation -> ctRelation.getImpactPercent() == 100).map(ct -> ct.getRelatedFrom().getId())
                .collect(Collectors.toList());

    }

    public void addHistory(CtHistory ctHistory) {

        this.getCtHistory().add(ctHistory);
    }

    @JsonIgnore
    @Transient

    public boolean validateAttributes() {
        MonitoringProfileRepo monProRepo = SpringContext.getBean(MonitoringProfileRepo.class);

        Optional<MonitoringProfile> monitoringProfileFound = monProRepo.findById(monitoringProfile.getId());
        if (monitoringProfileFound.isPresent()) {
            Set<Monitor> monitors = monitoringProfileFound.get().getMonitors();

            return monitors.stream().map(m -> m.getMetric().getInstrumentation().getCtSourceParameters())
                    .flatMap(List::stream).distinct().allMatch(isIn(((Set<Attribute>) this.getJoinedAtts()).stream()
                            .map(a -> a.getName()).collect(Collectors.toList())));
        } else {
            return true;
        }

    }


    @Override
    public String toString() {
        return "Ct [ id=" + this.getId() + ", name=" + name + ", environment=" + environment + ", company=" + company
                + ", workgroup=" + workgroup + ", contact=" + contact + ", supportUser=" + supportUser + ", location="
                + location + ", state=" + state + ", attributes=" + this.getAttributes() + "]";
    }

    @JsonIgnore
    public Object getJoinedAtt(String propertyName) {
        Set<Attribute> attValueSetTotal = new HashSet<>();
        attValueSetTotal.addAll(this.getAttributes());
        attValueSetTotal.addAll(this.getInstrumentationParameterValues().stream()
                .map(i -> new Attribute(i.getName(), i.getValue())).collect(Collectors.toList()));

        Optional<Attribute> found = attValueSetTotal.stream()
                .filter(o -> o.getName().trim().equalsIgnoreCase(propertyName.trim())).findFirst();
        if (found.isPresent()) {
            return found.get().getValue();
        } else {
            return "";
        }
    }

    @JsonIgnore
    public Set<Attribute> getJoinedAtts() {
        Set<Attribute> attValueSetTotal = new HashSet<>();
        attValueSetTotal.addAll(this.getAttributes());
        attValueSetTotal.addAll(this.getInstrumentationParameterValues().stream()
                .map(i -> new Attribute(i.getName(), i.getValue())).collect(Collectors.toList()));

        return attValueSetTotal;
    }

    @JsonIgnore
    @Transient
    public boolean validateIp() {
        if (getJoinedAtt("ip").toString().isBlank()) {
            return true;
        }
        boolean isValid = InetAddresses.isInetAddress(getJoinedAtt("ip").toString());


        if (!isValid) {
            return false;
        }
        return true;

    }

    @JsonIgnore
    @Transient
    public boolean validateTimeZone() {
        if (getJoinedAtt("timezone").toString().isBlank()) {
            return true;
        }
        String[] validIDs = TimeZone.getAvailableIDs();
        for (String str : validIDs) {
            if (str != null && str.equals(getJoinedAtt("timezone").toString())) {
                return true;
            }
        }
        return false;

    }

    @JsonIgnore
    @Transient
    public boolean validateEnvironment() {
        for (Environment c : Environment.values()) {
            if (c.name().equals(this.environment)) {
                return true;
            }
        }

        return false;

    }

}
