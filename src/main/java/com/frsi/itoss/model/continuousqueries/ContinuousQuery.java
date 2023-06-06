package com.frsi.itoss.model.continuousqueries;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.SpringContext;
import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.profile.Metric;
import com.frsi.itoss.model.repository.MetricDataRepo;
import com.frsi.itoss.shared.DataType;
import com.frsi.itoss.shared.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ContinuousQuery extends EntityBase implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "continuousquery_gen")
    @SequenceGenerator(name = "continuousquery_gen", sequenceName = "continuousquery_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Metric metric;
    private String functions;
    private String granularity;


    @PrePersist
    private void setNameDefault() throws Exception {

        this.name = this.metric.getName() + "_" + this.getGranularity();

    }

    @JsonIgnore
    public String getFormatedFunctionsForInflux() {
        StringBuilder sb = new StringBuilder();
        List<String> functions = Arrays.asList(this.getFunctions().split(","));
        int len = functions.size();
        int count = 1;
        for (String f : functions) {
            sb.append(f + "(*)");
            if (count < len) {
                sb.append(",");
                count++;
            }

        }
        return sb.toString();

    }

    @JsonIgnore
    public String getFormatedFunctionsForTimescale() {
        StringBuilder sb = new StringBuilder();
        List<String> functions = Arrays.asList(this.getFunctions().split(","));
        int lenFunctions = functions.size();
        int lenFields = this.getMetric().getFieldsForSaveInPhase().collect(Collectors.toList()).size();
        int countFunctions = 1;

        for (String function : functions) {
            int countFields = 1;
            String functionPost = "";
            function = function.toLowerCase().strip();
            String functionAS = function;
            if (function.equals("median")) {
                function = "approx_percentile(";
                functionPost = ", 0.50";
                functionAS = "median";
            } else {
                if (function.equals("mean")) {
                    functionAS = "mean";
                    function = "avg(";
                } else {
                    functionAS = function;
                    function = function + "(";
                }
            }

            for (Field field : this.getMetric().getFieldsForSaveInPhase().collect(Collectors.toList())) {
                String fieldName = field.getName().toLowerCase();
                boolean isAllowAnyType = (function.equals("last(") || function.equals("first("));
                boolean isAllowOnlyNumbers = !isAllowAnyType;
                boolean isNumber = (field.getType() == DataType.NUMBER || field.getType() == DataType.FLOAT);

                if (isAllowAnyType || (isAllowOnlyNumbers && isNumber)) {
                    String addTime = (function.equals("first(") || function.equals("last(")) ? ",time" : "";
                    sb.append(function + "\"" + fieldName + "\"" + functionPost + addTime + ") as " + functionAS + "_" + fieldName);
                    if (countFields < lenFields) {
                        sb.append(",");
                    }
                }
                if (countFields < lenFields) {
                    countFields++;
                }
            }

            if (countFunctions < lenFunctions) {
                sb.append(",");
                countFunctions++;
            }
        }
        return sb.toString();

    }

    @JsonIgnore
    public String getBucket() {
        MetricDefaults metricDefaults = SpringContext.getBean(MetricDefaults.class);
        return metricDefaults.getGranularities().stream()
                .filter(g -> g.getId().equals(this.granularity))
                .map(g -> g.getBucket()).findFirst().get();
    }

    @JsonIgnore
    public String getDatabase() {
        MetricDataRepo metricDataRepo = SpringContext.getBean(MetricDataRepo.class);
        String database = metricDataRepo.getDatabaseName();
        return database;

    }

    @PostRemove
    private void removeQuery() throws Exception {
        MetricDataRepo metricDataRepo = SpringContext.getBean(MetricDataRepo.class);
        metricDataRepo.deleteContinuousQuery(this.name);


    }

    @PostUpdate
    private void updateName() throws Exception {
        this.setNameDefault();


    }

    @PostPersist
    private void createQuery() throws Exception {
        this.setNameDefault();
        MetricDataRepo metricDataRepo = SpringContext.getBean(MetricDataRepo.class);


        metricDataRepo.createContinuousQuery(this);

    }

}
