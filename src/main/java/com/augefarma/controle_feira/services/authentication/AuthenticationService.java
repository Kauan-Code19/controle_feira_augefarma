package com.augefarma.controle_feira.services.authentication;

import com.augefarma.controle_feira.exceptions.InvalidCredentialsException;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    private final AdministratorRepository administratorRepository;

    @Autowired
    public AuthenticationService(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    /**
     * Loads a user by their username (email).
     *
     * @param email the email of the user to be retrieved
     * @return UserDetails containing the user's information
     * @throws UsernameNotFoundException if the user with the given email does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        try {
            // Attempts to retrieve an AdministratorEntity by the provided email
            return administratorRepository.findByEmail(email);
        } catch (UsernameNotFoundException exception) {
            // If the email is not found, throw a custom ResourceNotFoundException
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    /**
     * Authenticates the user using the AuthenticationManager.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @param authenticationManager the AuthenticationManager for authentication
     * @return Authentication object containing the details of the authenticated user
     * @throws InvalidCredentialsException if the credentials are invalid
     */
    public Authentication authenticate(String email, String password, AuthenticationManager authenticationManager) {
        try {
            // Attempts to authenticate the user with the provided email and password
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (InternalAuthenticationServiceException | BadCredentialsException exception) {
            // Throws a custom exception if the credentials are invalid
            throw new InvalidCredentialsException("Invalid credentials");
        }
    }
}
