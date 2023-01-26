package ru.practicum.stat.repository;

import ru.practicum.stat.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatRepository  extends JpaRepository<EndpointHit, Long>{
}
