package com.valerius.blog.security;

import com.valerius.blog.model.User;
import com.valerius.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Declares password-encoding and user-lookup beans for Spring Security.
 * <p>
 * {@link #userDetailsService(UserRepository)} treats the authentication
 * username as an email and loads the matching {@link User} from
 * {@link UserRepository#findByEmail(String)}.
 *
 * @author Valerius
 * @see UserRepository
 * @see User
 */
@Configuration
public class SecurityConfig {

    private final String rememberMeKey;

    /**
     * @param rememberMeKey shared secret for remember-me tokens; must
     *                      not be {@code null}
     */
    public SecurityConfig(@Value("${app.remember-me-key}") String rememberMeKey) {
        this.rememberMeKey = rememberMeKey;
    }

    /**
     * Returns a BCrypt password encoder for hashing and verification.
     *
     * @return a new {@link BCryptPasswordEncoder}; never {@code null}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Returns a {@link UserDetailsService} that loads accounts by email.
     * <p>
     * The authentication name is passed to
     * {@link UserRepository#findByEmail(String)}. Missing users raise
     * {@link UsernameNotFoundException}.
     *
     * @param userRepo user store; must not be {@code null}
     * @return email-based user details service; never {@code null}
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {
        return username -> userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User '" + username + "' not found"));
    }

    /**
     * Configures authorization, form login, remember-me, and logout.
     *
     * @param http HTTP security builder; must not be {@code null}
     * @param userDetailsService account lookup for form and remember-me
     *                           authentication; must not be {@code null}
     * @return the built filter chain; never {@code null}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            UserDetailsService userDetailsService)
            throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/login", "/register")
                        .permitAll()
                    .requestMatchers("/history", "/history/**", "/create")
                        .hasRole("USER")
                    .anyRequest().permitAll())
            .formLogin(form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/create", true)
                    .permitAll())
            .rememberMe(remember -> remember
                    .key(rememberMeKey)
                    .alwaysRemember(true)
                    .userDetailsService(userDetailsService))
            .logout(logout -> logout
                    .logoutSuccessUrl("/login?loggedOut"));

        return http.build();
    }
}
