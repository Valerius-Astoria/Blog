package com.valerius.blog.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

/**
 * Stores the OAuth2 authorization request (including PKCE verifier) in a
 * cookie instead of the HTTP session.
 * <p>
 * Session-backed storage fails when the session is lost between the
 * provider redirect and the callback (common on free-tier hosts that
 * recycle instances). A short-lived {@code Lax} cookie survives that gap
 * for the top-level callback navigation.
 *
 * @author Valerius
 */
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    static final String COOKIE_NAME = "OAUTH2_AUTH_REQUEST";
    private static final int COOKIE_MAX_AGE_SECONDS = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(
            HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");
        String stateParameter = request.getParameter(OAuth2ParameterNames.STATE);
        if (stateParameter == null) {
            return null;
        }
        OAuth2AuthorizationRequest authorizationRequest = readCookie(request);
        if (authorizationRequest != null
                && stateParameter.equals(authorizationRequest.getState())) {
            return authorizationRequest;
        }
        return null;
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        Assert.notNull(request, "request cannot be null");
        Assert.notNull(response, "response cannot be null");
        if (authorizationRequest == null) {
            writeCookie(request, response, "", 0);
            return;
        }
        Assert.hasText(authorizationRequest.getState(),
                "authorizationRequest.state cannot be empty");
        writeCookie(request, response, serialize(authorizationRequest),
                COOKIE_MAX_AGE_SECONDS);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request,
            HttpServletResponse response) {
        Assert.notNull(response, "response cannot be null");
        OAuth2AuthorizationRequest authorizationRequest =
                loadAuthorizationRequest(request);
        if (authorizationRequest != null) {
            writeCookie(request, response, "", 0);
        }
        return authorizationRequest;
    }

    private static OAuth2AuthorizationRequest readCookie(
            HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())
                    && cookie.getValue() != null
                    && !cookie.getValue().isBlank()) {
                return deserialize(cookie.getValue());
            }
        }
        return null;
    }

    private static void writeCookie(HttpServletRequest request,
            HttpServletResponse response, String value, int maxAge) {
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }

    private static String serialize(OAuth2AuthorizationRequest request) {
        byte[] bytes = SerializationUtils.serialize(request);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static OAuth2AuthorizationRequest deserialize(String value) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(value);
            Object object = SerializationUtils.deserialize(bytes);
            if (object instanceof OAuth2AuthorizationRequest authorizationRequest) {
                return authorizationRequest;
            }
            return null;
        }
        catch (RuntimeException ex) {
            return null;
        }
    }
}
