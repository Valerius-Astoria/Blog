package com.valerius.blog.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

/**
 * A published blog post persisted as a JPA entity.
 * <p>
 * Two {@code Blog} instances are equal if and only if their
 * {@link #id} values are equal, including when both identifiers are
 * {@code null}. Equality and hash code ignore all other fields of this
 * class. Callers must not rely on hash-based collections remaining
 * valid if an identifier is assigned after an instance has already been
 * inserted into such a collection.
 * <p>
 * The fields {@code title}, {@code subTitle}, and {@code body} must be
 * non-{@code null} when this entity is persisted. The {@code createdAt}
 * timestamp is assigned when the entity is first persisted and must not
 * be changed afterward. A blog may have zero or more {@link User}
 * authors through the {@code authors} association.
 * <p>
 * This class is not thread-safe. Concurrent access from multiple threads
 * requires external synchronization.
 *
 * @author Valerius
 * @see Instant
 * @see User
 */
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Blog {

    /**
     * Unique identifier of this blog post.
     * <p>
     * {@code null} until the persistence provider assigns a value on
     * insert. After assignment, the value is unique among persisted
     * {@code Blog} instances and is the sole basis for
     * {@link #equals(Object)} and {@link #hashCode()}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Primary headline displayed for this blog post.
     * Must be non-{@code null} when persisted; empty strings are not
     * prohibited by this type.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Secondary headline displayed beneath the {@link #title}.
     * Must be non-{@code null} when persisted; empty strings are not
     * prohibited by this type.
     */
    @Column(nullable = false)
    private String subTitle;

    /**
     * Full textual content of this blog post.
     * Must be non-{@code null} when persisted; empty strings are not
     * prohibited by this type.
     */
    @Column(nullable = false)
    private String body;

    /**
     * Users credited as authors of this blog post.
     * <p>
     * May be empty. Duplicate membership is not prohibited by this
     * type. The list must be non-{@code null} before
     * {@link #addAuthor(User)} is invoked.
     */
    @ManyToMany
    private List<User> authors;

    /**
     * Instant at which this blog post was first persisted.
     * <p>
     * Assigned automatically on insert and not updated thereafter.
     * {@code null} only for instances that have never been persisted.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Appends the given user to this blog's author list.
     *
     * @param author the user to add; must not be {@code null}
     * @throws NullPointerException if {@code authors} or {@code author}
     *         is {@code null}
     */
    public void addAuthor(User author) {
        this.authors.add(author);
    }
}
