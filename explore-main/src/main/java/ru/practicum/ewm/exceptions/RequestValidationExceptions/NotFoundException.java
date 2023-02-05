package ru.practicum.ewm.exceptions.RequestValidationExceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
@Getter
public class NotFoundException extends RuntimeException {
    private final String message;
    private final String reason;
    private final HttpStatus status;
    private final LocalDateTime timestamp;

    public NotFoundException(String massage, String reason, LocalDateTime timestamp) {
        this.status = HttpStatus.NOT_FOUND;
        this.reason = reason;
        this.message = massage;
        this.timestamp = timestamp;
    }
}
