package ru.practicum.ewm.compilation.service;

import client.StatClient;
import com.querydsl.core.BooleanBuilder;
import dto.StatDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdatingCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.QCompilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventDtoForCompilation;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.mappers.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NotFoundException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.RequestValidationException;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.validation.DtoValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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


    public void deleteCompilationById(Long compId) {
        log.info("Удаление подборки admin");
        compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с id" + compId + "не найдена",
                        "Запрашиваемый объект не найден или не доступен"
                        , LocalDateTime.now()));
        compilationRepository.deleteById(compId);
    }

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        log.info("Создание новой подборки admin");
        if (newCompilationDto.getTitle() == null) {
            throw new RequestValidationException("Пустой заголовок", "Заголовок не может быть пустым", LocalDateTime.now());
        }
        List<Event> storedEvents = eventRepository.findAllByEvents(newCompilationDto.getEvents());
        Compilation compilation = new Compilation(null, storedEvents, newCompilationDto.isPinned(), newCompilationDto.getTitle());
        Compilation saved = compilationRepository.save(compilation);
        return createCompilationDto(saved);
    }

    public CompilationDto updateCompilation(Long compId, UpdatingCompilationDto updatingCompilationDto) {
        log.info("Обновление подборки подборки admin");
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с id" + compId + "не найдена",
                        "Запрашиваемый объект не найден или не доступен"
                        , LocalDateTime.now()));
        Compilation newCompilation = createCompilationForUpdate(compilation, updatingCompilationDto);
        compilationRepository.save(newCompilation);
        return createCompilationDto(newCompilation);
    }


    public CompilationDto getCompilationById(Long compId) {
        log.info("Получение подборки по id {}", compId);
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Подборка с id" + compId + "не найдена",
                        "Запрашиваемый объект не найден или не доступен"
                        , LocalDateTime.now()));
        return createCompilationDto(compilation);
    }

    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Получение всех подборок с пагинацией и привзкой {}", pinned);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        QCompilation qCompilation = QCompilation.compilation;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qCompilation.pinned.eq(true));
        if (pinned != null) {
            booleanBuilder.and(qCompilation.pinned.eq(pinned));
        }
        return compilationRepository.findAll(Objects.requireNonNull(booleanBuilder.getValue()),
                        pageable).getContent()
                .stream().map(this::createCompilationDto).collect(Collectors.toList());
    }

    private Compilation createCompilationForUpdate(Compilation stored, UpdatingCompilationDto updatingCompilationDto) {
        if (updatingCompilationDto.getPinned() != null) {
            stored.setPinned(updatingCompilationDto.getPinned());
        }
        if (updatingCompilationDto.getTitle() != null) {
            stored.setTitle(updatingCompilationDto.getTitle());
        }
        if (updatingCompilationDto.getEvents() != null) {
            stored.setEvents(eventRepository.findAllByEvents(updatingCompilationDto.getEvents()));
        }
        return stored;
    }

    private CompilationDto createCompilationDto(Compilation compilation) {
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
