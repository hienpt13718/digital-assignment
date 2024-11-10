package com.pth.digital_assignment.utils;

import com.pth.digital_assignment.exception.AuthException;
import com.pth.digital_assignment.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

import static com.pth.digital_assignment.constant.Constants.AUTHORIZATION_HEADER;
import static com.pth.digital_assignment.constant.Constants.BEARER_TOKEN_PREFIX;
import static org.springframework.util.StringUtils.hasText;

public final class RequestUtils {
    private RequestUtils() {}

    public static String getRequestBearerToken(HttpServletRequest request) {
        var bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        var hasToken = hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX);
        return hasToken ? bearerToken.substring(7) : null;
    }

    public static UUID paramToUUID(String uuidParam) {
        try {
            return UUID.fromString(uuidParam);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("INVALID_PARAMETER", "Invalid parameter: " + uuidParam);
        }
    }

    public static String getCurrentUserIdentifier(Authentication authentication) {
        if (authentication.getPrincipal() != null
            && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        throw new AuthException("UNAUTHORIZED", "Unauthorized");
    }
}
