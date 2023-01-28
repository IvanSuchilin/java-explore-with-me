package ru.practicum.stat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.StatDto;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.service.StatService;

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
}
