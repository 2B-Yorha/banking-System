package com.Banking.system.TestAuthService;


import com.Banking.system.dto.request.LoginRequest;
import com.Banking.system.dto.request.RegisterRequest;
import com.Banking.system.dto.response.AuthResponse;
import com.Banking.system.entity.User;
import com.Banking.system.enums.Role;
import com.Banking.system.exception.DuplicateEmailException;
import com.Banking.system.exception.RateLimiterException;
import com.Banking.system.repository.UserRepository;
import com.Banking.system.security.JwtUtil;
import com.Banking.system.security.LoginAttemptService;
import com.Banking.system.Service.implementations.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private LoginAttemptService loginAttemptService;



    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private UserDetails userDetails;


    @BeforeEach
    void setUp(){
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Jotaro");
        registerRequest.setLastName("Kujo");
        registerRequest.setEmail("jstarplatinum@gmail.com");
        registerRequest.setPassword("OraOra420");
        registerRequest.setPhone("1234567889");


        loginRequest = new LoginRequest();
        loginRequest.setEmail("jstarplatinum@gmail.com");
        loginRequest.setPassword("OraOra420");


        user = User.builder()
                .id(1L)
                .firstName("Jotaro")
                .lastName("Kujo")
                .email("jstarplatinum@gmail.com")
                .password("OraOra420")
                .role(Role.USER)
                .enabled(true)
                .build();

        userDetails = new org.springframework.security.core.userdetails.User(
                "jstarplatinum@gmail.com", "encodedPassword", List.of()
        );

    }

    //Register Testing:
    @Test
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("mock-jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
        assertThat(response.getEmail()).isEqualTo("jstarplatinum@gmail.com");
        assertThat(response.getRole()).isEqualTo(Role.USER);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("OraOra420");
    }


    @Test
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail("jstarplatinum@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("jstarplatinum@gmail.com");

        verify(userRepository, never()).save(any());


    }





    //Login testing:
    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail("jstarplatinum@gmail.com")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("mock-jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
        assertThat(response.getEmail()).isEqualTo("jstarplatinum@gmail.com");
        assertThat(response.getFullName()).isEqualTo("Jotaro Kujo");

        verify(loginAttemptService).loginSucceeded("jstarplatinum@gmail.com");
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void login_BlockedEmail_ThrowsRateLimiterException() {
        when(loginAttemptService.isBlocked("jstarplatinum@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RateLimiterException.class);

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_BadCredentials_RecordsFailedAttempt() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(loginAttemptService).loginFailed("jstarplatinum@gmail.com");
    }




}
