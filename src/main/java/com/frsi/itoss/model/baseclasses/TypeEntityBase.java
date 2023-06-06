package com.frsi.itoss.model.baseclasses;

import com.frsi.itoss.shared.TypeAttribute;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Data
@EqualsAndHashCode
@AllArgsConstructor
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Audited
public abstract class TypeEntityBase extends Auditable<String> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private List<TypeAttribute> typeAttributes = new ArrayList<TypeAttribute>();


    public TypeEntityBase() {
        //this.id = UUID.randomUUID();

    }


}