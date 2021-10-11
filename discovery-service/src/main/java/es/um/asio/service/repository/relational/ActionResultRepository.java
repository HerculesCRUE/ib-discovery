package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.ActionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * ActionResultRepository interface. Repository JpaRepository for ActionResult entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface ActionResultRepository extends JpaRepository<ActionResult,Long> {

    @Query(value = "SELECT coalesce(max(id), 0)+1 FROM discovery.action_result", nativeQuery = true)
    public Long getNextId();

    @Modifying
    @Transactional
    @Query(value = "INSERT discovery.action_result (\n" +
            "\tid, action, version, objectResultParent_id" +
            ") VALUES (\n" +
            "\t:id, :action, :version, :objectResultParentId\n" +
            ")", nativeQuery = true)
    void insertNoNested(
            @Param("id") Long id,
            @Param("action") String action,
            @Param("version") Long version,
            @Param("objectResultParentId") Long objectResultParentId
    );
}
