package ru.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.dto.StatDto;
import ru.practicum.stat.mappers.StatMapper;
import ru.practicum.stat.model.EndpointHit;
import ru.practicum.stat.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatService {
    private final StatRepository statRepository;

    public EndpointHitDto saveStat(EndpointHit endpointHit) {
        log.info("Получен запрос на сохрание информации об обращении к эндпоинту {}", endpointHit.getUri());
        return StatMapper.INSTANCE.toEndpointHitDto(statRepository.save(endpointHit));
    }

    public List<StatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Получен запрос на получений статистики");
        List<StatDto> stat = new ArrayList<>();
        for (String uri : uris) {
            List<EndpointHit> hits = statRepository.findAllByUriAndCreatedBetween(uri, start, end);
            if (hits.size() == 0) {
                stat.add(new StatDto("", uri, 0));
            } else {
                if (unique) {
                    List<EndpointHit> returnedListEndpointHits = hits.stream().distinct().collect(Collectors.toList());
                    stat.add(new StatDto(returnedListEndpointHits.get(0).getApp(), uri, returnedListEndpointHits.size()));
                } else {
                    stat.add(new StatDto(hits.get(0).getApp(), uri, hits.size()));
                }
            }
        }
        return stat;
    }
}
