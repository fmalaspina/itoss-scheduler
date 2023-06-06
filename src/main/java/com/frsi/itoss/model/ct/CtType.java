package com.frsi.itoss.model.ct;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.frsi.itoss.model.SpringContext;
import com.frsi.itoss.model.baseclasses.TypeEntityBase;
import com.frsi.itoss.model.profile.Instrumentation;
import com.frsi.itoss.model.repository.CtTypeRepo;
import com.frsi.itoss.shared.DataType;
import com.frsi.itoss.shared.TypeAttribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@AllArgsConstructor
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
////@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})

public class CtType extends TypeEntityBase implements Serializable {


    private static final long serialVersionUID = 1L;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    public CtType parent;
    @JsonBackReference
    @OneToMany(mappedBy = "parent")
    public List<CtType> childs = new ArrayList<CtType>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cttype_gen")
    @SequenceGenerator(name = "cttype_gen", sequenceName = "cttype_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @NotNull(message = "Name cannot be null")
    private String name;
    private String typePath;
    @RestResource(exported = true)
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "ct_type_instrumentation", joinColumns = @JoinColumn(name = "ct_type_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "instrumentation_name", referencedColumnName = "name"))
    private Set<Instrumentation> instrumentations = new HashSet<Instrumentation>();

    public CtType() {
        super();
    }


    public CtType(String name) {

        this.name = name;
    }

    @JsonIgnore
    public DataType getDataType(String attributeName) {

        CtTypeRepo ctTypeRepo = SpringContext.getBean(CtTypeRepo.class);
        Optional<CtType> optionalCtType = ctTypeRepo.findById(this.id);
        CtType ctType = optionalCtType.get();
        Set<TypeAttribute> attributes = ctType.getInstrumentations().stream().flatMap(i -> i.getCtSourceParameters().stream())
                .map(i -> {
                    TypeAttribute t = new TypeAttribute();
                    t.setType(i.getType());
                    t.setName(i.getName());
                    return t;
                }).collect(Collectors.toSet());
        attributes.addAll(this.getTypeAttributes());

        return attributes.stream().filter(a -> a.getName().equals(attributeName)).findFirst().get().getType();
    }

    @JsonProperty("typePath")
    public String getPath() {
        StringBuilder sb = new StringBuilder();
        CtType parentNode;
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

    @Override
    public String toString() {
        return "CtType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", typePath='" + typePath + '\'' +
                ", instrumentations=" + instrumentations +
                '}';
    }
}