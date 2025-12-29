package com.tcs.bank.controller;

import com.tcs.bank.dto.LoginRequest;
import com.tcs.bank.dto.RegisterRequest;
import com.tcs.bank.model.Account;
import com.tcs.bank.security.JwtUtil;
import com.tcs.bank.service.BankingService;
import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final BankingService service;
    private final JwtUtil jwtUtil;

    public AuthController(BankingService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req) {

        service.register(
                req.getUsername(),
                req.getEmail(),
                req.getPassword(),
                req.getInitialBalance(),
                req.getFullName(),
                req.getDateOfBirth(),
                req.getResidentialAddress(),
                req.getIdType(),
                req.getIdNo()
        );

        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        try {
            Account acc = service.login(req.getUsername(), req.getPassword());

            if (acc == null) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }

            String token = jwtUtil.generateToken(acc.getUsername());
            return ResponseEntity.ok(token);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of(
                            "error", e.getClass().getName(),
                            "message", e.getMessage()));
        }
    }
}
