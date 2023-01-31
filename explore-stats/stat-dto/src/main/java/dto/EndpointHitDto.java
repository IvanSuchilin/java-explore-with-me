package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EndpointHitDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime created;
}
