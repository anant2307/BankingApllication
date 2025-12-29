package com.tcs.bank.service;

import com.tcs.bank.model.*;
import com.tcs.bank.repository.AccountRepository;
import com.tcs.bank.repository.TransactionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BankingService {

    private final AccountRepository repo;
    private final TransactionRepository transactionRepo;
    private final PasswordEncoder encoder;

    public BankingService(AccountRepository repo,
            TransactionRepository transactionRepo,
            PasswordEncoder encoder) {
        this.repo = repo;
        this.transactionRepo = transactionRepo;
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

    public Account register(String username, String email, String password,
            double balance, String fullName,
            java.time.LocalDate dateOfBirth,
            String residentialAddress,
            IdType idType, String idNo) {

        if (repo.findByUsername(username) != null)
            throw new IllegalArgumentException("Username already exists");

        Account acc = new Account();
        acc.setAccountNumber(generateAccountNumber());
        acc.setUsername(username);
        acc.setEmail(email);
        acc.setPasswordHash(encoder.encode(password));
        acc.setBalance(balance);
        acc.setFullName(fullName);
        acc.setDateOfBirth(dateOfBirth);
        acc.setResidentialAddress(residentialAddress);
        acc.setIdType(idType);
        acc.setIdNo(idNo);

        return repo.save(acc);
    }

    public Account login(String username, String password) {
        Account acc = repo.findByUsername(username);
        if (acc == null)
            return null;
        return encoder.matches(password, acc.getPasswordHash()) ? acc : null;
    }

    public Account getAccountByUsername(String username) {
        Account acc = repo.findByUsername(username);
        if (acc == null)
            throw new IllegalArgumentException("Account not found");
        return acc;
    }

    private void recordTransaction(Account acc, double amount,
            TransactionType type, String description) {

        Transaction tx = new Transaction();
        tx.setAccount(acc);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setDescription(description);
        tx.setTimestamp(LocalDateTime.now());

        transactionRepo.save(tx);
    }

    public void depositByUsername(String username, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount");

        Account acc = getAccountByUsername(username);
        acc.setBalance(acc.getBalance() + amount);
        repo.save(acc);

        recordTransaction(acc, amount,
                TransactionType.DEPOSIT, "Amount deposited");
    }

    public void withdrawByUsername(String username, double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Invalid amount");

        Account acc = getAccountByUsername(username);
        if (acc.getBalance() < amount)
            throw new IllegalArgumentException("Insufficient balance");

        acc.setBalance(acc.getBalance() - amount);
        repo.save(acc);

        recordTransaction(acc, amount,
                TransactionType.WITHDRAW, "Amount withdrawn");
    }

    public void transferByUsername(String sourceUsername,
            Long targetAccountNumber,
            double amount) {

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
        repo.save(source);

        recordTransaction(source, amount,
                TransactionType.WITHDRAW,
                "Transfer to account " + targetAccountNumber);

        target.setBalance(target.getBalance() + amount);
        repo.save(target);

        recordTransaction(target, amount,
                TransactionType.DEPOSIT,
                "Transfer from account " + source.getAccountNumber());
    }

    public List<Transaction> getTransactionHistory(Long accountId) {
        return transactionRepo.findByAccountIdOrderByTimestampDesc(accountId);
    }
}
