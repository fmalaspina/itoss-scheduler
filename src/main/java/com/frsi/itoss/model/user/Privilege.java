package com.frsi.itoss.model.user;

import com.frsi.itoss.model.baseclasses.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Audited

public class Privilege extends EntityBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "privilege_gen")
    @SequenceGenerator(name = "privilege_gen", sequenceName = "privilege_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private String name;

    private String description;
    private String groupName;

    public Privilege(String name, String description, String groupName) {
        this.description = description;
        this.groupName = groupName;
        this.name = name;
    }

//	@ManyToMany(mappedBy = "privileges")
//	private Collection<Role> roles;
}