package ru.practicum.ewm.exceptions.RequestValidationExceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class IncorrectlyDateStateRequestException extends RuntimeException {
    private final String message;
    private final String reason;
    private final HttpStatus status;
    private final LocalDateTime timestamp;

    public IncorrectlyDateStateRequestException(String massage, String reason, LocalDateTime timestamp) {
        this.status = HttpStatus.FORBIDDEN;
        this.reason = reason;
        this.message = massage;
        this.timestamp = timestamp;
    }
}
