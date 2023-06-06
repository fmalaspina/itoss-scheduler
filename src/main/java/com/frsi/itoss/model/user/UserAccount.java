package com.frsi.itoss.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.SpringContext;
import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.baseclasses.KeyConstructor;
import com.frsi.itoss.model.company.Company;
import com.frsi.itoss.model.repository.RoleRepo;
import com.frsi.itoss.model.repository.UserAccountRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@AllArgsConstructor

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Audited
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"username"}), indexes = {
        @Index(name = "idx_user_account_manager_id", columnList = "manager_id"),
        @Index(name = "idx_user_account_company_id", columnList = "company_id")
})
public class UserAccount extends EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    public UserAccount manager;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "useraccount_gen")
    @SequenceGenerator(name = "useraccount_gen", sequenceName = "useraccount_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(nullable = false)
    private String name;
    @RestResource(exported = false)
    @ManyToOne
    private UserAccountType type;
    @RestResource(exported = false)
    @ManyToOne
    private Company company;


    private Boolean ldapAuthentication = false;


    //	@OneToMany
//	public Set<Workgroup> workgroups = new HashSet<Workgroup>();
    private String mobile;
    @JsonIgnore
    private String password;
    private String email;
    @Column(name = "username", unique = true)
    private String username;
    private UserStatus status;
    private boolean changeOnNextLogin = true;

    public UserAccount() {
        super();
    }

    public UserAccount(String name) {

        this.name = name;
    }
    // @JsonManagedReference

    // @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    // private List<Role> roles = new ArrayList<Role>();

    @JsonIgnore
    public List<Role> getRoles() {

        RoleRepo roleRepo = SpringContext.getBean(RoleRepo.class);
        List<Role> roles = roleRepo.findByUsersId(this.id);

        return roles;
    }

    @JsonIgnore
    public void setRoles(List<Role> requestedRoles) {

        RoleRepo roleRepo = SpringContext.getBean(RoleRepo.class);
        List<Role> actualRoles = roleRepo.findByUsersId(this.id);
        Set<Long> actualRolesIds = actualRoles.stream().map(Role::getId).collect(Collectors.toSet());
        Set<Long> requestedRolesIds = requestedRoles.stream().map(Role::getId).collect(Collectors.toSet());

        List<Role> newRoles = requestedRoles.stream().filter(role -> !actualRolesIds.contains(role.getId()))
                .collect(Collectors.toList());
        List<Role> orphanRoles = actualRoles.stream().filter(role -> !requestedRolesIds.contains(role.getId()))
                .collect(Collectors.toList());
        roleRepo.deleteAll(orphanRoles);
        roleRepo.saveAll(newRoles);
    }


    @PrePersist
    @PreUpdate

    private void setKeyDefault() throws Exception {
        this.username = this.username.strip();
        this.name = this.name.strip();
        final KeyConstructor nameKey = new KeyConstructor(this.type, this);

        super.setKey(nameKey.getKey());
        PasswordEncoder encoder = SpringContext.getBean(PasswordEncoder.class);
        if (ldapAuthentication != null && ldapAuthentication) {
            this.password = encoder.encode("itoss");
        }
        if (this.id != null && this.id != 0L && this.manager != null && this.manager.id.equals(this.id)) {
            throw new IllegalArgumentException("User cannot be his own manager");
        }
    }

    @PreRemove
    @JsonIgnore
    private void deleteContainer() {
        this.manager = null;
        this.company = null;

        UserAccountRepo userAccountRepo = SpringContext.getBean(UserAccountRepo.class);
        userAccountRepo.deleteByUserId(this.getId());

    }
}