package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * AttributeRepository interface. Repository JpaRepository for Attribute entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Transactional(timeout = 10, readOnly = true)
public interface AttributeRepository extends JpaRepository<Attribute,Long> {

    @Query(value = "SELECT coalesce(max(id), 0)+1 FROM discovery.attribute", nativeQuery = true)
    public Long getNextId();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "INSERT discovery.attribute (\n" +
            "\tid, `key`, version, objectResult_id,parentValue_id" +
            ") VALUES (\n" +
            "\t:id, :key, :version, :objectResultId, :parentValueId\n" +
            ")", nativeQuery = true)
    void insertNoNested(
            @Param("id") Long id,
            @Param("key") String key,
            @Param("version") Long version,
            @Param("objectResultId") Long objectResultId,
            @Param("parentValueId") Long parentValueId
    );
}
