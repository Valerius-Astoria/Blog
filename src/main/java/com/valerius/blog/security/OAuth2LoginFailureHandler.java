package com.valerius.blog.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Redirects failed OAuth2 logins to the login page with an OAuth-specific
 * error query so the UI does not show the form-login failure message.
 *
 * @author Valerius
 */
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private static final Logger log =
            LoggerFactory.getLogger(OAuth2LoginFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {
        String errorCode = "auth_failed";
        String description = exception.getMessage() == null
                ? exception.getClass().getSimpleName()
                : exception.getMessage();

        if (exception instanceof OAuth2AuthenticationException oauthEx) {
            OAuth2Error error = oauthEx.getError();
            if (error != null) {
                if (error.getErrorCode() != null
                        && !error.getErrorCode().isBlank()) {
                    errorCode = error.getErrorCode();
                }
                if (error.getDescription() != null
                        && !error.getDescription().isBlank()) {
                    description = error.getDescription();
                }
            }
        }

        log.warn("OAuth2 login failed: code={}, description={}, uri={}",
                errorCode, description, request.getRequestURI());

        String target = UriComponentsBuilder.fromPath("/login")
                .queryParam("error", "oauth")
                .queryParam("oauthError", errorCode)
                .build()
                .encode()
                .toUriString();
        response.sendRedirect(target);
    }
}
