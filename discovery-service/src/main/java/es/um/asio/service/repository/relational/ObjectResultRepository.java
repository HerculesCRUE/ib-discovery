package es.um.asio.service.repository.relational;

import es.um.asio.service.model.relational.ObjectResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;
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


    @Query(value = "SELECT coalesce(max(id), 0)+1 FROM discovery.object_result", nativeQuery = true)
    public Long getNextId();

    @Modifying
    @Transactional
    @Query(value = "INSERT discovery.object_result (\n" +
            "\tid, version, origin, node, triple_store, class_name, local_uri,\n" +
            "\tcanonical_uri, last_modification, jobRegistry_id, entity_id,\n" +
            "\tparentAutomatic_id, parentManual_id, parentLink_id,\n" +
            "\tsimilarity, similarity_no_id, is_main,is_automatic, is_manual,\n" +
            "\tis_merge, is_link, merge_action, state, actionResultParent_id\n" +
            ") VALUES (\n" +
            "\t:id, :version, :origin, :node, :tripleStore, :className, :localUri,\n" +
            "\t:canonicalUri, :lastModification, :jobRegistryId, :entityId,\n" +
            "\t:parentAutomaticId, :parentManualId, :parentLinkId,\n" +
            "\t:similarity, :similarityWithOutId, :isMain, :isAutomatic, :isManual,\n" +
            "\t:isMerge, :isLink, :mergeAction, :state, :actionResultParentId\n" +
            ")", nativeQuery = true)
    int insertNoNested(
            @Param("id") long id,
            @Param("version") Long version,
            @Param("origin") String origin,
            @Param("node") String node,
            @Param("tripleStore") String tripleStore,
            @Param("className") String className,
            @Param("localUri") String localUri,
            @Param("canonicalUri") String canonicalUri,
            @Param("lastModification") Date lastModification,
            @Param("jobRegistryId") String jobRegistryId,
            @Param("entityId") String entityId,
            @Param("parentAutomaticId") Long parentAutomaticId,
            @Param("parentManualId") Long parentManualId,
            @Param("parentLinkId") Long parentLinkId,
            @Param("similarity") Float similarity,
            @Param("similarityWithOutId") Float similarityWithOutId,
            @Param("isMain") boolean isMain,
            @Param("isAutomatic") boolean isAutomatic,
            @Param("isManual") boolean isManual,
            @Param("isMerge") boolean isMerge,
            @Param("isLink") boolean isLink,
            @Param("mergeAction") String mergeAction,
            @Param("state") String state,
            @Param("actionResultParentId") Long actionResultParentId
    );
}
