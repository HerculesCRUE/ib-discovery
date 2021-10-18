package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * ValueRepository interface. Repository JpaRepository for Value entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Transactional(readOnly = true)
public interface ValueRepository extends JpaRepository<Value,Long> {

    @Query(value = "SELECT coalesce(max(id), 0)+1 FROM discovery.val", nativeQuery = true)
    public Long getNextId();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "INSERT discovery.val (\n" +
            "\tid, type, val, version, attribute_id" +
            ") VALUES (\n" +
            "\t:id, :type, :val, :version, :attributeId\n" +
            ")", nativeQuery = true)
    void insertNoNested(
            @Param("id") Long id,
            @Param("type") String type,
            @Param("val") String val,
            @Param("version") Long version,
            @Param("attributeId") Long attributeId
    );
}
