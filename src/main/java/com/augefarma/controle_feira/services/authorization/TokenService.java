package com.augefarma.controle_feira.services.authorization;

import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.exceptions.JWTGenerationException;
import com.augefarma.controle_feira.exceptions.JWTValidException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret; // Secret key for signing the JWT

    /**
     * Generates a JWT token for an administrator.
     *
     * @param administrator the administrator entity for whom the token is generated
     * @return the generated JWT token as a String
     * @throws JWTGenerationException if there is an error during token creation
     */
    public String generateTokenAdministrator(AdministratorEntity administrator) {
        try {
            // Define the algorithm with the secret key for signing the JWT
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Create and sign the JWT with the specified claims and return it as a String
            return JWT.create()
                    .withIssuer("augefarma") // Set the issuer of the token
                    .withSubject(administrator.getEmail()) // Set the subject of the token to the administrator's email
                    .withClaim("role", "ROLE_ADMIN") // Add a custom claim for the administrator's role
                    .withIssuedAt(new Date()) // Set the issued at date to the current date and time
                    .withExpiresAt(generateExpirationDate()) // Set the expiration date using the helper method
                    .sign(algorithm); // Sign the token with the specified algorithm
        } catch (IllegalArgumentException exception) {
            // Throw a custom exception if there is an error during token creation
            throw new JWTGenerationException("token generation failure");
        }
    }

    /**
     * Validates the provided JWT token and returns the subject (administrator's email).
     *
     * @param token the JWT token to be validated
     * @return the subject (email) extracted from the valid token
     * @throws JWTValidException if the token is invalid
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("augefarma")
                    .build()
                    .verify(token) // Verify the token's validity
                    .getSubject(); // Extract and return the subject (administrator's email)
        } catch (JWTVerificationException exception) {
            // Throw a custom exception if the token is not valid
            throw new JWTValidException("token is not valid");
        }
    }

    /**
     * Generates the expiration date for the JWT token.
     *
     * @return the expiration date as an Instant, set to 1 hour from the current time
     */
    private Instant generateExpirationDate() {
        // Returns the current time plus 1 hour as the expiration date, adjusted to the specified time zone
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }
}
