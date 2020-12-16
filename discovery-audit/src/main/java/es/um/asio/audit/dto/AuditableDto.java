package es.um.asio.audit.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Auditable entity DTO
 */
@Getter
@Setter
@ToString(includeFieldNames = true)
public abstract class AuditableDto {
	/**
	 * Principal that created the entity.
	 */
	//private String createdBy;

	/**
	 * Date the entity was created.
	 */
	//private Date createdDate;

	/**
	 * Principal that recently modified the entity.
	 */
	//private String lastModifiedBy;

	/**
	 * Date the entity was recently modified.
	 */
	//private Date lastModifiedDate;
}
