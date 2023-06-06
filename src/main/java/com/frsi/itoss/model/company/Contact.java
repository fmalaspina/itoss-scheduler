package com.frsi.itoss.model.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Audited

public class Contact extends EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_gen")
    @SequenceGenerator(name = "contact_gen", sequenceName = "contact_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private String name;

    @RestResource(exported = false)
    @ManyToOne
    private ContactType type;
    @RestResource(exported = false)
    @ManyToOne
    private Company company;
    private String email;
    @JsonIgnore
    private String password;
    private ContactStatus status;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean changeOnNextLogin = true;


    public Contact() {
        super();
    }

    public Contact(String name) {

        this.name = name;
    }

    @PrePersist
    @PreUpdate
    @PostLoad
    private void setKeyDefault() throws Exception {


        final KeyConstructor nameKey = new KeyConstructor(this.type, this);

        super.setKey(nameKey.getKey());
    }

}