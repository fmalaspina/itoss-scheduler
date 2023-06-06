package com.frsi.itoss.model.baseclasses;

import com.frsi.itoss.shared.Attribute;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@MappedSuperclass
@Data
@EqualsAndHashCode
@AllArgsConstructor
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Audited
@EntityListeners(ValidationListener.class)
public abstract class EntityBase extends Auditable<String> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String key;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.EAGER)
    private List<Attribute> attributes = new ArrayList<Attribute>();

    public EntityBase() {
        super();

    }

    public String getStringValue(String propertyName) {
        return (String) getObject(propertyName);
    }

    public int getIntValue(String propertyName) {
        // TODO Auto-generated method stub
        return (int) getObject(propertyName);
    }

    public Object getObject(String propertyName) {


        Optional<Attribute> found = this.attributes.stream()
                .filter(o -> o.getName().trim().equalsIgnoreCase(propertyName.trim())).findFirst();
        if (found.isPresent()) {
            return found.get().getValue();
        } else {
            return "";
        }
    }


}