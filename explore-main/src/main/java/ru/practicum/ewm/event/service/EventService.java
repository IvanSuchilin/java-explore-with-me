package ru.practicum.ewm.event.service;

import client.StatClient;
import com.querydsl.core.BooleanBuilder;
import dto.EndpointHitDto;
import dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateAdminDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.mappers.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.IncorrectlyDateStateRequestException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NotFoundException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.PartialRequestException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.RequestValidationException;
import ru.practicum.ewm.publicApi.controller.PublicController;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.validation.DtoValidator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final DtoValidator validator;

    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatClient statClient = new StatClient("http://localhost:9090", "explore-main", new RestTemplateBuilder());


    public Object createEvent(Long userId, NewEventDto newEvent) {
        validator.validateNewEventDto(newEvent);
        log.info("Создание события в категории {}", newEvent.getCategory());
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
                true, null, newEvent.getRequestModeration(), Event.State.PENDING, newEvent.getTitle(), 0L);
    }

    public Object getEventsByUserId(Long userId, int from, int size) {
        log.info("Получение информации о событии пользователем");
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
        log.info("Получение информации о событии пользователем");
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
        log.info("Обновление события пользователем");
        Event stored = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id" + eventId + "не найдено",
                        "Запрашиваемый объект не найден или не доступен"
                        , LocalDateTime.now()));
        if (!stored.getInitiator().getId().equals(userId)) {
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
        if (eventUpdateDto.getEventDate() != null) {
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
        log.info("Обновление события админом");
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

    public Object getEventById(Long id, HttpServletRequest request) {
        log.info("Получение информации пользователем public");
        Event stored = null;
        try {
            stored = eventRepository.findByIdAndState(id, Event.State.PUBLISHED);
        } catch (RuntimeException е) {
            new NotFoundException("Событие с id" + id + "не найдено",
                    "Запрашиваемый объект не найден или не доступен"
                    , LocalDateTime.now());
        }
        statClient.addHit(request);
        EventFullDto eventFullDto = EventMapper.INSTANCE.toEventFullDto(stored);
        List<StatDto> stat =
                statClient.getStat(stored.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        List.of("/events/" + stored.getId()), false).getBody();
        if (stat.size() > 0) {
            eventFullDto.setViews(stat.get(0).getHits());
            stored.setViews(stat.get(0).getHits());
            eventRepository.save(stored);
        }
        List<Request> confirmedRequests = requestRepository.findAllByStatusAndAndEvent_Id(Request.RequestStatus.CONFIRMED,
                id);
        eventFullDto.setConfirmedRequests(confirmedRequests.size());
        return eventFullDto;
    }

    public Object getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                            LocalDateTime rangeEnd, Boolean onlyAvailable, PublicController.FilterSort sort,
                            Integer from, Integer size, HttpServletRequest request) {
        log.info("Получение информации о событиях с фильтрами public");
        statClient.addHit(request);
        QEvent qEvent = QEvent.event;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qEvent.state.eq(Event.State.PUBLISHED));
        if (text != null) {
            booleanBuilder.and(qEvent.annotation.containsIgnoreCase(text).or(qEvent.description.containsIgnoreCase(text)));
        }
        if (categories != null) {
            booleanBuilder.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            booleanBuilder.and(QEvent.event.paid.eq(paid));
        }
        if (onlyAvailable != null) {
            booleanBuilder.and(QEvent.event.available.eq(true));
        }
        if (rangeStart != null) {
            booleanBuilder.and(qEvent.eventDate.after(rangeStart));
        }

        if (rangeEnd != null) {
            booleanBuilder.and(qEvent.eventDate.before(rangeEnd));
        }
        if (rangeStart == null) {
            booleanBuilder.and(qEvent.eventDate.after(LocalDateTime.now()));
        }
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        if (sort.equals(PublicController.FilterSort.VIEWS)) {
            pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "views"));
        }
        log.info("Получение информации о событиях с фильтрами public из репозиория");
        List<Event> storedEvents = eventRepository.findAll(booleanBuilder.getValue(), pageable).getContent();
        //return storedEvents;
        return storedEvents.stream().map(EventMapper.INSTANCE::toEventFullDto).collect(Collectors.toList());
    }
}
