package com.divyanka.payment_gateway.service;

import com.divyanka.payment_gateway.dto.request.LoginRequest;
import com.divyanka.payment_gateway.dto.request.RegisterRequest;
import com.divyanka.payment_gateway.dto.response.AuthResponse;
import com.divyanka.payment_gateway.entity.Role;
import com.divyanka.payment_gateway.entity.User;
import com.divyanka.payment_gateway.repository.UserRepository;
import com.divyanka.payment_gateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
            .email(req.getEmail())
            .password(passwordEncoder.encode(req.getPassword()))
            .role(req.getRole() != null
                ? Role.valueOf(req.getRole().toUpperCase())
                : Role.CUSTOMER)
            .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(
            org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build());

        return AuthResponse.builder()
            .token(token)
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found"));

        String token = jwtUtil.generateToken(
            org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build());

        return AuthResponse.builder()
            .token(token)
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();
    }
}