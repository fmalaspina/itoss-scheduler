package com.frsi.itoss.model.dynamicsearch;

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

public class DynamicSearch extends EntityBase implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dynamicsearch_gen")
    @SequenceGenerator(name = "dynamicsearch_gen", sequenceName = "dynamicsearch_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private String name;

    private String description;
    private String groupName;
    @Column(unique = true)
    private String endpoint;
    @Enumerated(EnumType.STRING)
    private Source source;

    @Column(columnDefinition = "TEXT")
    private String query;
    @RestResource(exported = false)
    @ManyToOne
    private DynamicSearchType type;


    public DynamicSearch() {
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
        return "DynamicSearch [name=" + name + ",  type=" + type + "]";
    }

}