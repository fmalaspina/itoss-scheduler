package com.frsi.itoss.model.tennant;

import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.ct.Ct;
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
@Table(indexes = {

        @Index(name = "idx_tennant_name", columnList = "name", unique = true)


})
public class Tennant extends EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @RestResource(exported = true)
    @ManyToMany(targetEntity = UserAccount.class, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "tennant_users", joinColumns = @JoinColumn(name = "tennant_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"tennant_id", "users_id"})},
            indexes = {
                    @Index(name = "idx_tennant_user_tennant_id_users_id", columnList = "tennant_id,users_id"),
                    @Index(name = "idx_tennant_user_users_id", columnList = "users_id"),
                    @Index(name = "idx_tennant_user_tennant_id", columnList = "tennant_id")

            })

    Set<UserAccount> users = new HashSet<UserAccount>();
    @RestResource(exported = true)
    @ManyToMany(targetEntity = Ct.class, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "tennant_cts",
            joinColumns = {@JoinColumn(name = "tennant_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "cts_id", referencedColumnName = "id")},
            uniqueConstraints = {@UniqueConstraint(columnNames = {"tennant_id", "cts_id"})},
            indexes = {
                    @Index(name = "idx_tennant_cts_tennant_id_cts_id", columnList = "tennant_id,cts_id"),

                    @Index(name = "idx_tennant_cts_cts_id", columnList = "cts_id"),
                    @Index(name = "idx_tennant_cts_tennant_id", columnList = "tennant_id")

            })

    Set<Ct> cts = new HashSet<Ct>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tennant_gen")
    @SequenceGenerator(name = "tennant_gen", sequenceName = "tennant_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(name = "name", unique = true)

    private String name;
    private String description;

    public Tennant() {
        super();
    }

}