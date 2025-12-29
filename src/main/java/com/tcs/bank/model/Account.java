package com.tcs.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


    @Column(unique = true, nullable = false)
    private Long accountNumber;


    @NotBlank
    @Size(min = 5, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    @Column(unique = true)
    private String username;

    @NotBlank
    private String passwordHash;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    private boolean emailVerified;

    private String verificationToken;

    private double balance;
}
