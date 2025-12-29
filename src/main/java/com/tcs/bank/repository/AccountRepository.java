package com.tcs.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcs.bank.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Account findByUsername(String username);

	Account findByAccountNumber(Long accountNumber);

	Account findByVerificationToken(String token);
}
