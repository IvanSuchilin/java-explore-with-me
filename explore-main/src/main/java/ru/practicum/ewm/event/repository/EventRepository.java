package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository <Event, Long> {
    @Query(value = "select e " +
            "from Event as e " +
            "where e.initiator.id = ?1 ")
    List<Event> getOwnerEvents(Long userId, Pageable pageable);
}
