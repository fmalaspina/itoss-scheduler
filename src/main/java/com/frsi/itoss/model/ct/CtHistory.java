package com.frsi.itoss.model.ct;

import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.model.statemachine.CtEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@Audited
@Table(name="ct_history",schema="public",indexes = {

        @Index(name = "idx_ct_history_event", columnList = "event"),
        @Index(name = "idx_ct_history_user_account", columnList = "userAccountId"),
        @Index(name = "idx_ct_history_successful", columnList = "successful")

})
public class CtHistory extends EntityBase implements Serializable {


    /**
     *
     */

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cthistory_gen")
    @SequenceGenerator(name = "cthistory_gen", sequenceName = "cthistory_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private Date timestamp;
    private Long userAccountId;

    @Enumerated(EnumType.STRING)
    private CtEvent event;
    private String notes;
    private boolean successful;


    public CtHistory() {
        super();
    }


    public CtHistory(Date timestamp, Long userAccountId, CtEvent event, String notes, boolean successful) {
        this();


        this.timestamp = timestamp;
        this.userAccountId = userAccountId;
        this.event = event;
        this.notes = notes;
        this.successful = successful;
    }


}
