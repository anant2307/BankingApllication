package com.tcs.bank.controller;

import com.tcs.bank.model.Account;
import com.tcs.bank.service.BankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "http://localhost:4200")
public class AccountController {

    private final BankingService service;

    public AccountController(BankingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAccount(Authentication authentication) {
        if (authentication == null)
            return ResponseEntity.status(401).body("Unauthorized");

        return ResponseEntity.ok(
                service.getAccountByUsername(authentication.getName()));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(Authentication authentication,
                                     @RequestParam double amount) {

        service.depositByUsername(authentication.getName(), amount);
        return ResponseEntity.ok("Deposit successful");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(Authentication authentication,
                                      @RequestParam double amount) {

        service.withdrawByUsername(authentication.getName(), amount);
        return ResponseEntity.ok("Withdrawal successful");
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(Authentication authentication,
                                      @RequestParam Long targetAccountNumber,
                                      @RequestParam double amount) {

        service.transferByUsername(authentication.getName(),
                targetAccountNumber, amount);

        return ResponseEntity.ok("Transfer successful");
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(Authentication authentication) {

        Account acc = service.getAccountByUsername(authentication.getName());
        return ResponseEntity.ok(
                service.getTransactionHistory(acc.getId()));
    }
}
