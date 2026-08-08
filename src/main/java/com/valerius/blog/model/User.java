package com.valerius.blog.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * An account holder who may authenticate with this application.
 * <p>
 * Two {@code User} instances are equal if and only if their
 * {@link #id} values are equal, including when both identifiers are
 * {@code null}. Equality and hash code ignore all other fields of this
 * class. Callers must not rely on hash-based collections remaining
 * valid if an identifier is assigned after an instance has already been
 * inserted into such a collection.
 * <p>
 * The fields {@code email} and {@code passwordHash} must be
 * non-{@code null} when this entity is persisted. Among persisted
 * users, {@code email} values are unique. The {@code createdAt}
 * timestamp is assigned when the entity is first persisted and must not
 * be changed afterward.
 * <p>
 * The generated string representation of this class omits
 * {@code passwordHash}.
 * <p>
 * This class is not thread-safe. Concurrent access from multiple threads
 * requires external synchronization.
 *
 * @author Valerius
 * @see Instant
 */
@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    /**
     * Unique identifier of this user.
     * <p>
     * {@code null} until the persistence provider assigns a value on
     * insert. After assignment, the value is unique among persisted
     * {@code User} instances and is the sole basis for
     * {@link #equals(Object)} and {@link #hashCode()}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Login address that uniquely identifies this user among persisted
     * accounts.
     * Must be non-{@code null} when persisted; empty strings are not
     * prohibited by this type. Case sensitivity of uniqueness is
     * determined by the underlying datastore collation.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * One-way digest of this user's password credentials.
     * Must be non-{@code null} when persisted. This value is never a
     * plaintext password; callers store only the result of a password
     * hashing function. Omitted from {@link #toString()}.
     */
    @ToString.Exclude
    @Column(nullable = false)
    private String passwordHash;

    /**
     * Instant at which this user account was first persisted.
     * <p>
     * Assigned automatically on insert and not updated thereafter.
     * {@code null} only for instances that have never been persisted.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

}
