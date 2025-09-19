package com.example.bankcards.mapper;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {

    @Mapping(source = "balance.balance", target = "balance")
    @Mapping(target = "number", expression = "java(card.hideNumber())")
    AdminCardDto toAdminCardDto(Card card);

    @Mapping(source = "balance.balance", target = "balance")
    UserCardDto toUserCardDto(Card card);
}
