package com.frsi.itoss.model.profile;

import com.frsi.itoss.shared.InstrumentationParameter;
import com.frsi.itoss.shared.MappingModes;
import com.frsi.itoss.shared.Source;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity

@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Audited
public class Instrumentation implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    public String name;

    public String description;
    @Enumerated(EnumType.STRING)
    public MappingModes mappingMode;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<InstrumentationParameter> instrumentationParameters = new ArrayList<InstrumentationParameter>();

    public List<InstrumentationParameter> getCtSourceParameters() {
        return this.instrumentationParameters.stream().filter(p -> p.getSource().equals(Source.CT)).collect(Collectors.toList());
    }

    public List<InstrumentationParameter> getMonitorSourceParameters() {
        return this.instrumentationParameters.stream().filter(p -> p.getSource().equals(Source.MONITOR)).collect(Collectors.toList());
    }

    public List<InstrumentationParameter> getMetricSourceParameters() {
        return this.instrumentationParameters.stream().filter(p -> p.getSource().equals(Source.METRIC)).collect(Collectors.toList());
    }

    public String getName() {
        return this.name;
    }


}
