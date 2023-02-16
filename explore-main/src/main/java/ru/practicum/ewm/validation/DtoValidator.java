package ru.practicum.ewm.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryShortDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
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
    LocalDateTime time = LocalDateTime.now();

    public void validateNewCommentDto(NewCommentDto newCommentDto) {
        if (StringUtils.isBlank(newCommentDto.getText())) {
            throw new RequestValidationException(
                    "Не указан текст комментария",
                    "Комментарий пуст",
                    LocalDateTime.now()
            );
        }
    }

    public void validateUpdCommentDto(UpdateCommentDto updateCommentDto) {
        if (StringUtils.isBlank(updateCommentDto.getText())) {
            throw new RequestValidationException(
                    "Не указан текст комментария",
                    "Комментарий пуст",
                    LocalDateTime.now()
            );
        }
    }

    public void updValidationDtoForAdmin(Event stored, EventUpdateAdminDto eventUpdateAdminDto) {
        if (!Objects.equals(Event.State.PENDING, stored.getState())) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять можно неопубликованные события",
                    time);
        }
        if (stored.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IncorrectlyDateStateRequestException(
                    "Неверно указана дата события",
                    "Дата события не может быть менее чем за 1 час до начала",
                    time);
        }
        if (eventUpdateAdminDto.getEventDate() != null) {
            if (eventUpdateAdminDto.getEventDate().isBefore(LocalDateTime.now())) {
                throw new IncorrectlyDateStateRequestException(
                        "Условия выполнения не соблюдены",
                        "Новое время в прошлом",
                        time);
            }
        }
    }

    public void updValidationDtoForUser(Long userId, EventUpdateDto eventUpdateDto, Event stored) {
        if (!stored.getInitiator().getId().equals(userId)) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять может только владелец",
                    time);
        }
        if (stored.getState().equals(Event.State.PUBLISHED)) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять можно неопубликованные события",
                    time);
        }
        if (eventUpdateDto.getEventDate() != null) {
            if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new IncorrectlyDateStateRequestException(
                        "Условия выполнения не соблюдены",
                        "Изменять можно события за 2 часа до начала",
                        time);
            }
        }
        if (stored.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Изменять можно события за 2 часа до начала",
                    time);
        }
        if (stored.getParticipantLimit() == 0) {
            throw new PartialRequestException("Мест нет",
                    "Нет свободных мест в событиии", LocalDateTime.now());
        }
    }

    public void validateCategory(CategoryDto category) {
        if (StringUtils.isBlank(category.getName())) {
            throw new RequestValidationException(
                    "Не указано имя категории",
                    "Имя категории пустое",
                    time
            );
        }
    }

    public void validateCategoryForUpd(CategoryShortDto category) {
        if (StringUtils.isBlank(category.getName())) {
            throw new RequestValidationException(
                    "Не указано имя категории",
                    "Имя категории пустое",
                    time
            );
        }
    }

    public void validateUserDto(UserDto user) {
        if (StringUtils.isBlank(user.getName())) {
            throw new RequestValidationException(
                    "Не указано имя пользователя",
                    "Имя пользователя пустое",
                    time
            );
        }
    }

    public void validateNewEventDto(NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectlyDateStateRequestException(
                    "Неверно указана дата события",
                    "Дата события не может быть в прошлом или ранее 2-х часов",
                    time
            );
        }
        if (newEventDto.getCategory() <= 0) {
            throw new RequestValidationException(
                    "Неверно указана категория указано имя пользователя",
                    "Имя пользователя пустое",
                    time
            );
        }
        if (StringUtils.isBlank(newEventDto.getAnnotation())) {
            throw new RequestValidationException(
                    "Не указана аннотация",
                    "Поле аннотации пустое",
                    time
            );
        }
    }

    public void validateUpdateEventDto(EventUpdateDto eventUpdateDto) {
        if (null != eventUpdateDto.getEventDate()) {
            if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new IncorrectlyDateStateRequestException(
                        "Неверно указана дата события",
                        "Дата события не может быть в прошлом или ранее 2-х часов",
                        time
                );
            }
        }
    }
}
