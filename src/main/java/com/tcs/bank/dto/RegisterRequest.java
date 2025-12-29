package com.tcs.bank.dto;

import com.tcs.bank.model.IdType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {

    @Size(min = 5, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    private String username;

    @Email
    private String email;

    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",
        message = "Password must contain uppercase, lowercase, digit & special character"
    )
    private String password;

    @PositiveOrZero
    private double initialBalance;

    @NotBlank
    private String fullName;

    @Past
    private LocalDate dateOfBirth;

    @NotBlank
    private String residentialAddress;

    @NotNull
    private IdType idType;

    @NotBlank
    private String idNo;
}
