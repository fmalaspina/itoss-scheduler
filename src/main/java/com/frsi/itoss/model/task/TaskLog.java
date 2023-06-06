package com.frsi.itoss.model.task;

import com.frsi.itoss.model.baseclasses.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Table(indexes = {

        @Index(name = "idx_ct_id", columnList = "ctId"),
        @Index(name = "idx_task_id", columnList = "taskId"),

})
public class TaskLog extends EntityBase implements Serializable {


    /**
     *
     */

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasklog_gen")
    @SequenceGenerator(name = "tasklog_gen", sequenceName = "tasklog_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private Date timestamp;
    private Long ctId;
    private Long taskId;
    private String taskName;

    @Column(name = "output", columnDefinition = "TEXT")
    private String output;
    private String format;
    private String status;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> payload;
}
