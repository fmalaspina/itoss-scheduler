package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor


public class CtStatus implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;

    private boolean down;
    private Date lastStatusChange;
    private Date createdAt;
    private Date modifiedAt;

    @PrePersist
    void setCreatedAt() {
        if (this.createdAt == null) this.createdAt = new Date();
    }

    @PreUpdate
    void setModifiedAt() {
        if (this.createdAt == null) this.createdAt = new Date();
        this.modifiedAt = new Date();
    }


}
	
