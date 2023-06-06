package com.frsi.itoss.model.task;

import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.profile.Instrumentation;
import com.frsi.itoss.shared.InstrumentationParameterValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Task extends EntityBase implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_gen")
    @SequenceGenerator(name = "task_gen", sequenceName = "task_seq", initialValue = 200000, allocationSize = 1)

    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    private String description;
    private Long timeout;
    // JSON, HTML, CSV, PLAIN_TEXT, XML
    private String format;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Instrumentation instrumentation;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<InstrumentationParameterValue> instrumentationParameterValues;


    public Task() {
        super();
    }

    public Task(String name) {

        this.name = name;
    }


}