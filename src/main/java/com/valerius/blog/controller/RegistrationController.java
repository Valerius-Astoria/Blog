package com.valerius.blog.controller;

import com.valerius.blog.model.User;
import com.valerius.blog.repository.UserRepository;
import com.valerius.blog.security.RegistrationForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles account registration at {@code /register}.
 * <p>
 * GET renders the registration form. POST encodes the submitted
 * password, persists a new {@link User}, and redirects to
 * {@code /login}. Duplicate emails are not checked here; persistence
 * may fail if the email unique constraint is violated. Successful
 * registration does not authenticate the new account.
 *
 * @author Valerius
 * @see RegistrationForm
 * @see UserRepository
 */
@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    /**
     * @param userRepository store for new accounts; must not be
     *                       {@code null}
     * @param encoder        encodes submitted passwords; must not be
     *                       {@code null}
     */
    public RegistrationController(UserRepository userRepository,
            PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    /**
     * Provides a new unbound {@link RegistrationForm} for the form.
     *
     * @return a fresh form instance; never {@code null}
     */
    @ModelAttribute
    public RegistrationForm registrationForm() {
        return new RegistrationForm();
    }

    /**
     * Shows the registration form.
     *
     * @return view name {@code registration}
     */
    @GetMapping
    public String registerForm() {
        return "registration";
    }

    /**
     * Persists a new account from the submitted form.
     *
     * @param registrationForm bound registration data; must not be
     *                         {@code null}
     * @return redirect to {@code /login}
     */
    @PostMapping
    public String processRegistrationForm(
            RegistrationForm registrationForm) {
        userRepository.save(registrationForm.toUser(encoder));
        return "redirect:/login";
    }
}
