package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.exceptions.userExceptions.EmailAlreadyExistException;
import ru.practicum.ewm.exceptions.userExceptions.InvalidEmailException;
import ru.practicum.ewm.exceptions.userExceptions.UserNotFoundException;
import ru.practicum.ewm.response.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSystemExceptions(final IllegalArgumentException e) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSystemExceptions(final NullPointerException e) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({InvalidEmailException.class,
            //ValidationFailedException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateEmailException(final RuntimeException e) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({EmailAlreadyExistException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidateEmailExistException(final RuntimeException e) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, ResponseStatusException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final RuntimeException e) {
        log.debug("Возникла ошибка {},", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
