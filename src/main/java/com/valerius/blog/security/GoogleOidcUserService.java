package com.valerius.blog.security;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

/**
 * Loads Google OpenID Connect users only when Google has verified their email.
 *
 * <p>The application uses the email address as its durable account identity,
 * so accepting an unverified claim could let a user claim another account.</p>
 */
@Component
public class GoogleOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {
        OidcUser user = super.loadUser(userRequest);
        if (!Boolean.TRUE.equals(user.getClaimAsBoolean("email_verified"))
                || user.getEmail() == null || user.getEmail().isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_required"),
                    "Google did not provide a verified email address");
        }
        return user;
    }
}
