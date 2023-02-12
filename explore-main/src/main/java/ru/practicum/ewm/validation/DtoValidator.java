package ru.practicum.ewm.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryShortDto;
import ru.practicum.ewm.event.dto.EventUpdateAdminDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.IncorrectlyDateStateRequestException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.PartialRequestException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.RequestValidationException;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Component
public class DtoValidator {

    public void updValidationDtoForAdmin(Event stored, EventUpdateAdminDto eventUpdateAdminDto){
        if (!Objects.equals(Event.State.PENDING, stored.getState())) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять можно неопубликованные события",
                    LocalDateTime.now());
        }
        if (stored.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IncorrectlyDateStateRequestException(
                    "Неверно указана дата события",
                    "Дата события не может быть менее чем за 1 час до начала",
                    LocalDateTime.now());
        }
        if (eventUpdateAdminDto.getEventDate() != null) {
            if (eventUpdateAdminDto.getEventDate().isBefore(LocalDateTime.now())) {
                throw new IncorrectlyDateStateRequestException(
                        "Условия выполнения не соблюдены",
                        "Новое время в прошлом",
                        LocalDateTime.now());
            }
        }
    }

    public void updValidationDtoForUser(Long userId, EventUpdateDto eventUpdateDto, Event stored) {
        if (!stored.getInitiator().getId().equals(userId)) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять может только владелец",
                    LocalDateTime.now());
        }
        if (stored.getState().equals(Event.State.PUBLISHED)) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять можно неопубликованные события",
                    LocalDateTime.now());
        }
        if (eventUpdateDto.getEventDate() != null) {
            if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new IncorrectlyDateStateRequestException(
                        "Условия выполнения не соблюдены",
                        "Изменять можно события за 2 часа до начала",
                        LocalDateTime.now());
            }
        }
        if (stored.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять можно события за 2 часа до начала",
                    LocalDateTime.now());
        }
        if (stored.getParticipantLimit() == 0) {
            throw new PartialRequestException("Мест нет",
                    "Нет свободных мест в событиии", LocalDateTime.now());
        }
    }

    public void validateCategory(CategoryDto category) {
        if (category.getName() == null || category.getName().isEmpty()) {
            throw new RequestValidationException(
                    "Не указано имя категории",
                    "Имя категории пустое",
                    LocalDateTime.now()
            );
        }
    }

    public void validateCategoryForUpd(CategoryShortDto category) {
        if (category.getName() == null || category.getName().isEmpty()) {
            throw new RequestValidationException(
                    "Не указано имя категории",
                    "Имя категории пустое",
                    LocalDateTime.now()
            );
        }
    }

    public void validateUserDto(UserDto user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new RequestValidationException(
                    "Не указано имя пользователя",
                    "Имя пользователя пустое",
                    LocalDateTime.now()
            );
        }
    }

    public void validateNewEventDto(NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectlyDateStateRequestException(
                    "Неверно указана дата события",
                    "Дата события не может быть в прошлом или ранее 2-х часов",
                    LocalDateTime.now()
            );
        }
        if (newEventDto.getCategory() <= 0) {
            throw new RequestValidationException(
                    "Неверно указана категория указано имя пользователя",
                    "Имя пользователя пустое",
                    LocalDateTime.now()
            );
        }
    }

    public void validateUpdateEventDto(EventUpdateDto eventUpdateDto) {
        if (null != eventUpdateDto.getEventDate()) {
            if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new IncorrectlyDateStateRequestException(
                        "Неверно указана дата события",
                        "Дата события не может быть в прошлом или ранее 2-х часов",
                        LocalDateTime.now()
                );
            }
        }
    }
}
