package ru.practicum.ewm.publicApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.text.MessageFormat;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping
public class PublicController {
    private final CategoryService categoryService;

    @GetMapping("/categories/{catId}")
    public ResponseEntity<Object> get(@PathVariable("catId") Long id) {
        log.info("Получение информации о категории id {}", id);
        return new ResponseEntity<>(categoryService.getCategoryById(id), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<Object> get(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                      @Positive @RequestParam(defaultValue = "10") int size) {
        log.info(MessageFormat.format("Получение списка категорий id: {0} с {1} категории и размером страницы {2}",
                from, size));
        return new ResponseEntity<>(categoryService.getCategories(from, size), HttpStatus.OK);
    }
}
