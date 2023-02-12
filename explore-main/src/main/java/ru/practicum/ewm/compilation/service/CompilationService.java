package ru.practicum.ewm.compilation.service;

import client.StatClient;
import dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventDtoForCompilation;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.mappers.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.RequestValidationException;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.validation.DtoValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationService {
    private final DtoValidator validator;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;
    DateTimeFormatter returnedTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatClient statClient = new StatClient("http://localhost:9090", "explore-main", new RestTemplateBuilder());


    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getTitle() == null) {
            throw new RequestValidationException("Пустой заголовок", "Заголовок не может быть пустым", LocalDateTime.now());
        }
        List<Event> storedEvents = eventRepository.findAllByEvents(newCompilationDto.getEvents());
        Compilation compilation = new Compilation(null, storedEvents, newCompilationDto.isPinned(), newCompilationDto.getTitle());
        Compilation saved = compilationRepository.save(compilation);
        return creatingCompilationDto(saved);
    }

    private CompilationDto creatingCompilationDto(Compilation compilation) {
        List<EventFullDto> eventFullDtoList = compilation.getEvents().stream().map(EventMapper.INSTANCE::toEventFullDto)
                .collect(Collectors.toList());
        List<EventFullDto> eventFullDtoListWithViews = eventFullDtoList.stream()
                .map(this::preparingFullDtoWithStat).collect(Collectors.toList());
        List<EventDtoForCompilation> eventCompilationDtoListWithViews = eventFullDtoListWithViews
                .stream().map(EventMapper.INSTANCE::toCompilationDtoFromFull).collect(Collectors.toList());
        return new CompilationDto(eventCompilationDtoListWithViews, compilation.getId(),
                compilation.isPinned(), compilation.getTitle());
    }

    private EventFullDto preparingFullDtoWithStat(EventFullDto eventFullDto) {
        List<StatDto> stat =
                statClient.getStat(eventFullDto.getCreatedOn().format(returnedTimeFormat),
                        LocalDateTime.now().format(returnedTimeFormat),
                        List.of("/events/" + eventFullDto.getId()), false).getBody();
        if (stat.size() > 0) {
            eventFullDto.setViews(stat.get(0).getHits());
        }
        List<Request> confirmedRequests = requestRepository.findAllByStatusAndAndEvent_Id(Request.RequestStatus.CONFIRMED,
                eventFullDto.getId());
        eventFullDto.setConfirmedRequests(confirmedRequests.size());
        return eventFullDto;
    }
}
