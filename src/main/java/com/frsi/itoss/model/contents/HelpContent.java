package com.frsi.itoss.model.contents;

import com.frsi.itoss.model.baseclasses.EntityBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Audited
////@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"key"}), indexes = {
        @Index(name = "IDX_HELPCONTENT", columnList = "key")})
public class HelpContent extends EntityBase implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "helpcontent_gen")
    @SequenceGenerator(name = "helpcontent_gen", sequenceName = "helpcontent_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "key", unique = true)
    private String key;
    private String name;
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean root = false;
    @Column(columnDefinition = "TEXT")
    private String helpContent;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<HelpContent> helpContents = new HashSet<HelpContent>();


}