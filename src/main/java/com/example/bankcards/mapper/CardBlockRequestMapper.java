package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardBlockRequestDto;
import com.example.bankcards.entity.CardBlockRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardBlockRequestMapper {

    CardBlockRequestDto toDto(CardBlockRequest cardBlockRequest);

    CardBlockRequest toEntity(CardBlockRequestDto cardBlockRequestDto);
}
