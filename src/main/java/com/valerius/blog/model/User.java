package com.valerius.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

/**
 * An application account, mapped to table {@code app_user}.
 * <p>
 * Implements Spring Security {@link UserDetails}:
 * {@link #getUsername()} is {@code email},
 * {@link #getPassword()} is {@code passwordHash}, and
 * {@link #getAuthorities()} grants {@code ROLE_USER}. Account-status
 * checks use the {@code UserDetails} defaults (enabled, non-expired,
 * non-locked).
 * <p>
 * Equality and hash code use only {@link #id}, including when both ids
 * are {@code null}. Do not place an instance in a hash-based collection
 * before its id is assigned, then rely on that collection after
 * assignment.
 * <p>
 * {@code email} and {@code passwordHash} must be non-{@code null} when
 * persisted. Email is unique among persisted users.
 * {@code createdAt} is set on first insert and is not updated afterward.
 * {@link #toString()} omits {@code passwordHash}.
 * <p>
 * This class is not thread-safe.
 *
 * @author Valerius
 */
@Data
@Entity
@Table(name = "app_user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails {

    /**
     * Persistent identity of this account, or {@code null} before
     * insert. Sole basis for {@link #equals(Object)} and
     * {@link #hashCode()}.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Unique login address among persisted accounts.
     * Non-{@code null} when persisted; empty strings are allowed.
     * Case sensitivity of uniqueness follows the datastore collation.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Stored credential material for this account.
     * Non-{@code null} when persisted. Omitted from
     * {@link #toString()}.
     */
    @ToString.Exclude
    private String passwordHash;

    /**
     * Creation time assigned on first persist; {@code null} only before
     * insert. Not updated afterward.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Returns the authorities granted to this account.
     * <p>
     * Always a list containing a single {@code ROLE_USER} authority.
     *
     * @return non-empty collection of authorities; never {@code null}
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Returns the stored credential for authentication.
     *
     * @return {@code passwordHash}, or {@code null} if unset
     */
    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    /**
     * Returns the Spring Security username for this account.
     *
     * @return {@code email}; may be {@code null} before it is set
     */
    @Override
    public String getUsername() {
        return email;
    }

}
