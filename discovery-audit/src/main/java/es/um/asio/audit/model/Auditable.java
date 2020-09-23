package es.um.asio.audit.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract class that must implement Entities that need auditory.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString(includeFieldNames = true)
public abstract class Auditable {
    /**
     * Principal that created the entity.
     */
    @CreatedBy
    @Column(name = Columns.CREATED_BY)
    private String createdBy;

    /**
     * Date the entity was created.
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @CreatedDate
    @Column(name = Columns.CREATED_DATE)
    private Date createdDate;

    /**
     * Principal that recently modified the entity.
     */
    @LastModifiedBy
    @Column(name = Columns.LAST_MODIFIED_BY)
    private String lastModifiedBy;

    /**
     * Date the entity was recently modified.
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @LastModifiedDate
    @Column(name = Columns.LAST_MODIFIED_DATE)
    private Date lastModifiedDate;

    /**
     * Gets the createdDate.
     *
     * @return the createdDate.
     */
    public Date getCreatedDate() {
        return this.createdDate == null ? null : new Date(this.createdDate.getTime());
    }

    /**
     * Sets the createdDate.
     *
     * @param createdDate
     *            the createdDate to set.
     */
    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate == null ? null : new Date(createdDate.getTime());
    }

    /**
     * Gets the lastModifiedDate.
     *
     * @return the lastModifiedDate.
     */
    public Date getLastModifiedDate() {
        return this.lastModifiedDate == null ? null : new Date(this.lastModifiedDate.getTime());
    }

    /**
     * Sets the lastModifiedDate.
     *
     * @param lastModifiedDate
     *            the lastModifiedDate to set.
     */
    public void setLastModifiedDate(final Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate == null ? null : new Date(lastModifiedDate.getTime());
    }

    /**
     * Column name constants.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Columns {

        /**
         * Created By column.
         */
        protected static final String CREATED_BY = "CREATED_BY";

        /**
         * Created Date column.
         */
        protected static final String CREATED_DATE = "CREATED_DATE";

        /**
         * Last Modified By column.
         */
        protected static final String LAST_MODIFIED_BY = "LAST_MODIFIED_BY";

        /**
         * Last Modified Date column.
         */
        protected static final String LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";
    }

    /**
     * The Class Properties.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Properties {

        /**
         * Created By field name.
         */
        protected static final String CREATED_BY = "createdBy";

        /**
         * Created Date field name.
         */
        protected static final String CREATED_DATE = "createdDate";

        /**
         * Last Modified By field name.
         */
        protected static final String LAST_MODIFIED_BY = "lastModifiedBy";

        /**
         * Last Modified Date field name.
         */
        protected static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    }
}
