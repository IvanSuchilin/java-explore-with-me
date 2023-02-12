package ru.practicum.ewm.event.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository <Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query(value = "select e " +
            "from Event as e " +
            "where e.initiator.id = ?1 ")
    List<Event> getOwnerEvents(Long userId, Pageable pageable);

    Event findByIdAndState(Long id, Event.State published);

    Page<Event> findAll(Predicate value, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.id IN :events")
    List<Event> findAllByEvents(List<Long> events);
}
