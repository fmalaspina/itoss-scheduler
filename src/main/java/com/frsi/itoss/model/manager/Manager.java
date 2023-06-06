package com.frsi.itoss.model.manager;


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
@Audited
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)

public class Manager extends EntityBase implements Serializable {
    /**
     *
     */

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manager_gen")
    @SequenceGenerator(name = "manager_gen", sequenceName = "manager_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private String name;
    private String endpoint;
//	@OneToMany(mappedBy = "manager", fetch = FetchType.EAGER)
//	@JsonManagedReference
//	private Set<Analizer> analizers = new HashSet<>();


//	public void addAnalizer(Analizer analizer) {
//		//this.analizers.add(analizer);
//		analizer.setManager(this);
//		
//	}


    @Override
    public String toString() {
        return "Manager [name=" + name + ", endpoint=" + endpoint + "]";
    }


}