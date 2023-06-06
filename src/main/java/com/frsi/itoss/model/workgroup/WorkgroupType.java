package com.frsi.itoss.model.workgroup;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.frsi.itoss.model.baseclasses.TypeEntityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

@Entity
@Data
@AllArgsConstructor
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
////@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})

public class WorkgroupType extends TypeEntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    public WorkgroupType parent;
    @JsonBackReference
    @OneToMany(mappedBy = "parent")
    public Set<WorkgroupType> childs = new HashSet<WorkgroupType>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workgrouptype_gen")
    @SequenceGenerator(name = "workgrouptype_gen", sequenceName = "workgrouptype_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @NotNull(message = "Name cannot be null")
    private String name;

    public WorkgroupType() {
        super();
    }


    public WorkgroupType(String name) {

        this.name = name;
    }

    @JsonProperty("typePath")
    public String getPath() {
        StringBuilder sb = new StringBuilder();
        WorkgroupType parentNode;
        List<String> path = new ArrayList<>();

        parentNode = this.parent;

        while (parentNode != null) {
            path.add(parentNode.getName());
            parentNode = parentNode.getParent();

        }
        if (path.size() > 0) {
            Collections.reverse(path);
            path.forEach(n -> {
                sb.append("/");
                sb.append(n);
            });

        }
        sb.append("/");

        sb.append(this.name);
        return sb.toString();


    }
}