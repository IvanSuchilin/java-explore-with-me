package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NotFoundException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.PartialRequestException;
import ru.practicum.ewm.request.mappers.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;


    public Object createRequest(long userId, long eventId) {
        Event stored = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id" + eventId + "не найдено",
                        "Запрашиваемый объект не найден или не доступен"
                        , LocalDateTime.now()));
        if (stored.getInitiator().getId() == userId) {
            throw new PartialRequestException("Вы инициатор",
                    "Нельзя ходить на свои мероприятия как гость", LocalDateTime.now());
        }
        if (!stored.getState().equals(Event.State.PUBLISHED)) {
            throw new PartialRequestException("Событие не опубликовано",
                    "Нельзя подать запрос на неопубликованное событие", LocalDateTime.now());
        }
        if (stored.getParticipantLimit() == 0) {
            throw new PartialRequestException("Мест нет",
                    "Нет свободных мест в событиии", LocalDateTime.now());
        }
        Request request = new Request();
        if (!stored.isRequestModeration()){
            request.setStatus(Request.RequestStatus.CONFIRMED);
            stored.setParticipantLimit(stored.getParticipantLimit() - 1);
            eventRepository.save(stored);
        } else {
            request.setStatus(Request.RequestStatus.PENDING);
        }
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id" + userId + "не найден",
                        "Запрашиваемый объект не найден или не доступен"
                        , LocalDateTime.now()));
        request.setRequester(requester);
        request.setEvent(stored);
        request.setCreated(LocalDateTime.now());
        Request saved = requestRepository.save(request);
        return RequestMapper.INSTANCE.toRequestDto(saved);
    }
}
