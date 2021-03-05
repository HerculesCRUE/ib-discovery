package es.um.asio.service.model.relational;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

import static es.um.asio.service.model.relational.ElasticRegistry.TABLE;

/**
 * ElasticRegistry Class. In relational model the Elastic Registry entity for audit.
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
public class ElasticRegistry {

    public static final String TABLE = "elastic_registry";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = Columns.ID)
    @EqualsAndHashCode.Include
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DiscoveryApplication discoveryApplication;

    @Column(name = Columns.NODE, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    private String node;

    @Column(name = Columns.TRIPLE_STORE, nullable = false,columnDefinition = "VARCHAR(100)",length = 100)
    private String tripleStore;

    @Column(name = Columns.CLASS_NAME, nullable = false,columnDefinition = "VARCHAR(200)",length = 200)
    private String className;

    @Column(name = Columns.INSERTED, nullable = false)
    private int inserted;

    @Column(name = Columns.LAST_UPDATE, nullable = false,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP",insertable = true, updatable = false)
    private Date lastUpdate;

    /**
     * Constructor
     * @see DiscoveryApplication
     * @param discoveryApplication  DiscoveryApplication. The discovery application
     * @param node String. The node name
     * @param tripleStore String. The triple Store name
     * @param className String. The class name
     * @param inserted. int. Number of instances inserted
     */
    public ElasticRegistry(DiscoveryApplication discoveryApplication, String node, String tripleStore, String className,int inserted) {
        this.discoveryApplication = discoveryApplication;
        this.node = node;
        this.tripleStore = tripleStore;
        this.className = className;
        this.inserted = inserted;
        this.lastUpdate = new Date();
    }

    /**
     * Column name constants.
     */

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Columns {
        /*
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
         * CLASS_NAME column.
         */

        protected static final String INSERTED = "inserted";
         /**
         * REQUEST_DATE column.
         */

        protected static final String LAST_UPDATE = "last_update";
    }

}

