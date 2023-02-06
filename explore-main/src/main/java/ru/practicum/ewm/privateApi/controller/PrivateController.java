package ru.practicum.ewm.privateApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.service.EventService;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping
public class PrivateController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    public ResponseEntity<Object> createEvent(@PathVariable("userId") Long id,
                                              @RequestBody NewEventDto newEvent) {
        log.info("Создание нового события в категории {}", newEvent.getCategory());
        return new ResponseEntity<>(eventService.createEvent(id, newEvent), HttpStatus.CREATED);
    }}
