package ru.practicum.ewm.event.service;

import client.StatClient;
import dto.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventUpdateAdminDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.mappers.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.IncorrectlyDateStateRequestException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NotFoundException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.PartialRequestException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.RequestValidationException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.validation.DtoValidator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DtoValidator validator;
   // private final StatClient statClient;

    public Object createEvent(Long userId, NewEventDto newEvent) {
        validator.validateNewEventDto(newEvent);
        try {
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
        } catch (RuntimeException е) {
            throw new RequestValidationException("Не верно составлен запрос",
                    "Ошибка в параметрах запроса",
                    LocalDateTime.now());
        }
    }

    private Event creatingNewEvent(NewEventDto newEvent, User user, Category category) {
        return new Event(null, newEvent.getAnnotation(), category, LocalDateTime.now(), newEvent.getDescription(),
                newEvent.getEventDate(), user, newEvent.getLocation(), newEvent.getPaid(), newEvent.getParticipantLimit(),
                true, null, newEvent.getRequestModeration(), Event.State.PENDING, newEvent.getTitle());
    }

    public Object getEventsByUserId(Long userId, int from, int size) {
        try {
            Pageable pageable = PageRequest.of(from / size, size);
            User initiator = userRepository.findById(userId).orElseThrow(() ->
                    new NotFoundException("Пользователь с id" + userId + "не найден",
                            "Запрашиваемый объект не найден или не доступен"
                            , LocalDateTime.now()));
            return eventRepository.getOwnerEvents(userId, pageable).stream()
                    .map(EventMapper.INSTANCE::toEventShortDto).collect(Collectors.toList());
        } catch (RuntimeException е) {
            throw new RequestValidationException("Не верно составлен запрос",
                    "Ошибка в параметрах запроса",
                    LocalDateTime.now());
        }
    }

    public Object getEventsByUserAndEventId(Long userId, Long eventId) {
        try {
            User initiator = userRepository.findById(userId).orElseThrow(() ->
                    new NotFoundException("Пользователь с id" + userId + "не найден",
                            "Запрашиваемый объект не найден или не доступен"
                            , LocalDateTime.now()));
            Event stored = eventRepository.findById(eventId).orElseThrow(() ->
                    new NotFoundException("Событие с id" + eventId + "не найдено",
                            "Запрашиваемый объект не найден или не доступен"
                            , LocalDateTime.now()));
            return EventMapper.INSTANCE.toEventDto(stored);
        } catch (RuntimeException е) {
            throw new RequestValidationException("Не верно составлен запрос",
                    "Ошибка в параметрах запроса",
                    LocalDateTime.now());
        }
    }

    public Object updateEventsByUser(Long userId, Long eventId, EventUpdateDto eventUpdateDto) {
        //try {
            Event stored = eventRepository.findById(eventId).orElseThrow(() ->
                    new NotFoundException("Событие с id" + eventId + "не найдено",
                            "Запрашиваемый объект не найден или не доступен"
                            , LocalDateTime.now()));
            if(!stored.getInitiator().getId().equals(userId)){
                throw new IncorrectlyDateStateRequestException(
                        "Условия выполнения не соблюдены",
                        "Изменять может только владелец",
                        LocalDateTime.now());
            }
            if (stored.getState().equals(Event.State.PUBLISHED)) {
                throw new IncorrectlyDateStateRequestException(
                        "Условия выполнения не соблюдены",
                        "Изменять можно неопубликованные события",
                        LocalDateTime.now());
            }
            if (eventUpdateDto.getEventDate() != null){
                if (eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                    throw new IncorrectlyDateStateRequestException(
                            "Условия выполнения не соблюдены",
                            "Изменять можно события за 2 часа до начала",
                            LocalDateTime.now());
                }
            }
            if (stored.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new IncorrectlyDateStateRequestException(
                        "Условия выполнения не соблюдены",
                        "Изменять можно события за 2 часа до начала",
                        LocalDateTime.now());
            }
            if (stored.getParticipantLimit() == 0) {
                throw new PartialRequestException("Мест нет",
                        "Нет свободных мест в событиии", LocalDateTime.now());
            }
            Event updEventWithoutState = EventMapper.INSTANCE.updateEventWithUser(eventUpdateDto, stored);
            if (eventUpdateDto.getCategory() != null) {
                Category newCategory = categoryRepository.findById(eventUpdateDto.getCategory()).get();
                updEventWithoutState.setCategory(newCategory);
            }
            if (Objects.equals(EventUpdateDto.State.SEND_TO_REVIEW, eventUpdateDto.getStateAction())) {
                updEventWithoutState.setState(Event.State.PENDING);
            }
            if (Objects.equals(EventUpdateDto.State.CANCEL_REVIEW, eventUpdateDto.getStateAction())) {
                updEventWithoutState.setState(Event.State.CANCELED);
            }
            return EventMapper.INSTANCE.toEventDto(eventRepository.save(updEventWithoutState));
       /* } catch (RuntimeException е) {
            throw new RequestValidationException("Не верно составлен запрос",
                    "Ошибка в параметрах запроса",
                    LocalDateTime.now());
        }*/
    }

    public Object updateEventsByAdmin(Long eventId, EventUpdateAdminDto eventUpdateAdminDto) {
        try {
            Event stored = eventRepository.findById(eventId).orElseThrow(() ->
                    new NotFoundException("Событие с id" + eventId + "не найдено",
                            "Запрашиваемый объект не найден или не доступен"
                            , LocalDateTime.now()));
            if (!Objects.equals(Event.State.PENDING, stored.getState())) {
                throw new IncorrectlyDateStateRequestException(
                        "Условия выполнения не соблюдены",
                        "Изменять можно неопубликованные события",
                        LocalDateTime.now());
            }
            if (stored.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new IncorrectlyDateStateRequestException(
                        "Неверно указана дата события",
                        "Дата события не может быть менее чем за 1 час до начала",
                        LocalDateTime.now());
            }
           /* if (stored.getParticipantLimit() == 0) {
                throw new PartialRequestException("Мест нет",
                        "Нет свободных мест в событиии", LocalDateTime.now());*/
            Event updEventWithoutState = EventMapper.INSTANCE.updateEventWithUser(eventUpdateAdminDto, stored);
            if (eventUpdateAdminDto.getCategory() != null) {
                Category newCategory = categoryRepository.findById(eventUpdateAdminDto.getCategory()).get();
                updEventWithoutState.setCategory(newCategory);
            }
            if (Objects.equals(EventUpdateAdminDto.State.PUBLISH_EVENT, eventUpdateAdminDto.getStateAction())) {
                updEventWithoutState.setState(Event.State.PUBLISHED);
                updEventWithoutState.setPublishedOn(LocalDateTime.now());
            }
            if (Objects.equals(EventUpdateAdminDto.State.REJECT_EVENT, eventUpdateAdminDto.getStateAction())) {
                updEventWithoutState.setState(Event.State.CANCELED);
            }
            return EventMapper.INSTANCE.toEventDto(eventRepository.save(updEventWithoutState));
        } catch (RuntimeException е) {
            throw new RequestValidationException("Не верно составлен запрос",
                    "Ошибка в параметрах запроса",
                    LocalDateTime.now());
        }
    }

    public Object getEventDyId(Long id, HttpServletRequest request) {
        Event stored = null;
        try {
            stored = eventRepository.findByIdAndState(id, Event.State.PUBLISHED);
        } catch (RuntimeException е) {
            new NotFoundException("Событие с id" + id + "не найдено",
                    "Запрашиваемый объект не найден или не доступен"
                    , LocalDateTime.now());
        }
        EndpointHitDto endpointHitDto = new EndpointHitDto(
                null,
                "explore-main",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        //statClient.post(endpointHitDto);
        return null;
    }
}
