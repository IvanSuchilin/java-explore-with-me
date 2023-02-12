package ru.practicum.ewm.compilation.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdatingCompilationDto {
    private String title;

    private List<Long> events;

    private Boolean pinned;
}
