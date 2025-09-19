package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String lastName;

    @NotNull
    private String surname;

    @NotNull
    private String login;

    @NotNull
    private UserStatus status;

    @NotNull
    private Role role;

    @NotNull
    private LocalDateTime createdAt;
}
