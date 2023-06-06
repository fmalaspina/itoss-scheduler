package com.frsi.itoss.model.dashboard;


import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.baseclasses.KeyConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.io.Serializable;

@Log
@Data
@AllArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Audited

public class Container extends EntityBase implements Serializable {
    /**
     *
     */

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "container_gen")
    @SequenceGenerator(name = "container_gen", sequenceName = "container_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @RestResource(exported = false)
    @ManyToOne
    private ContainerType type;
    private String tooltip;
    private String name;

    private String label;
//	@RestResource(exported = false)
//	@ManyToOne(fetch = FetchType.EAGER)
//	private Metric metric;

    public Container() {
        super();
    }

    //	public void addDashboardEntry(DashboardEntry de) {
//		this.dashboardEntries.add(de);
//	}
//
//	public void deleteDashboardEntry(DashboardEntry de) {
//		this.dashboardEntries.remove(de);
//	}
//	@JsonProperty("totalEntries")
//	public Long entries() {
//		return this.dashboardEntries.stream().count();
//	}
//	@JsonProperty("entriesBySeverity")
//	public Map<String, Long> entriesBySeverity() {
//		return this.dashboardEntries.stream().map(DashboardEntry::getSeverity)
//				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//	}
//	@JsonProperty("metricFaults")
//	public Long metricFaults() {
//		return this.dashboardEntries.stream().map(DashboardEntry::getMetricPayload).filter(mf -> mf.isFault())
//				.count();
//	}
//	
//	
    @PrePersist
    @PreUpdate
    @PostLoad
    private void setKeyDefault() throws Exception {


        final KeyConstructor nameKey = new KeyConstructor(this.type, this);

        super.setKey(nameKey.getKey());
    }
}