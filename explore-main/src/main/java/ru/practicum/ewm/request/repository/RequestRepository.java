package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequester_IdAndEvent_Id(Long userId, Long eventId);
    List<Request> findAllByEvent_Id(Long eventId);
    List<Request> findAllByRequesterId(Long userId);
}
