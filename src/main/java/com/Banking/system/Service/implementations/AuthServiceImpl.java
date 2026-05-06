package com.Banking.system.Service.implementations;

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
import com.Banking.system.Service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.USER)
                .enabled(true)
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole())
                .build();
    }


    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail();

        if (loginAttemptService.isBlocked(email)) {
            throw new RateLimiterException("Too many failed login attempts. Try again after "
                    + loginAttemptService.getLockExpiryTime(email));
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
            loginAttemptService.loginSucceeded(email);
        } catch (BadCredentialsException ex) {
            loginAttemptService.loginFailed(email);

            if (loginAttemptService.isBlocked(email)) {
                throw new RateLimiterException("Too many failed login attempts. Try again after "
                        + loginAttemptService.getLockExpiryTime(email));
            }

            throw ex;
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .role(user.getRole())
                .build();
    }


}
