package com.valerius.blog.security;

import com.valerius.blog.model.User;
import com.valerius.blog.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Resolves the persisted {@link User} that corresponds to the current
 * Spring Security authentication.
 * <p>
 * The authentication name is treated as the user's email address and
 * looked up through {@link UserRepository#findByEmail(String)}.
 *
 * @author Valerius
 * @see UserRepository
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    /**
     * Creates a service that loads users by email from the given
     * repository.
     *
     * @param userRepository repository used for email lookup; must not
     *                       be {@code null}
     */
    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the persisted user for the given authentication.
     * <p>
     * Uses {@link Authentication#getName()} as the email key. Fails if
     * no user with that email exists.
     *
     * @param authentication the current authentication whose name is
     *                       the account email; must not be {@code null}
     * @return the matching persisted user; never {@code null}
     * @throws IllegalStateException if no user exists for
     *         {@code authentication.getName()}
     */
    public User require(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Logged-in account not found: "
                                + authentication.getName()));
    }
}
