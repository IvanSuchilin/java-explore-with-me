package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.mappers.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.validation.DtoValidator;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DtoValidator validator;

    public Object createEvent(Long userId, NewEventDto newEvent) {
        validator.validateNewEventDto(newEvent);
        User initiator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id" + userId + "не найден",
                        "Запрашиваемый объект не найден или не доступен"
                        , LocalDateTime.now()));
        Category stored = categoryRepository.findById(newEvent.getCategory()).orElseThrow(() ->
                new NotFoundException("Категория с id" + newEvent.getCategory() + "не найдена",
                        "Запрашиваемый объект не найден или не доступен"
                        , LocalDateTime.now()));
        Event newEventEntity = creatingNewEvent(newEvent, initiator, stored);
        return EventMapper.INSTANCE.toEventDto(eventRepository.save(newEventEntity));
    }

    private Event creatingNewEvent(NewEventDto newEvent, User user, Category category) {
        return new Event(null, newEvent.getAnnotation(), category, LocalDateTime.now(), newEvent.getDescription(),
                newEvent.getEventDate(), user, newEvent.getLocation(), newEvent.getPaid(), newEvent.getParticipantLimit(),
                true, null, newEvent.getRequestModeration(), Event.State.PENDING, newEvent.getTitle());
    }
}