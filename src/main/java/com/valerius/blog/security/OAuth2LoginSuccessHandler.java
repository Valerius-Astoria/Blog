package com.valerius.blog.security;

import com.valerius.blog.model.User;
import com.valerius.blog.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Completes OAuth2/OIDC login by resolving a persisted {@link User}.
 * <p>
 * Finds an existing account by email or creates one with an unusable
 * password hash, then replaces the OAuth principal with that
 * {@link User} so {@code @AuthenticationPrincipal User} continues to
 * work for form-login and social-login sessions.
 *
 * @author Valerius
 * @see UserRepository
 */
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Marker hash that is not a valid BCrypt digest, so form login cannot
     * authenticate OAuth-only accounts with a guessed password.
     */
    private static final String OAUTH_PASSWORD_PLACEHOLDER =
            "{oauth2}nologin";
    private static final String DEFAULT_SUCCESS_URL = "/create";

    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    /**
     * @param userRepository user store used to find or create accounts;
     *                       must not be {@code null}
     */
    public OAuth2LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Maps the OAuth2 principal to a persisted {@link User} and
     * redirects to {@code /create}.
     *
     * @param request        current request; must not be {@code null}
     * @param response       current response; must not be {@code null}
     * @param authentication successful OAuth2 authentication; must not
     *                       be {@code null}
     * @throws IOException      if redirect fails
     * @throws ServletException if the container reports an error
     * @throws IllegalStateException if the provider supplies no email
     *         (and no GitHub login fallback)
     */
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = resolveEmail(oauth2User);

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User created = new User();
            created.setEmail(email);
            created.setPasswordHash(OAUTH_PASSWORD_PLACEHOLDER);
            return userRepository.save(created);
        });

        UsernamePasswordAuthenticationToken blogAuth =
                new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
        blogAuth.setDetails(authentication.getDetails());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(blogAuth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        response.sendRedirect(DEFAULT_SUCCESS_URL);
    }

    /**
     * Resolves a login email from provider attributes.
     * Prefers {@code email}, then OIDC email, then a GitHub noreply
     * address derived from {@code login}.
     *
     * @param oauth2User authenticated OAuth2 user; must not be
     *                   {@code null}
     * @return non-blank email; never {@code null}
     * @throws IllegalStateException if no usable email can be derived
     */
    private static String resolveEmail(OAuth2User oauth2User) {
        Object emailAttr = oauth2User.getAttribute("email");
        if (emailAttr != null && !emailAttr.toString().isBlank()) {
            return emailAttr.toString();
        }
        if (oauth2User instanceof OidcUser oidcUser
                && oidcUser.getEmail() != null
                && !oidcUser.getEmail().isBlank()) {
            return oidcUser.getEmail();
        }
        Object login = oauth2User.getAttribute("login");
        if (login != null && !login.toString().isBlank()) {
            return login + "@users.noreply.github.com";
        }
        throw new IllegalStateException(
                "OAuth provider did not supply an email address");
    }
}
