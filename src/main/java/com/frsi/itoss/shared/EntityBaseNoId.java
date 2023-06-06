package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.Date;

//@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public abstract class EntityBaseNoId implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Date createdAt;
    private Date modifiedAt;

    @PrePersist
    void setCreatedAt() {
        if (this.createdAt == null) this.createdAt = new Date();
    }

    @PreUpdate
    void setModifiedAt() {
        this.modifiedAt = new Date();
    }


}