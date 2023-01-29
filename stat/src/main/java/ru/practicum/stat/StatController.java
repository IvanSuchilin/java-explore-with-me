package ru.practicum.stat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.StatDto;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping
public class StatController {
    private StatService statService;

    @Autowired
public StatController (StatService statService){
        this.statService = statService;
    }

   @PostMapping ("/hit")
    public EndpointHitDto saveStat (EndpointHit endpointHit){
        log.info("Получен запрос на сохрание информации об обращении к эндпоинту {}", endpointHit.getUri());
       return statService.saveStat(endpointHit);
   }

    @GetMapping("/hit/stats")
    public List<StatDto> getStat (@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                      LocalDateTime start,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                  LocalDateTime end,
                                  @RequestParam List<String> uris,
                                  @RequestParam boolean unique){
        log.info("Получен запрос на получение статистики");
        return statService.getStat(start, end, uris, unique);
    }

}
