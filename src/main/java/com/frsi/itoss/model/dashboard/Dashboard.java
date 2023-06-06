package com.frsi.itoss.model.dashboard;


import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.baseclasses.KeyConstructor;
import com.frsi.itoss.model.user.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Log
@Data
@AllArgsConstructor
@Entity
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)

public class Dashboard extends EntityBase implements Serializable {
    /**
     *
     */

    private static final long serialVersionUID = 1L;
    @ManyToMany
    List<Container> containers = new ArrayList<Container>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_gen")
    @SequenceGenerator(name = "dashboard_gen", sequenceName = "dashboard_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @RestResource(exported = false)
    @ManyToOne
    private DashboardType type;
    private Long parentId;
    private String tooltip;
    private String label;
    private String name;
    private String description;
    private int location;
    private String icon;
    @RestResource(exported = false)
    @ManyToOne
    private UserAccount userAccount;
    private boolean defaultDashboard;


    public Dashboard() {
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