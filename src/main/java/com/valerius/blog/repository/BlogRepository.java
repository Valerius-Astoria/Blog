package com.valerius.blog.repository;

import com.valerius.blog.model.Blog;
import com.valerius.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for {@link Blog} aggregates.
 * <p>
 * Inherits standard {@link JpaRepository} operations for entity type
 * {@link Blog} and id type {@link Long}. Declared query methods are
 * derived from their names by Spring Data JPA.
 *
 * @author Valerius
 * @see Blog
 */
public interface BlogRepository extends JpaRepository<Blog, Long> {

    /**
     * Returns blogs that include {@code author} in {@code authors}.
     * Never {@code null}; empty if none match. Order is unspecified.
     *
     * @param author required author; must not be {@code null}
     * @return matching blogs, possibly empty
     */
    List<Blog> findByAuthorsContaining(User author);

    /**
     * Returns the blog with {@code id} when {@code author} is among its
     * authors; otherwise empty.
     *
     * @param id     blog id; must not be {@code null}
     * @param author required author; must not be {@code null}
     * @return the matching blog, or empty if id is unknown or the user
     *         is not an author
     */
    Optional<Blog> findByIdAndAuthorsContaining(Long id, User author);

}
