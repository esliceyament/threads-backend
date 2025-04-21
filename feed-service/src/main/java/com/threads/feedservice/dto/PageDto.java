package com.threads.feedservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageDto<T> {
    private int page;
    private int size;
    private long totalElements;
    private long totalPages;
    private List<T> content;
}
