package com.augefarma.controle_feira.services.authentication;

import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
