package com.frsi.itoss.model.tool;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.profile.Metric;
import com.frsi.itoss.shared.InstrumentationParameterValue;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})

public class Tool extends EntityBase implements Serializable {
    /**
     *
     */


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tool_gen")
    @SequenceGenerator(name = "tool_gen", sequenceName = "tool_seq", initialValue = 200000, allocationSize = 1)

    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private String name;

    @RestResource(exported = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Metric metric;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<InstrumentationParameterValue> instrumentationParameterValues = new ArrayList<>();
    @Column(columnDefinition = "TEXT")
    private String template;

    @JsonIgnore
    public Optional<Object> getTimeout() {
        Optional<InstrumentationParameterValue> optionalTimeout = this.instrumentationParameterValues.stream()
                .filter(i -> i.getName().equals("timeout")).findFirst();
        if (optionalTimeout.isPresent()) {
            return Optional.of(optionalTimeout.get().getValue());

        } else {
            return Optional.of(5000L);
        }

    }
}
