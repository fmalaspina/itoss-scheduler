package com.frsi.itoss.model.profile;

import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.ct.CtType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class MonitoringProfile extends EntityBase implements Serializable {
    /**
     *
     */

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "monitoringprofile_gen")
    @SequenceGenerator(name = "monitoringprofile_gen", sequenceName = "monitoringprofile_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;


    @RestResource(exported = false)
    @ManyToOne
    private CtType ctType;


    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Monitor> monitors = new HashSet<Monitor>();


    public MonitoringProfile() {
        super();
    }

    public void addMonitor(Monitor monitor) {
        this.monitors.add(monitor);


    }

    public void delMonitor(Monitor monitor) {
        this.monitors.remove(monitor);


    }

    @Override
    public String toString() {
        return "MonitoringProfile [name=" + name + ", ctType=" + ctType.getName() + ", monitors=" + monitors + "]";
    }


}