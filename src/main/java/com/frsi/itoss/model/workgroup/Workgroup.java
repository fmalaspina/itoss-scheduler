package com.frsi.itoss.model.workgroup;

import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.baseclasses.KeyConstructor;
import com.frsi.itoss.model.user.UserAccount;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Audited
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Workgroup extends EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManyToMany
    Set<UserAccount> userAccounts = new HashSet<UserAccount>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workgroup_gen")
    @SequenceGenerator(name = "workgroup_gen", sequenceName = "workgroup_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    private String email;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Workgroup parent;
    @RestResource(exported = false)
    @ManyToOne
    private UserAccount workgroupManager;
    @RestResource(exported = false)
    @ManyToOne
    private WorkgroupType type;


    public Workgroup() {
        super();
    }

    @PrePersist
    @PreUpdate
    @PostLoad
    private void setKeyDefault() throws Exception {


        final KeyConstructor nameKey = new KeyConstructor(this.type, this);

        super.setKey(nameKey.getKey());
    }

}