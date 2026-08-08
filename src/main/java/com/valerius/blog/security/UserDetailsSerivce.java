package com.valerius.blog.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Loads a {@link UserDetails} account by email address.
 * <p>
 * This is a project-local SPI distinct from Spring Security's
 * {@link org.springframework.security.core.userdetails.UserDetailsService},
 * which keys lookup by a generic username string. Callers that need
 * email-named lookup may implement this interface.
 *
 * @author Valerius
 */
public interface UserDetailsSerivce {

    /**
     * Returns the account for {@code email}.
     *
     * @param email account email used as the login name; must not be
     *              {@code null}
     * @return the matching user details; never {@code null}
     * @throws UsernameNotFoundException if no account exists for
     *         {@code email}
     */
    UserDetails loadUserByEmail(String email)
            throws UsernameNotFoundException;
}
