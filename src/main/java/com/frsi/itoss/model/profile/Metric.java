package com.frsi.itoss.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.SpringContext;
import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.ct.CtType;
import com.frsi.itoss.model.repository.TimeScaleDBMetricDataRepoImpl;
import com.frsi.itoss.shared.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Data
@AllArgsConstructor
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Metric extends EntityBase implements Serializable {


    private static final long serialVersionUID = 1L;
    Boolean isConsistentWithTS = false;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metric_gen")
    @SequenceGenerator(name = "metric_gen", sequenceName = "metric_seq", initialValue = 200000, allocationSize = 1)

    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    private MetricCategory metricCategory;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Instrumentation instrumentation;


    private Integer retentionDays = 120;
    //private boolean statusMetric;
    //private boolean uptimeMetric;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<InstrumentationParameterValue> instrumentationParameterValues = new ArrayList<>();

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private MetricPayloadAttributes metricPayloadAttributes = new MetricPayloadAttributes();

    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private CtType ctType;
    @Enumerated(EnumType.STRING)
    private ProcessingPhase saveInPhase = ProcessingPhase.Detail;

    public Metric() {
        super();
    }

    public Metric(String name) {

        this.name = name;
    }


    public boolean getStatusMetric() {
        return this.metricCategory == MetricCategory.Status;
    }

    @JsonIgnore
    public Stream<Tag> getTagsForSaveInPhase() {
//        if (metricCategory == MetricCategory.Availability) {
//            Optional<Tag> boottime = getMetricPayloadAttributes().getBootTimeField();
//            if (boottime.isPresent()) {
//                getMetricPayloadAttributes().getTags().removeIf(t -> t.getInternalField() == InternalField.BOOTTIME);
//            }
//        }

        Stream<Tag> tagStream = getMetricPayloadAttributes().getTags().stream().filter(t -> t.getPhase() == getSaveInPhase());
        return tagStream;
    }


    @JsonIgnore
    public Stream<Field> getFieldsForSaveInPhase() {
        //if (metricCategory == MetricCategory.Availability) addAvailabilityFields();

        Stream<Field> fieldStream = getMetricPayloadAttributes().getFields().stream().filter(f -> f.getPhase() == getSaveInPhase());
        return fieldStream;
    }

//    @JsonIgnore
//    public Stream<Field> getFieldsForSaveInPhaseAndCalculables() {
//        if (metricCategory == MetricCategory.Availability) addAvailabilityFields();
//
//        Stream<Field> fieldStream = getMetricPayloadAttributes().getFields().stream().filter(f -> f.getPhase() == getSaveInPhase());
//        return fieldStream;
//    }

    /**
     * Adds availability internal fields
     */
    @JsonIgnore
    private void addAvailabilityFields() {

//        Field itoss_partial_uptime_seconds = new Field();
        Field itoss_uptime_seconds = new Field();
//        Tag itoss_boottime = new Tag();
//        itoss_partial_uptime_seconds.setPhase(ProcessingPhase.Aggregate);

//        itoss_partial_uptime_seconds.setType(DataType.NUMBER);
//        itoss_partial_uptime_seconds.setName("itoss_partial_uptime_seconds");
//        itoss_partial_uptime_seconds.setUnit("seconds");
        itoss_uptime_seconds.setPhase(ProcessingPhase.Detail);
        itoss_uptime_seconds.setInternalField(InternalField.BOOTTIME);
        itoss_uptime_seconds.setType(DataType.NUMBER);
        itoss_uptime_seconds.setName("uptime");
        itoss_uptime_seconds.setUnit("seconds");
//        itoss_boottime.setPhase(ProcessingPhase.Aggregate);
//        itoss_boottime.setType(DataType.TIME);
//        itoss_boottime.setName("itoss_boottime");
        //itoss_boottime.setUnit("timestamp");
//        if (this.getMetricPayloadAttributes().getFields().stream().noneMatch(f -> f.getName().equals("itoss_partial_uptime_seconds"))) {
//            this.getMetricPayloadAttributes().getFields().add(itoss_partial_uptime_seconds);
//        }
        if (this.getMetricPayloadAttributes().getFields() == null || this.getMetricPayloadAttributes().getFields().stream().noneMatch(f -> f.getName().equals("uptime"))) {
            this.getMetricPayloadAttributes().getFields().add(itoss_uptime_seconds);
        }
//        if (this.getMetricPayloadAttributes().getTags().stream().noneMatch(f -> f.getName().equals("itoss_boottime"))) {
//            this.getMetricPayloadAttributes().getTags().add(itoss_boottime);
//        }

    }

    @JsonIgnore
    public List<Field> getFieldsAndVirtualFields() {
        List<Field> fields = this.getFieldsForSaveInPhase().collect(Collectors.toList());
        var optVirtualFields = this.getAttributes().stream().filter(a -> a.getName().toLowerCase().equals("virtualfields")).findFirst();

        if (optVirtualFields.isPresent()) {
            var attMapList = (List<Map<?, ?>>) optVirtualFields.get().getValue();
            var vFields = attMapList.stream().map(
                    a -> {

                        Field f = new Field();
                        f.setName((String) a.get("name"));
                        f.setOrdinal(Integer.valueOf(String.valueOf(a.get("ordinal"))));
                        f.setPhase(ProcessingPhase.valueOf((String) a.get("phase")));
                        f.setType(DataType.valueOf((String) a.get("type")));
                        return f;
                    }).collect(Collectors.toList());
            fields.addAll(vFields);
        }
        return fields;
    }


    @PreUpdate
    @PrePersist
    @Transactional
    @Modifying
    @JsonIgnore
    public void checkCreateTSTable() throws SQLException {

        if (this.metricCategory == MetricCategory.Availability) {
            this.addAvailabilityFields();
        }
        if (this.id != null && this.id != 0) {
            try {

                TimeScaleDBMetricDataRepoImpl metricRepo = SpringContext.getBean(TimeScaleDBMetricDataRepoImpl.class);
                this.isConsistentWithTS = metricRepo.isConsistentWithTS(this);
            } catch (Exception e) {
                this.isConsistentWithTS = true;
            }
        }
    }
}