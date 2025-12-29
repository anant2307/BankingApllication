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
                req.getInitialBalance()
        );
        return ResponseEntity.ok("Registered. Verify email.");
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
            e.printStackTrace(); // 👈 SEE REAL CAUSE IN CONSOLE
            return ResponseEntity.status(500).body(
                    Map.of(
                        "error", e.getClass().getName(),
                        "message", e.getMessage()
                    )
            );
        }
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        service.verifyEmail(token);
        return ResponseEntity.ok("Email verified");
    }
}
