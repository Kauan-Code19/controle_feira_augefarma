package com.augefarma.controle_feira.config.security;

import com.augefarma.controle_feira.services.authorization.component.TokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenFilter tokenFilter;

    @Autowired
    public SecurityConfig(TokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param httpSecurity the HttpSecurity object to configure security settings
     * @return a configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF protection
                // Configures stateless session management
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Allows public access to the login endpoint
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/realtime").permitAll()
                        .anyRequest().authenticated()) // Requires authentication for all other requests
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)  // Adds custom token filter
                .build();
    }

    /**
     * Provides an AuthenticationManager bean.
     *
     * @param authenticationConfiguration the AuthenticationConfiguration to get the AuthenticationManager
     * @return an AuthenticationManager instance
     * @throws Exception if an error occurs while retrieving the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Provides a PasswordEncoder bean for encoding passwords.
     *
     * @return an Argon2PasswordEncoder instance with specified parameters
     */
    @Bean
    public PasswordEncoder passwordEncoder() { return new Argon2PasswordEncoder(16, 32, 2, 65536, 2); }
}
