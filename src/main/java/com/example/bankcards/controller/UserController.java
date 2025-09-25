package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserPasswordDto;
import com.example.bankcards.dto.UserRegistrationDto;
import com.example.bankcards.entity.UserStatus;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
@Tag(name = "User", description = "User API")
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;

  @Operation(summary = "Register a new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully registered user"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "409", description = "User already exists")
  })
  @PostMapping("registration")
  public void userRegistration(@Valid @RequestBody UserRegistrationDto dto) {
    userService.userRegistration(userMapper.toUser(dto));
  }

  @Operation(summary = "Create a new user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created user"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "409", description = "User already exists")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("create")
  public void createUser(@Valid @RequestBody UserCreationDto dto) {
    userService.createUser(userMapper.toUser(dto));
  }

  @Operation(summary = "Delete a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("{userId}")
  public void deleteUser(@Parameter(description = "ID of the user") @NotNull @PathVariable("userId") Long id) {
    userService.deleteUser(id);
  }

  @Operation(summary = "Block a user", description = "Changes the user's status to BLOCKED.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully blocked user"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("{userId}/block")
  public void blockUser(@Parameter(description = "ID of the user to block") @NotNull @PathVariable("userId") Long userId) {
      userService.changeUserStatus(userId, UserStatus.BLOCKED);
  }

  @Operation(summary = "Activate a user", description = "Changes the user's status to ACTIVE.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully activated user"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("{userId}/activate")
  public void activateUser(@Parameter(description = "ID of the user to activate") @NotNull @PathVariable("userId") Long userId) {
    userService.changeUserStatus(userId, UserStatus.ACTIVE);
  }

  @Operation(summary = "Get all users")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("list")
  public List<UserDto> getAllUsers() {
    return userService.getAllUsers().stream().map(userMapper::toUserDto).toList();
  }

  @Operation(summary = "Update a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated user"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping
  public void updateUser(@Valid @RequestBody UserDto dto) {
    userService.updateUser(userMapper.toEntity(dto));
  }

  @Operation(summary = "Change user password")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully changed password"),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PatchMapping
  public void changeUserPassword(@Valid @RequestBody UserPasswordDto dto) {
    userService.changeUserPassword(dto.getUserId(), dto.getPassword());
  }

  @Operation(summary = "Block all cards for a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully blocked cards"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("cards/block")
  public void blockUserCards(@Parameter(description = "ID of the user") @NotNull @RequestParam Long userId) {
    userService.blockUserCards(userId);
  }
}
