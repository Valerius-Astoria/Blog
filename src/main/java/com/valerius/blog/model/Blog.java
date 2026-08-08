package com.valerius.blog.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A published blog post.
 * <p>
 * Equality and hash code use only {@link #id}, including when both ids
 * are {@code null}. Do not place an instance in a hash-based collection
 * before its id is assigned, then rely on that collection after
 * assignment.
 * <p>
 * {@code title}, {@code subTitle}, and {@code body} must be
 * non-{@code null} when persisted. {@code createdAt} is set on first
 * insert and is not updated afterward. A post may list zero or more
 * {@link User} authors.
 * <p>
 * This class is not thread-safe.
 *
 * @author Valerius
 * @see User
 */
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Blog {

    /**
     * Persistent identity of this post, or {@code null} before insert.
     * Sole basis for {@link #equals(Object)} and {@link #hashCode()}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Primary headline.
     * Non-{@code null} when persisted; empty strings are allowed.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Secondary headline shown with {@link #title}.
     * Non-{@code null} when persisted; empty strings are allowed.
     */
    @Column(nullable = false)
    private String subTitle;

    /**
     * Full post content.
     * Non-{@code null} when persisted; empty strings are allowed.
     */
    @Column(nullable = false)
    private String body;

    /**
     * Authors of this post.
     * Never {@code null} after construction unless replaced; may be
     * empty. Duplicate membership is allowed.
     */
    @ManyToMany
    private List<User> authors = new ArrayList<>();

    /**
     * Creation time assigned on first persist; {@code null} only before
     * insert. Not updated afterward.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Appends {@code author} to {@link #authors}.
     *
     * @param author author to append; must not be {@code null}
     * @throws NullPointerException if {@code author} is {@code null},
     *         or if {@code authors} is {@code null}
     */
    public void addAuthor(User author) {
        this.authors.add(author);
    }
}
