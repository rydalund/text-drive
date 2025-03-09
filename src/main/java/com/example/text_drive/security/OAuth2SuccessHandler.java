package com.example.text_drive.security;

import com.example.text_drive.model.User;
import com.example.text_drive.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Handles successful OAuth2 authentication by creating or retrieving a user
 * and generating a JWT token for the authenticated user.
 */
@Service
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;  // Service for user-related operations
    private final JWTService jwtService;   // Service for JWT token generation and validation

    /**
     * Handles successful OAuth2 authentication.
     * Retrieves user information from the OAuth2 provider, creates or retrieves a user,
     * and generates a JWT token for the authenticated user.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response.
     * @param authentication The authentication object containing OAuth2 user details.
     * @throws IOException If an input or output exception occurs.
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        // Extract the OAuth2 authentication token from the authentication object
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauth2Token.getPrincipal();

        // Get the OAuth2 provider (e.g., "GitHub")
        String oidcProvider = oauth2Token.getAuthorizedClientRegistrationId();
        // Get the OAuth2 user's unique ID
        String oidcId = oAuth2User.getName();
        // Get the OAuth2 user's username (e.g., GitHub username)
        String username = oAuth2User.getAttribute("login");
        // Get the OAuth2 user's email (if available)
        String email = oAuth2User.getAttribute("email");

        // Check if the user already exists in the database
        Optional<User> existingUser = userService.findByOpenId(oidcId);
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // Create a new user with OAuth2 information
            user = userService.createOpenIdUser(username, email, oidcId, oidcProvider);
            System.out.println("Registered user '" + user.getUsername() + "' through OpenID-connect!");
        }

        // Generate a JWT token for the authenticated user
        String token = jwtService.generateToken(user.getId());

        // Send the token back to the client as a JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Create a JSON object containing the token
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("token", token);

        // Convert the JSON object to a string and write it to the response
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
    }
}