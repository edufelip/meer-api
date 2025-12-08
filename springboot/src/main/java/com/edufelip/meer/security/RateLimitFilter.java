package com.edufelip.meer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Set<String> GUARDED_POST_PATHS = Set.of(
            "/auth/login",
            "/auth/signup",
            "/auth/forgot-password",
            "/auth/refresh",
            "/auth/google",
            "/auth/apple",
            "/dashboard/login"
    );

    private static final int WINDOW_SECONDS = 60;
    private static final int MAX_REQUESTS = 10;

    private static class Counter {
        int count;
        long windowStartEpochSec;
    }

    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    private boolean isAllowed(String key) {
        long now = Instant.now().getEpochSecond();
        Counter c = counters.computeIfAbsent(key, k -> {
            Counter n = new Counter();
            n.windowStartEpochSec = now;
            n.count = 0;
            return n;
        });

        synchronized (c) {
            if (now - c.windowStartEpochSec >= WINDOW_SECONDS) {
                c.windowStartEpochSec = now;
                c.count = 0;
            }
            c.count++;
            return c.count <= MAX_REQUESTS;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        return GUARDED_POST_PATHS.stream().noneMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String token = request.getHeader("Authorization");

        String key = path + "|" + ip;
        if (token != null && !token.isBlank()) {
            key = key + "|" + token.hashCode();
        }

        if (!isAllowed(key)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please try again later.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
