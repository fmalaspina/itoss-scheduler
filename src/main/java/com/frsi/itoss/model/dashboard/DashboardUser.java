package com.frsi.itoss.model.dashboard;

import com.frsi.itoss.model.user.UserAccount;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@Audited
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {

        @Index(name = "idx_dashboard_user", columnList = "user_account_id,dashboard_id", unique = true),
        @Index(name = "idx_dashboard_user_id", columnList = "user_account_id")
})
public class DashboardUser implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_user_gen")
    @SequenceGenerator(name = "dashboard_user_gen", sequenceName = "dashboard_user_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;


    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<Object, Object> config = new HashMap<>();
    @RestResource(exported = false)
    @ManyToOne
    private UserAccount userAccount;
    @RestResource(exported = false)
    @ManyToOne
    private Dashboard dashboard;
    private boolean defaultDashboard;
    private int location;
}
	