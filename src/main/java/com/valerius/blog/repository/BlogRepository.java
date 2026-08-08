package com.valerius.blog.repository;

import com.valerius.blog.model.Blog;
import com.valerius.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Persistence operations for {@link Blog} entities.
 * <p>
 * Extends {@link JpaRepository} with identifier type {@link Long}, and
 * therefore provides the standard create, read, update, delete, flush,
 * and query-by-example operations defined by that interface. Custom
 * query methods declared here are implemented by Spring Data JPA from
 * their method signatures.
 * <p>
 * Implementations of this interface are not required to be thread-safe
 * for concurrent use of a single repository proxy from multiple threads
 * without external coordination; typical Spring usage obtains the bean
 * as a singleton and relies on the underlying persistence context
 * scoping.
 *
 * @author Valerius
 * @see Blog
 * @see JpaRepository
 */
public interface BlogRepository extends JpaRepository<Blog, Long> {

    /**
     * Returns every blog post whose author list contains the given user.
     * <p>
     * A blog matches if and only if {@code author} is an element of that
     * blog's {@code authors} association. The returned list is empty if
     * no such blog exists; it is never {@code null}. The order of
     * elements is not specified.
     *
     * @param author the user that must appear among a blog's authors;
     *               must not be {@code null}
     * @return the matching blog posts, possibly empty
     */
    List<Blog> findByAuthorsContaining(User author);

    /**
     * Returns the blog with the given id when the given user is among
     * its authors.
     * <p>
     * The result is empty if no blog has the given id, or if a blog
     * with that id exists but {@code author} is not an element of its
     * {@code authors} association.
     *
     * @param id     the blog identifier; must not be {@code null}
     * @param author the user that must appear among the blog's authors;
     *               must not be {@code null}
     * @return an {@code Optional} describing the matching blog, or an
     *         empty {@code Optional} if none matches
     */
    Optional<Blog> findByIdAndAuthorsContaining(Long id, User author);

}
