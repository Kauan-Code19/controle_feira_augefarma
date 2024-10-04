package com.augefarma.controle_feira.unit.services.authorization.component;

import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import com.augefarma.controle_feira.services.authorization.TokenService;
import com.augefarma.controle_feira.services.authorization.component.TokenFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenFilterTest {

    @Mock
    private AdministratorRepository administratorRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TokenFilter tokenFilter;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilterInternal_WithValidToken() throws ServletException, IOException {
        String token = "Bearer valid.token";
        String email = "admin@test.com";
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(tokenService.validateToken("valid.token")).thenReturn(email);
        when(administratorRepository.findByEmail(email)).thenReturn(userDetails);

        tokenFilter.filter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(tokenService).validateToken("valid.token");
        verify(administratorRepository).findByEmail(email);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    public void testDoFilterInternal_WithoutToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        tokenFilter.filter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternal_WithInvalidToken() throws ServletException, IOException {
        String token = "Bearer invalid.token";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(tokenService.validateToken("invalid.token")).thenReturn(null);

        tokenFilter.filter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}