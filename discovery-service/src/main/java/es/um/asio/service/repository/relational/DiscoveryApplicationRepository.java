package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.DiscoveryApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * DiscoveryApplicationRepository interface. Repository JpaRepository for DiscoveryApplication entities
 * @see JpaRepository
 * @author  Daniel Ruiz Santamaría
 * @version 2.0
 * @since   1.0
 */
@Transactional(readOnly = true)
public interface DiscoveryApplicationRepository extends JpaRepository<DiscoveryApplication,String> {

    Optional<DiscoveryApplication> findById(String id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "INSERT discovery.application (\n" +
            "\tid, name, pid, started_date, version\n"+
            ") VALUES (\n" +
            "\t:id, :name, :pid, :startedDate, :version\n"+
            ")"
            , nativeQuery = true)
    void insertNoNested(
            @Param("id") String id,
            @Param("name") String name,
            @Param("pid") String pid,
            @Param("startedDate") Date startedDate,
            @Param("version") Long version
    );
}
