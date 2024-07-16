package com.augefarma.controle_feira.services.authorization.component;

import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import com.augefarma.controle_feira.services.authorization.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private final AdministratorRepository administratorRepository;
    private final TokenService tokenService;

    @Autowired
    public TokenFilter(AdministratorRepository administratorRepository, TokenService tokenService) {
        this.administratorRepository = administratorRepository;
        this.tokenService = tokenService;
    }

    /**
     * Filters incoming HTTP requests to check for a valid authentication token.
     *
     * @param request   the HttpServletRequest to filter
     * @param response  the HttpServletResponse to use
     * @param filterChain the FilterChain to continue the request processing
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var token = this.recoverToken(request); // Retrieve the token from the request

        if (token != null) {
            var email = tokenService.validateToken(token); // Validate the token and get the associated email

            UserDetails userDetails = administratorRepository.findByEmail(email); // Retrieve user details by email

            // Create authentication object and set it in the security context
            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());

            // Set authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response); // Continue with the filter chain
    }

    /**
     * Extracts the token from the Authorization header.
     *
     * @param request the HttpServletRequest from which to extract the token
     * @return the extracted token or null if not found
     */
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");

        if (authHeader == null) return null; // Return null if no Authorization header

        return authHeader.replace("Bearer ", ""); // Remove "Bearer " prefix and return the token
    }
}
