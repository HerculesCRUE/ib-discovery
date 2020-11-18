package es.um.asio.service.model.relational;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.system.ApplicationPid;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

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


    public DiscoveryApplication(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.startDate = new Date();
        this.pid = new ApplicationPid().toString();
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
