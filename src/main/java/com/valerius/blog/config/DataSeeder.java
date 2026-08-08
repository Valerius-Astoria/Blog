package com.valerius.blog.config;

import com.valerius.blog.model.User;
import com.valerius.blog.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.stereotype.Component;

/**
 * Seeds a persisted {@link User} matching the configured Spring
 * Security default username on application startup.
 * <p>
 * Runs after the application context is ready. If no user exists whose
 * email equals {@code spring.security.user.name}, inserts one so that
 * {@link com.valerius.blog.security.CurrentUserService} can resolve the
 * in-memory security principal to a database row. The stored
 * {@code passwordHash} is a placeholder; authentication continues to
 * use Spring Security's configured user password.
 *
 * @author Valerius
 * @see UserRepository
 * @see SecurityProperties
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final SecurityProperties securityProperties;

    /**
     * Creates a seeder that writes the default security user into the
     * database when missing.
     *
     * @param userRepository     repository used to look up and save
     *                           users; must not be {@code null}
     * @param securityProperties Boot security properties providing the
     *                           default username; must not be
     *                           {@code null}
     */
    public DataSeeder(UserRepository userRepository,
            SecurityProperties securityProperties) {
        this.userRepository = userRepository;
        this.securityProperties = securityProperties;
    }

    /**
     * Inserts a user whose email equals
     * {@code spring.security.user.name} if that email is not already
     * present.
     * <p>
     * This method is idempotent with respect to email: a second
     * startup against a non-empty store does not insert a duplicate.
     *
     * @param args application arguments; ignored
     */
    @Override
    public void run(ApplicationArguments args) {
        String email = securityProperties.getUser().getName();

        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            // Login uses Spring Security's in-memory user; this value
            // only satisfies the non-null persistence constraint.
            user.setPasswordHash("{noop}unused");
            userRepository.save(user);
        }
    }
}
