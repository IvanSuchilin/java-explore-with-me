package ru.practicum.ewm.stats;

import dto.EndpointHitDto;
import dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.stats.mappers.StatMapper;
import ru.practicum.ewm.stats.model.EndpointHit;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.service.StatService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    public EndpointHitDto saveStat(@RequestBody @Valid EndpointHitDto endpointHit) {
        log.info("Получен запрос на сохрание информации об обращении к эндпоинту в контроллере {}", endpointHit.getUri());
        EndpointHit endpointHit1 = StatMapper.INSTANCE.toEndpointHit(endpointHit);
        return statService.saveStat(endpointHit1);
    }

    @GetMapping("/stats")
    public List<StatDto> getStat(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                 LocalDateTime start,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                 LocalDateTime end,
                                 @RequestParam List<String> uris,
                                 @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Получен запрос на получение статистики в контроллере");
        return statService.getStat(start, end, uris, unique);
    }
}
