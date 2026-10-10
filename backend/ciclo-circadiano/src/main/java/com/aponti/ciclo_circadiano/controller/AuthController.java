package com.aponti.ciclo_circadiano.controller;

import com.aponti.ciclo_circadiano.dto.AuthResponse;
import com.aponti.ciclo_circadiano.dto.ErrorResponse;
import com.aponti.ciclo_circadiano.dto.LoginRequest;
import com.aponti.ciclo_circadiano.dto.UserRequest;
import com.aponti.ciclo_circadiano.exception.EmailAlreadyRegisteredException;
import com.aponti.ciclo_circadiano.exception.InvalidCredentialsException;
import com.aponti.ciclo_circadiano.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
        } catch (EmailAlreadyRegisteredException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(exception.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (InvalidCredentialsException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(exception.getMessage()));
        }
    }
}