package com.example.text_drive.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service responsible for handling JWT (JSON Web Token) creation, validation, and related operations.
 * This service generates JWT tokens for users and validates incoming tokens to ensure they are legitimate.
 */
@Service
public class JWTService {

    // Static algorithm and verifier used for signing and verifying JWTs.
    // The secret is injected as a string from the application properties (this way, the secret is externalized and configurable).
    private static final Algorithm algorithm = Algorithm.HMAC256("${jwt.secret}");
    private static final JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build();

    /**
     * Generates a JWT token for a given user.
     * The generated token includes the user's ID as the subject and an expiration time of 30 minutes.
     *
     * @param userId the ID of the user for whom the token is being generated
     * @return a signed JWT token as a String
     */
    public String generateToken(Long userId) {
        return JWT.create()
                .withIssuer("auth0")  // The issuer of the JWT
                .withSubject(userId.toString())  // The subject of the token is the user's ID
                .withExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))  // Set expiration time (30 minutes)
                .sign(algorithm);  // Signs the token using the predefined algorithm
    }

    /**
     * Validates the JWT token by checking its authenticity and expiration.
     * If the token is valid, it returns the user ID from the token.
     *
     * @param token the JWT token to be validated
     * @return the user ID extracted from the token if it is valid
     * @throws com.auth0.jwt.exceptions.JWTVerificationException if the token is invalid or expired
     */
    public Long validateToken(String token) {
        DecodedJWT jwt = verifier.verify(token);  // Verify the token using the JWT verifier
        return Long.parseLong(jwt.getSubject());  // Extract and return the user ID (subject) from the token
    }
}