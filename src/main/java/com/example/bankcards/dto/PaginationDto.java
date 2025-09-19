package com.example.bankcards.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginationDto<T> {
    private int totalPages;
    private long totalElements;
    private List<T> elements;
}
