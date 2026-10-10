package com.aponti.ciclo_circadiano.service;

import com.aponti.ciclo_circadiano.config.JwtService;
import com.aponti.ciclo_circadiano.dto.AuthResponse;
import com.aponti.ciclo_circadiano.dto.LoginRequest;
import com.aponti.ciclo_circadiano.dto.UserRequest;
import com.aponti.ciclo_circadiano.exception.EmailAlreadyRegisteredException;
import com.aponti.ciclo_circadiano.exception.InvalidCredentialsException;
import com.aponti.ciclo_circadiano.model.UserModel;
import com.aponti.ciclo_circadiano.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(UserRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException("E-mail já cadastrado");
        }

        UserModel user = userRepository.save(
                new UserModel(null, request.nome().trim(), email, passwordEncoder.encode(request.senha()), null)
        );
        return createAuthResponse(user.getEmail());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        UserModel user = userRepository.findByEmail(email)
                .filter(candidate -> passwordEncoder.matches(request.senha(), candidate.getSenha()))
                .orElseThrow(() -> new InvalidCredentialsException("E-mail ou senha inválidos"));

        return createAuthResponse(user.getEmail());
    }

    private AuthResponse createAuthResponse(String email) {
        return new AuthResponse(jwtService.generateToken(email), "Bearer", jwtService.getExpirationSeconds());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
