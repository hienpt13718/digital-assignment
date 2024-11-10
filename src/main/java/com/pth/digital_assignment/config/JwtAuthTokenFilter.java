package com.pth.digital_assignment.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pth.digital_assignment.dto.auth.UserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.pth.digital_assignment.utils.RequestUtils.getRequestBearerToken;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtAuthTokenFilter extends OncePerRequestFilter {
    private final JsonMapper jsonMapper;
    private final JwtProvider jwtProvider;

    @Override
    public void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        authenticate(request, response);
        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, HttpServletResponse response) {
        String jwtBearerToken = getRequestBearerToken(request);
        boolean isValidJWT = hasText(jwtBearerToken) && jwtProvider.validate(jwtBearerToken);

        if (!isValidJWT) {
            return;
        }

        Claims jwtClaims = jwtProvider.getClaims(jwtBearerToken);
        UserPrincipal principal = jsonMapper.convertValue(jwtClaims, UserPrincipal.class);

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(principal.getAuthorities());

        UserDetails user = User.builder()
                .username(principal.getIdentifier())
                .password(EMPTY)
                .authorities(authorities)
                .build();

        saveAuthentication(user, request);
    }

    private void saveAuthentication(UserDetails user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
