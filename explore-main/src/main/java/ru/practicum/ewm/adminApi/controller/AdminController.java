package ru.practicum.ewm.adminApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryShortDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.text.MessageFormat;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin")
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;

    @PostMapping("/users")
    public ResponseEntity<Object> createUserByAdmin(@RequestBody UserDto user) {
        log.info("Создание пользователя {}", user.getName());
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Object> deleteUserByAdmin(@Positive @PathVariable("userId") Long id) {
        log.info("Удаление пользователя id {}", id);
        userService.deleteUser(id);
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
        return new ResponseEntity<>(userService.getUsers(ids, from, size), HttpStatus.OK);
    }

    @PostMapping("/categories")
    public ResponseEntity<Object> createCategory(@RequestBody CategoryDto category) {
        log.info("Создание категории {}", category.getName());
        return new ResponseEntity<>(categoryService.createCategory(category), HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Object> deleteCategoryByAdmin(@Positive @PathVariable("catId") Long id) {
        log.info("Удаление Категории id {}", id);
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<Object> patch(@PathVariable("catId") Long id, @RequestBody CategoryShortDto updatingDto) {
        log.info("Обновлеие данных категории id {}", id);
        return new ResponseEntity<>(categoryService.update(id, updatingDto), HttpStatus.OK);
    }

}
