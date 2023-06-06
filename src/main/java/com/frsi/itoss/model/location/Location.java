package com.frsi.itoss.model.location;

import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.baseclasses.KeyConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Audited
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)

public class Location extends EntityBase implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_gen")
    @SequenceGenerator(name = "location_gen", sequenceName = "location_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private String name;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Location parent;
    @RestResource(exported = false)
    @ManyToOne
    private LocationType type;


    public Location() {
        super();
    }


//	@OneToMany(fetch = FetchType.EAGER)
//	public Set<Ct> cts = new HashSet<Ct>();

//	@OneToMany(fetch = FetchType.EAGER)
//	public Set<Organization> organizations = new HashSet<Organization>();

    @PrePersist
    @PreUpdate
    @PostLoad
    private void setKeyDefault() throws Exception {


        final KeyConstructor nameKey = new KeyConstructor(this.type, this);

        super.setKey(nameKey.getKey());
    }

    @Override
    public String toString() {
        return "Location [name=" + name + ", parent=" + parent + ", type=" + type + "]";
    }


}