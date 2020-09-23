package es.um.asio.service.repository;

import es.um.asio.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Spring Data repository for {@link User}
 */
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    /**
     * Finds a URI using the ONTOLOGY_URI field.
     *
     * @param username
     *            The username to search for
     * @return an {@link User} entity stored in the database or {@literal Optional#empty()} if none found
     */
    Optional<User> findByUsername(String username);

    /**
     * Locks / unlocks a user account.
     *
     * @param accountNonLocked
     *            The accountNonLocked value to set
     * @param userId
     *            The userId that is being modified
     */
    @Modifying
    @Query("update User u set u.accountNonLocked = ?1 where u.id = ?2")
    void setAccountNonLocked(boolean accountNonLocked, String userId);
}
