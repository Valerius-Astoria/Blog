package com.valerius.blog.security;

import com.valerius.blog.model.User;
import com.valerius.blog.repository.UserRepository;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Form backing object for account registration.
 * <p>
 * Holds the submitted email and plaintext password. {@link #toUser}
 * maps those values into a new {@link User} whose
 * {@code passwordHash} is produced by the given
 * {@link PasswordEncoder}.
 * <p>
 * This class is not thread-safe.
 *
 * @author Valerius
 * @see User
 * @see com.valerius.blog.controller.RegistrationController
 */
@Data
public class RegistrationForm {

    /**
     * Submitted login address. Bound from the registration form.
     */
    private String email;

    /**
     * Submitted plaintext password. Bound from the registration form;
     * never persisted as-is.
     */
    private String password;

    /**
     * Builds a new {@link User} from this form.
     * <p>
     * The returned user has {@code email} set from this form and
     * {@code passwordHash} set to {@code encoder.encode(password)}.
     * Other fields remain at their defaults (unset id and
     * {@code createdAt}).
     *
     * @param encoder password encoder; must not be {@code null}
     * @return a new user ready to persist; never {@code null}
     * @throws NullPointerException if {@code encoder} is {@code null}
     */
    public User toUser(PasswordEncoder encoder) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));
        return user;
    }
}
