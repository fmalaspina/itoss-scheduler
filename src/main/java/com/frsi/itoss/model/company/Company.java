package com.frsi.itoss.model.company;

import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.baseclasses.KeyConstructor;
import com.frsi.itoss.model.location.Location;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}), indexes = {
        @Index(name = "idx_company_location_id", columnList = "location_id"),
        @Index(name = "idx_company_name", columnList = "name", unique = true),
        @Index(name = "idx_company_integration_id", columnList = "integrationId", unique = true)
})
public class Company extends EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @RestResource(exported = false)
    @ManyToOne
    public Company parent;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_gen")
    @SequenceGenerator(name = "company_gen", sequenceName = "company_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private String name;
    private String integrationId;
    @RestResource(exported = false)
    @ManyToOne
    private Location location;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private CompanyType type;

    public Company() {
        super();
    }

    @PrePersist
    @PreUpdate
    @PostLoad
    private void setKeyDefault() throws Exception {
//		if (this.type != null) {
//
//			StringBuilder sbName = new StringBuilder();
//			StringBuilder sbKey = new StringBuilder();
//
//			this.type.getTypeAttributes().stream().filter(a -> a.isNamePart() || a.isIdPart()).forEachOrdered(a -> {
//
//				if (a.isNamePart()) {
//
//					if (!sbName.toString().isEmpty()) {
//						sbName.append("/");
//					}
//					sbName.append(this.getObject(a.getName().toString()));
//				} else {
//					if (!sbKey.toString().isEmpty()) {
//						sbKey.append("/");
//					}
//					sbKey.append(this.getObject(a.getName().toString()));
//				}
//
//			});
//			this.name = sbName.toString();
//			this.key = sbKey.toString();
//		} else {
//			this.name = "/";
//			this.key = "/";
//		}

        final KeyConstructor nameKey = new KeyConstructor(this.type, this);

        super.setKey(nameKey.getKey());
    }

}