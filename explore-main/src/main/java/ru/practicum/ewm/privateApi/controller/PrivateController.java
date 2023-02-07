package ru.practicum.ewm.privateApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


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
    }

@GetMapping("/users/{userId}/events")
public ResponseEntity<Object> getEventsByUserId(@PathVariable("userId") Long id,
        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
        @Positive @RequestParam(defaultValue = "10") int size){
    log.info("Получение событий, добавленных текущим пользователем c id {}", id);
    return new ResponseEntity<>(eventService.getEventsByUserId(id, from, size), HttpStatus.OK);
}

    @GetMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<Object> getEventsByUserAndEventId(@PathVariable("userId") Long id,
                                                            @PathVariable Long eventId){
        log.info("Получение события c id {}", id);
        return new ResponseEntity<>(eventService.getEventsByUserAndEventId(id, eventId), HttpStatus.OK);
    }

}
