package com.example.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    
    @NotBlank
    @Size(max = 50)
    private String name;
    
    @NotBlank
    @Size(max = 50)
    private String lastName;
    
    @NotNull
    @Size(max = 50)
    private String surname;
    
    @NotBlank
    @Email
    @Size(max = 50)
    private String login;
    
    @NotBlank
    @Size(min = 8, max = 12)
    private String password;
}