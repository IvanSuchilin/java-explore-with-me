package ru.practicum.stat.repository;

import ru.practicum.stat.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository  extends JpaRepository<EndpointHit, Long>{

    List<EndpointHit> findAllByUriAndCreatedBetween(String uri, LocalDateTime start, LocalDateTime end);
}
