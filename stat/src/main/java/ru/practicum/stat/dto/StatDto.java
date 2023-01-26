package ru.practicum.stat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatDto {
    private String app;
    private String uri;
    private int hits;
}
