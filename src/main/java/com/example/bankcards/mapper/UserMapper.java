package com.example.bankcards.mapper;

import com.example.bankcards.dto.UserCreationDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserRegistrationDto;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User toUser(UserRegistrationDto dto);

    UserDto toUserDto(User user);

    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User toEntity(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toUser(UserCreationDto dto);
}
