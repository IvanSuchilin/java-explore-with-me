package ru.practicum.ewm.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.service.AdminService;
import ru.practicum.ewm.user.dto.UserDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.text.MessageFormat;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin")
public class AdminUserController {

    private final AdminService adminService;

    @PostMapping("/users")
    public ResponseEntity<Object> create(@RequestBody UserDto user) {
        log.info("Создание пользователя {}", user.getName());
        return new ResponseEntity<>(adminService.createUser(user), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Object> delete(@Positive @PathVariable("userId") Long id) {
        log.info("Удаление пользователя id {}", id);
        adminService.delete(id);
        return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info(MessageFormat.format("Получение списка пользоветелей id: {0} с {1} пользователя и размером страницы {2}",
                ids, from, size));
        return new ResponseEntity<>(adminService.getUsers(ids, from, size), HttpStatus.OK);
    }
}
