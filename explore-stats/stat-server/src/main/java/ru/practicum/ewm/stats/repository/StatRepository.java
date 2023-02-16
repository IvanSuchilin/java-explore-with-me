package ru.practicum.ewm.stats.repository;

import dto.StatDto;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.model.EndpointHit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new dto.StatDto(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp between ?2 and ?3 " +
            "and e.uri in ?1 " +
            "group by e.app, e.uri")
    List<StatDto> findStatWithUnique(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select new dto.StatDto(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp between ?2 and ?3 " +
            "and e.uri in ?1 " +
            "group by e.app, e.uri")
    List<StatDto> findStatNOtUnique(List<String> uris, LocalDateTime start, LocalDateTime end);
}
