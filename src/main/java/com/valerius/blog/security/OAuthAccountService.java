package com.valerius.blog.security;

import com.valerius.blog.model.User;
import com.valerius.blog.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OAuthAccountService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuthAccountService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Finds the account for an OAuth email address or creates one.
     *
     * @param email verified email address supplied by the OAuth provider
     * @return the existing or newly persisted account
     */
    @Transactional
    public User findOrCreate(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail(email);
                    // The database requires a password value. Store a random,
                    // unusable credential so this account remains OAuth-only.
                    user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
                    return userRepository.save(user);
                });
    }
}
