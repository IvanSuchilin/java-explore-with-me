package ru.practicum.ewm.exceptions.RequestValidationExceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class NameAlreadyExistException extends RuntimeException {
    private final String message;
    private final String reason;
    private final HttpStatus status;
    private final LocalDateTime timestamp;

    public NameAlreadyExistException(String massage, String reason, LocalDateTime timestamp) {
        this.status = HttpStatus.CONFLICT;
        this.reason = reason;
        this.message = massage;
        this.timestamp = timestamp;
    }
}
