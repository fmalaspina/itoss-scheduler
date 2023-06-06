package com.frsi.itoss.model.ct;

import com.frsi.itoss.model.baseclasses.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited

public class CtRelation extends EntityBase implements Serializable {
    /**
     *
     */

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ctrelation_gen")
    @SequenceGenerator(name = "ctrelation_gen", sequenceName = "ctrelation_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;


    private String relation;// IMPACTS
    private int impactPercent;// 100
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Ct relatedFrom;
    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Ct relatedTo;
//	@RestResource(exported = false)
//	@ManyToOne(fetch = FetchType.EAGER)
//	private Ct relatedTo;

}
