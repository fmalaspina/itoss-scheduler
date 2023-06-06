package com.frsi.itoss.model.news;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),

        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class News implements Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    private Long ctId;
    private String title;
    private String type;
    private Date timestamp;
    @Type(type = "string-array")
    @Column(columnDefinition = "text[]")
    private String[] destinations;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.EAGER)
    private HashMap<String, String> payload = new HashMap<>();
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.EAGER)
    private List<String> tags = new ArrayList<>();


//	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//	@JoinColumn(name = "news_id")
//	
//	private List<NewsAcknowledge> acknowledges = new ArrayList<>();

}
