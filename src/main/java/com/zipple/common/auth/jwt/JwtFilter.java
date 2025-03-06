package com.zipple.common.auth.jwt;

import com.zipple.common.exception.ErrorCode;
import com.zipple.common.exception.custom.TokenInvalidException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private static final List<String> SWAGGER_WHITELIST = List.of(
            "/swagger-ui/",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars/",
            "/api/auth/kakao",
            "/api/v1/main/"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestURI = httpRequest.getRequestURI();

        if (SWAGGER_WHITELIST.stream().anyMatch(requestURI::startsWith)) {
            chain.doFilter(request, response);
            return;
        }
        String token = resolveToken((HttpServletRequest) request);

        try {
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                throw new TokenInvalidException();
            }
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (TokenInvalidException e) {
            handleException(httpResponse, e.getErrorCode());
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void handleException(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(String.format("{\"status\": %d, \"errorCode\": \"%s\", \"message\": \"%s\"}",
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage()));
    }
}
