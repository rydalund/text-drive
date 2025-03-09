package com.example.text_drive.security;

import com.example.text_drive.repository.UserRepository;
import com.example.text_drive.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * Configuration class responsible for configuring Spring Security.
 * This class defines security settings such as HTTP request authorization,
 * session management, authentication filters, and password encoding.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures security settings for HTTP requests, such as which endpoints
     * are publicly accessible and which require authentication.
     *
     * @param http The HttpSecurity object used to configure security settings.
     * @param jwtService The service used to manage JWT tokens.
     * @param userRepository Repository used for user-related data access.
     * @param userService The service responsible for loading user details.
     * @return The SecurityFilterChain configured with the security settings.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JWTService jwtService,
            UserRepository userRepository,
            UserService userService,
            OAuth2SuccessHandler oAuth2SuccessHandler
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .userDetailsService(userService)
                .securityContext(context -> context
                        .requireExplicitSave(false)
                )
                // Configure session management to be stateless, as JWT will handle the authentication state.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Define authorization rules for specific HTTP requests.
                .authorizeHttpRequests(auth -> auth
                        // Allow unrestricted access to the POST /user and POST /user/login endpoints.
                        .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users/login").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        // Only users with the "ROLE_ADMIN" authority can delete folders.
                        .requestMatchers(HttpMethod.DELETE, "/folders/**").hasAuthority("ROLE_ADMIN")
                        // All other requests require authentication.
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> {
                    oauth.successHandler(oAuth2SuccessHandler);
                })

                .addFilterBefore(
                        new AuthenticationFilter(jwtService, userRepository),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Provides a password encoder used for encoding passwords in the application.
     * In this case, we are using BCryptPasswordEncoder to securely hash and check passwords.
     *
     * @return A PasswordEncoder instance (BCryptPasswordEncoder).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Return an instance of BCryptPasswordEncoder.
    }
}