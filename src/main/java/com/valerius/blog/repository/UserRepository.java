package com.valerius.blog.repository;

import com.valerius.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Persistence operations for {@link User} entities.
 * <p>
 * Extends {@link JpaRepository} with identifier type {@link Long}, and
 * therefore provides the standard create, read, update, delete, flush,
 * and query-by-example operations defined by that interface. Custom
 * query methods declared here are implemented by Spring Data JPA from
 * their method signatures.
 * <p>
 * Among persisted users, {@link User#getEmail() email} values are
 * unique, so lookup by email yields at most one result.
 * <p>
 * Implementations of this interface are not required to be thread-safe
 * for concurrent use of a single repository proxy from multiple threads
 * without external coordination; typical Spring usage obtains the bean
 * as a singleton and relies on the underlying persistence context
 * scoping.
 *
 * @author Valerius
 * @see User
 * @see JpaRepository
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Returns the user with the given email address, if any.
     * <p>
     * Because email is unique among persisted users, this method returns
     * at most one match. The result is empty if no user has the given
     * email.
     *
     * @param email the email address to match; must not be {@code null}
     * @return an {@code Optional} describing the matching user, or an
     *         empty {@code Optional} if none exists
     */
    Optional<User> findByEmail(String email);

}
