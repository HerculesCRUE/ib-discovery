package es.um.asio.service.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "ENTITY_ACTION")
@Getter
@ToString(includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class EntityAction {

    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private long id;

    /**
     * TripleStore.
     * Relation Bidirectional ManyToOne
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private TripleStore tripleStore;

    /**
     * uri.
     */
    @Column(name = "uri", nullable = true,columnDefinition = "VARCHAR(400)")
    private String uri;

    /**
     * class.
     */
    @Column(name = "class", nullable = true,columnDefinition = "VARCHAR(100)")
    private String className;

    /**
     * action.
     */
    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private BasicAction basicAction;

    /**
     * state.
     */
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EntityState entityState;

    /**
     * state.
     */
    @Column(name = "actionDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDate;

    /**
     * Relation Bidirectional CanonicalURILanguage OneToMany
     */
/*    @OneToMany(mappedBy = "id" , cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityAttribute> entityAttributes;*/
    @OneToMany(mappedBy = "entityAction")
    private Set<EntityAttribute> entityAttributes;

    public EntityAction(TripleObject tripleObject, BasicAction basicAction) {
        this.tripleStore = tripleObject.getTripleStore();
        this.uri = tripleObject.getId();
        this.className = tripleObject.getClassName();
        this.basicAction = basicAction;
        this.entityState = EntityState.PENDING;
        this.actionDate = new Date();
        this.entityAttributes = new HashSet<>();
        for (Map.Entry<String, Object> attEntry: tripleObject.getAttributes().entrySet()) {
            EntityAttribute entityAttribute = new EntityAttribute(attEntry.getKey(),attEntry.getValue());
            entityAttributes.add(entityAttribute);
        }
    }
}
