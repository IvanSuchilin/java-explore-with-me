package ru.practicum.ewm.exceptions.RequestValidationExceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class IncorrectlyDateStateRequestException extends BaseException {
    private final HttpStatus status = HttpStatus.FORBIDDEN;

    public IncorrectlyDateStateRequestException(String massage, String reason, LocalDateTime timestamp) {
        super(massage, reason, timestamp);
    }
}
