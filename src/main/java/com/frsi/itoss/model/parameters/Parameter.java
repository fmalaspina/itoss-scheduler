package com.frsi.itoss.model.parameters;

import com.frsi.itoss.mgr.services.ParameterListener;
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
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@Audited
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@NoArgsConstructor
@EntityListeners(value = ParameterListener.class)
public class Parameter implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String type;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<Object, Object> parameters = new HashMap<>();
    @Column(length = 5000, columnDefinition = "TEXT")
    private String textValue;

}