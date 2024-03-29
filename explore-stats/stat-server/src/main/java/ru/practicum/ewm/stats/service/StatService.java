package ru.practicum.ewm.stats.service;

import dto.EndpointHitDto;
import dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.stats.mappers.StatMapper;
import ru.practicum.ewm.stats.model.EndpointHit;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatService {
    private final StatRepository statRepository;

    public EndpointHitDto saveStat(EndpointHit endpointHit) {
        log.info("Получен запрос на сохрание информации об обращении к эндпоинту в сервисе {}", endpointHit.getUri());
        return StatMapper.INSTANCE.toEndpointHitDto(statRepository.save(endpointHit));
    }

    public List<StatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Получен запрос на получений статистики");
        if (uris == null || uris.isEmpty()) {
            return new ArrayList<>();
        } else {
            if (unique) {
                return statRepository.findStatWithUnique(uris, start, end)
                        .stream().sorted(Comparator.comparing(StatDto::getHits).reversed()).collect(Collectors.toList());
            } else {
                return statRepository.findStatNOtUnique(uris, start, end)
                        .stream().sorted(Comparator.comparing(StatDto::getHits).reversed()).collect(Collectors.toList());
            }
        }
    }
}
