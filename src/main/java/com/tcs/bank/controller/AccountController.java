package com.tcs.bank.controller;

import com.tcs.bank.model.Account;
import com.tcs.bank.service.BankingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final BankingService service;

    public AccountController(BankingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAccount(Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid JWT");
        }

        Account acc = service.getAccountByUsername(authentication.getName());
        return ResponseEntity.ok(acc);
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(Authentication authentication,
                                     @RequestParam double amount) {

        if (authentication == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid JWT");
        }

        service.depositByUsername(authentication.getName(), amount);
        return ResponseEntity.ok("Deposit successful");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(Authentication authentication,
                                      @RequestParam double amount) {

        if (authentication == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid JWT");
        }

        service.withdrawByUsername(authentication.getName(), amount);
        return ResponseEntity.ok("Withdrawal successful");
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(Authentication authentication,
                                      @RequestParam Long targetAccountNumber,
                                      @RequestParam double amount) {

        if (authentication == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid JWT");
        }

        service.transferByUsername(authentication.getName(), targetAccountNumber, amount);
        return ResponseEntity.ok("Transfer successful");
    }
}
