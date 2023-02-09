package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
