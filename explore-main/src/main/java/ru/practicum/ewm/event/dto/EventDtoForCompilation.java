package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDtoForCompilation {
    private Long id;
    private String annotation;
    private Category category;
    private Long confirmedRequests;
    private User initiator;
    private Boolean paid;
    private String title;
    private Integer views;
}
