package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.DiscoveryApplication;
import es.um.asio.service.model.relational.ObjectResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * ObjectResultRepository interface. Repository JpaRepository for ObjectResult entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
public interface ObjectResultRepository extends JpaRepository<ObjectResult,Long> {


    Optional<ObjectResult> findById(String id);

    Optional<List<ObjectResult>> findByLocalURI(String localURI);

    Optional<List<ObjectResult>> findByEntityIdAndClassNameAndIsMain(String entityId,String className, boolean isMain);

    @Query("SELECT o from ObjectResult o " +
            " left join JobRegistry j on o.jobRegistry = j.id " +
            " where j.node = :node and j.tripleStore = :tripleStore and  o.isMain = 1 and o.state = 'OPEN'")
    public List<ObjectResult> getOpenObjectResults(@Param("node") String node,@Param("tripleStore") String tripleStore);
}
