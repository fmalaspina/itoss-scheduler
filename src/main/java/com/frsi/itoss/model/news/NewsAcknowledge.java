package com.frsi.itoss.model.news;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(indexes = {
        @Index(name = "idx_newsacknowledge_user_id", columnList = "userId"),
        @Index(name = "idx_newsacknowledge_status", columnList = "status")
})
public class NewsAcknowledge implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    private Date timestamp;

    private Long userId;


    private String status;

    @RestResource(exported = false)
    @ManyToOne
    @JoinColumn(name = "news_id")
    private News news;


}
