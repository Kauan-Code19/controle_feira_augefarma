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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    public void testDoFilterInternal_WithValidToken() throws ServletException, IOException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        String token = "Bearer valid.token";
        String email = "admin@test.com";
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(tokenService.validateToken("valid.token")).thenReturn(email);
        when(administratorRepository.findByEmail(email)).thenReturn(userDetails);

        Method method = TokenFilter.class.getDeclaredMethod("doFilterInternal", HttpServletRequest.class,
                HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);

        method.invoke(tokenFilter, request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(tokenService).validateToken("valid.token");
        verify(administratorRepository).findByEmail(email);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    public void testDoFilterInternal_WithoutToken() throws ServletException, IOException, InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {
        when(request.getHeader("Authorization")).thenReturn(null);

        Method method = TokenFilter.class.getDeclaredMethod("doFilterInternal", HttpServletRequest.class,
                HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);

        method.invoke(tokenFilter, request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}