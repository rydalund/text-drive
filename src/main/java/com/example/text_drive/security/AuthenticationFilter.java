package com.example.text_drive.security;

import com.example.text_drive.model.User;
import com.example.text_drive.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is a custom filter that intercepts HTTP requests to authenticate the user
 * based on a JWT token in the "Authorization" header. It validates the token, retrieves
 * the associated user from the database, and sets up the security context with the user
 * details for further processing of the request.
 */
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;  // Service used for JWT token validation
    private final UserRepository userRepository;  // Repository used to fetch user from the database

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // Check if the header contains a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        try {
            // Validate the token and retrieve the user ID from the token
            UUID userId = jwtService.validateToken(token);
            Optional<User> potentialUser = userRepository.findById(userId);

            // If user is not found, respond with UNAUTHORIZED
            if (potentialUser.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            // Retrieve the user from the database
            User user = potentialUser.get();

            // Create authentication object with user details but no password
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null, // Credentials are not included for security
                            user.getAuthorities()  // Include user authorities (roles and permissions)
                    );

            // Set the authentication details in the security context
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);  // Set authentication in security context

            // Continue the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // If token validation fails or any error occurs, send UNAUTHORIZED response
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid token: " + e.getMessage()
            );
        }
    }
}