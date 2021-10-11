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


    @Modifying
    @Transactional
    @Query(value = "INSERT discovery.action_result (\n" +
            "\taction, version, objectResultParent_id" +
            ") VALUES (\n" +
            "\t:action, :version, :objectResultParentId\n" +
            ")", nativeQuery = true)
    void insertNoNested(
            @Param("action") String action,
            @Param("version") Long version,
            @Param("objectResultParentId") Long objectResultParentId
    );
}
