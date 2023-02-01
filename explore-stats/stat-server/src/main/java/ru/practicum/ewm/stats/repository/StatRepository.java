package ru.practicum.ewm.stats.repository;

import ru.practicum.ewm.stats.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository  extends JpaRepository<EndpointHit, Long>{

    List<EndpointHit> findAllByUriAndTimestampBetween(String uri, LocalDateTime start, LocalDateTime end);
}
