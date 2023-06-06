package com.frsi.itoss.model.ldap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.baseclasses.EntityBase;
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
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Ldap extends EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @RestResource(exported = false)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ldap_gen")
    @SequenceGenerator(name = "ldap_gen", sequenceName = "ldap_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    @JsonIgnore
    private String oldPassword;
    private String ldapUrl;
    private String username;
    private String password;
    private String base;

    public Ldap() {
        super();
    }


}