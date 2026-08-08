package com.valerius.blog.config;

import com.valerius.blog.model.User;
import com.valerius.blog.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.stereotype.Component;

/**
 * Ensures a persisted {@link User} exists for the configured default
 * Spring Security username.
 * <p>
 * On startup, if no row has email equal to
 * {@code spring.security.user.name}, inserts one. The inserted
 * {@code passwordHash} is a placeholder that only satisfies the
 * non-null persistence constraint; it is not a usable login
 * credential under the database-backed
 * {@link org.springframework.security.core.userdetails.UserDetailsService}.
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
     * @param userRepository     user store; must not be {@code null}
     * @param securityProperties source of the default username; must
     *                           not be {@code null}
     */
    public DataSeeder(UserRepository userRepository,
            SecurityProperties securityProperties) {
        this.userRepository = userRepository;
        this.securityProperties = securityProperties;
    }

    /**
     * Inserts the default security user when missing.
     * Idempotent per email: does not insert a duplicate if the email
     * already exists.
     *
     * @param args application arguments; unused
     */
    @Override
    public void run(ApplicationArguments args) {
        String email = securityProperties.getUser().getName();

        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            // Placeholder only; not a usable credential for DB login.
            user.setPasswordHash("{noop}unused");
            userRepository.save(user);
        }
    }
}
