package com.goMaddy.multithreaded_http_fileserver.security;

import com.goMaddy.multithreaded_http_fileserver.entity.User;
import com.goMaddy.multithreaded_http_fileserver.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserService userService) {

        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
                String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }
    String jwt = authHeader.substring(7);

String userEmail = jwtService.extractEmail(jwt);

User user = userService.getUserByEmail(userEmail);

if (!jwtService.isTokenValid(jwt, user)) {
    filterChain.doFilter(request, response);
    return;
}

UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );

authToken.setDetails(
        new WebAuthenticationDetailsSource()
                .buildDetails(request)
);

SecurityContextHolder
        .getContext()
        .setAuthentication(authToken);

filterChain.doFilter(request, response);
    }
}