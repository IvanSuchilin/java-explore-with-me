package ru.practicum.ewm.publicApi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.mappers.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NotFoundException;
import ru.practicum.ewm.user.mappers.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.validation.DtoValidator;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DtoValidator validator;

    public Object getCategoryById(Long id) {
        log.debug("Получен запрос GET /categories/{catId}");
        Category stored = categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Категория с id" + id + "не найдена", "Запрашиваемый объект не найден или не доступен"
                        , LocalDateTime.now()));
        return CategoryMapper.INSTANCE.toDto(stored);
    }

    public Object getCategorys(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Поиск всех категорий с пагинацией");
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }
}
