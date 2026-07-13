package com.goMaddy.multithreaded_http_fileserver.rateLimit;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final BucketService bucketService;

    public RateLimitFilter(BucketService bucketService) {
        this.bucketService = bucketService;
    }

   @Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    String path = request.getRequestURI();

    // Skip rate limiting for public resources
    if (path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs")
            || path.equals("/swagger-ui.html")
            || path.startsWith("/favicon")
            || path.equals("/api/users/login")
            || path.equals("/api/users/register")) {

        filterChain.doFilter(request, response);
        return;
    }

    String clientIp = request.getRemoteAddr();

    Bucket bucket = bucketService.resolveBucket(clientIp);

    if (bucket.tryConsume(1)) {
        filterChain.doFilter(request, response);
    } else {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");

        response.getWriter().write("""
            {
              "status":429,
              "error":"Too Many Requests",
              "message":"Rate limit exceeded. Please try again later."
            }
            """);
    }
}
}