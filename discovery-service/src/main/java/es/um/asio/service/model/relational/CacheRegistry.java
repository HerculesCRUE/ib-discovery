package es.um.asio.service.model.relational;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

import static es.um.asio.service.model.relational.CacheRegistry.TABLE;

/**
 * CacheRegistry Class. In relational model the Cache Registry entity for audit.
 * @see DiscoveryApplication
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Entity
@Table(name = TABLE)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class CacheRegistry {

    public static final String TABLE = "cache_registry";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Columns.ID)
    @EqualsAndHashCode.Include
    private long id;

    @Version
    private Long version;

    @JsonIgnore
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    private DiscoveryApplication discoveryApplication;

    @Column(name = Columns.NODE, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    private String node;

    @Column(name = Columns.TRIPLE_STORE, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    private String tripleStore;

    @Column(name = Columns.CLASS_NAME, nullable = false,columnDefinition = "VARCHAR(200)",length = 200)
    private String className;

    @Column(name = Columns.LAST_UPDATE, nullable = false,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP",insertable = true, updatable = false)
    private Date lastUpdate;


    /**
     * Constructor
     * @see DiscoveryApplication
     * @param discoveryApplication DiscoveryApplication. The Discovery Application instance
     * @param node String. The node name
     * @param tripleStore String. The triple Store name
     * @param className String. The class name
     */
    public CacheRegistry(DiscoveryApplication discoveryApplication, String node, String tripleStore, String className) {
        this.discoveryApplication = discoveryApplication;
        this.node = node;
        this.tripleStore = tripleStore;
        this.className = className;
        this.lastUpdate = new Date();
    }

    /**
     * Column name constants.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Columns {
        /**
         * ID column.
         */
        protected static final String ID = "id";
        /**
         * NODE column.
         */
        protected static final String NODE = "node";
        /**
         * TRIPLE_STORE column.
         */
        protected static final String TRIPLE_STORE = "triple_store";
        /**
         * CLASS_NAME column.
         */
        protected static final String CLASS_NAME = "class_name";
        /**
         * REQUEST_DATE column.
         */
        protected static final String LAST_UPDATE = "last_update";
    }

}
