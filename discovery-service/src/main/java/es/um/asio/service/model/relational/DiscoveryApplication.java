package es.um.asio.service.model.relational;

import lombok.*;
import org.springframework.boot.system.ApplicationPid;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DiscoveryApplication Class. In relational model the Discovery Application entity for audit.
 * @see CacheRegistry
 * @see ElasticRegistry
 * @see JobRegistry
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Entity
@Table(name = DiscoveryApplication.TABLE)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class DiscoveryApplication {

    public static final String TABLE = "application";

    @Id
/*    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")*/
    @Column(name = Columns.ID,columnDefinition = "VARCHAR(36)",updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private String id;

    @Version
    private Long version;

    /**
     * NAME.
     */
    @Column(name = Columns.NAME, nullable = false,columnDefinition = "VARCHAR(200)",length = 200)
    private String name;

    @Column(name = JobRegistry.Columns.STARTED_DATE, nullable = false,columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP",insertable = true, updatable = false)
    private Date startDate;


    /**
     * PID.
     */
    @Column(name = Columns.PID, nullable = false,columnDefinition = "VARCHAR(40)",length = 40)
    private String pid;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "discoveryApplication", cascade = CascadeType.ALL)
    private Set<CacheRegistry> cacheRegistries;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "discoveryApplication", cascade = CascadeType.ALL)
    private Set<ElasticRegistry> elasticRegistries;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "discoveryApplication", cascade = CascadeType.ALL)
    private Set<JobRegistry> jobRegistries;


    /**
     * Constructor
     * @param name String. The name of the discovery application
     */
    public DiscoveryApplication(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.startDate = new Date();
        this.pid = new ApplicationPid().toString();
        this.jobRegistries = new HashSet<>();
        this.cacheRegistries = new HashSet<>();
        this.elasticRegistries = new HashSet<>();
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
         * NAME column.
         */
        protected static final String NAME = "name";
        /**
         * STARTED_DATE column.
         */
        protected static final String STARTED_DATE = "started_date";
        /**
         * STARTED_DATE column.
         */
        protected static final String PID = "pid";

    }

}
