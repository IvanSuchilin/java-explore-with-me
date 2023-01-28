package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.mappers.StatMapper;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.repository.StatRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatService {
    private final StatRepository statRepository;

    public EndpointHitDto saveStat(EndpointHit endpointHit) {
        log.info("Получен запрос на сохрание информации об обращении к эндпоинту {}", endpointHit.getUri());
       return StatMapper.INSTANCE.toEndpointHitDto(statRepository.save(endpointHit));
    }
}
