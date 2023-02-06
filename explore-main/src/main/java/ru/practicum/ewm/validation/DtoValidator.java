package ru.practicum.ewm.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.IncorrectlyDateException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.RequestValidationException;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DtoValidator {

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
            throw new IncorrectlyDateException(
                    "Неверно указана дата события",
                    "Дата события не может быть в прошлом или ранее 2-х часов",
                    LocalDateTime.now()
            );
        }
        if (newEventDto.getCategory() <= 0 ){
            throw new RequestValidationException(
                    "Неверно указана категория указано имя пользователя",
                    "Имя пользователя пустое",
                    LocalDateTime.now()
            );
        }
    }
}
