package ru.practicum.ewm.exceptions.RequestValidationExceptions;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
@Getter
public class RequestValidationException extends  RuntimeException{
    private final String message;
    private final String reason;
    private final HttpStatus status;
    private final LocalDateTime timestamp;

    public RequestValidationException(String massage, String reason, LocalDateTime timestamp) {
        this.status = HttpStatus.BAD_REQUEST;
        this.reason = reason;
        this.message = massage;
        this.timestamp = timestamp;
    }
}
