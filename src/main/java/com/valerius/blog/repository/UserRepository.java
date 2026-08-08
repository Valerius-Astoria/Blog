package com.valerius.blog.repository;

import com.valerius.blog.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Spring Data repository for {@link User} aggregates.
 * <p>
 * Inherits standard {@link CrudRepository} operations for entity type
 * {@link User} and id type {@link Long}. Email is unique among
 * persisted users, so email lookup returns at most one row.
 *
 * @author Valerius
 * @see User
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Returns the user with the given email, if present.
     *
     * @param email email to match; must not be {@code null}
     * @return the matching user, or empty if none exists
     */
    Optional<User> findByEmail(String email);

}
