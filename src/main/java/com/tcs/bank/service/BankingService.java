package com.tcs.bank.service;

import com.tcs.bank.model.Account;
import com.tcs.bank.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class BankingService {

    private final AccountRepository repo;
    private final PasswordEncoder encoder;

    public BankingService(AccountRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    private Long generateAccountNumber() {
        long min = 1_000_000_000L;
        long max = 9_999_999_999L;

        long number;
        do {
            number = min + (long) (Math.random() * (max - min));
        } while (repo.findByAccountNumber(number) != null);

        return number;
    }

    public Account register(String username, String email, String password, double balance) {

        if (repo.findByUsername(username) != null)
            throw new IllegalArgumentException("Username exists");

        Account acc = new Account();
        acc.setAccountNumber(generateAccountNumber());
        acc.setUsername(username);
        acc.setEmail(email);
        acc.setPasswordHash(encoder.encode(password));
        acc.setBalance(balance);
        acc.setEmailVerified(false);
        acc.setVerificationToken(UUID.randomUUID().toString());

        return repo.save(acc);
    }

    public Account login(String username, String password) {
        Account acc = repo.findByUsername(username);
        if (acc == null || !acc.isEmailVerified())
            return null;
        return encoder.matches(password, acc.getPasswordHash()) ? acc : null;
    }

    public void verifyEmail(String token) {
        Account acc = repo.findByVerificationToken(token);
        if (acc == null)
            throw new IllegalArgumentException("Invalid token");
        acc.setEmailVerified(true);
        acc.setVerificationToken(null);
        repo.save(acc);
        acc.setVerificationToken(null);
    }


    public Account getAccountByUsername(String username) {
        Account acc = repo.findByUsername(username);
        if (acc == null)
            throw new IllegalArgumentException("Account not found");
        return acc;
    }

    public void depositByUsername(String username, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount");

        Account acc = getAccountByUsername(username);
        acc.setBalance(acc.getBalance() + amount);
        repo.save(acc);
    }

    public void withdrawByUsername(String username, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount");

        Account acc = getAccountByUsername(username);
        if (acc.getBalance() < amount)
            throw new IllegalArgumentException("Insufficient balance");

        acc.setBalance(acc.getBalance() - amount);
        repo.save(acc);
    }

    public void transferByUsername(String sourceUsername, Long targetAccountNumber, double amount) {

        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount");

        Account source = getAccountByUsername(sourceUsername);
        Account target = repo.findByAccountNumber(targetAccountNumber);

        if (target == null)
            throw new IllegalArgumentException("Target account not found");

        if (source.getAccountNumber().equals(targetAccountNumber))
            throw new IllegalArgumentException("Cannot transfer to same account");

        if (source.getBalance() < amount)
            throw new IllegalArgumentException("Insufficient balance");

        source.setBalance(source.getBalance() - amount);
        target.setBalance(target.getBalance() + amount);

        repo.save(source);
        repo.save(target);
    }
}
