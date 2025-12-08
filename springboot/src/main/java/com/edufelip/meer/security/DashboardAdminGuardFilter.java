package com.edufelip.meer.security;

import com.edufelip.meer.core.auth.Role;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.security.token.InvalidTokenException;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Ensures all /dashboard/** routes (except /dashboard/login) are accessed by ADMIN users only.
 */
public class DashboardAdminGuardFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final AuthUserRepository authUserRepository;

    public DashboardAdminGuardFilter(TokenProvider tokenProvider, AuthUserRepository authUserRepository) {
        this.tokenProvider = tokenProvider;
        this.authUserRepository = authUserRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if (!isDashboardPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing bearer token");
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        try {
            TokenPayload payload = tokenProvider.parseAccessToken(token);
            var userOpt = authUserRepository.findById(payload.getUserId());
            if (userOpt.isEmpty()) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }
            var user = userOpt.get();

            Role role = user.getRole() != null ? user.getRole() : (payload.getRole() != null ? payload.getRole() : Role.USER);
            if (role != Role.ADMIN) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN, "Admin only");
                return;
            }

            // make user available downstream to avoid re-querying
            request.setAttribute("adminUser", user);
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException ex) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private boolean isDashboardPath(String path) {
        if (path == null) return false;
        String lower = path.toLowerCase();
        if ("/dashboard/login".equals(lower)) return false; // public dashboard auth
        return lower.startsWith("/dashboard");
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.sendError(status, message);
    }
}
