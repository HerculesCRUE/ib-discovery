package es.um.asio.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.um.asio.service.util.Utils;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ENTITY_ATTRIBUTE")
@Getter
@ToString(includeFieldNames = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class EntityAttribute {

    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private long id;

    /**
     * EntityAction.
     * Relation Bidirectional ManyToOne
     */
/*    @JsonIgnore
    @EqualsAndHashCode.Include*/
/*    @ManyToOne(fetch = FetchType.LAZY)
    private EntityAction entityAction;*/
    @ManyToOne
    @JoinColumn(name = "entity_action_id", nullable = false)
    private EntityAction entityAction;

    /**
     * key.
     */
    @Column(name = "key_Att", nullable = false,columnDefinition = "VARCHAR(100)")
    private String key;

    /**
     * value.
     */
    @Column(name = "value_att", nullable = false,columnDefinition = "TEXT")
    private String value;

    /**
     * state.
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private AttributeType attributeType;

    public EntityAttribute(/*EntityAction entityAction, */String key, Object value) {
        /*this.entityAction = entityAction;*/
        this.key = key;
        this.value = String.valueOf(value);
        this.attributeType = Utils.getAttributeType(String.valueOf(value));
    }
}
