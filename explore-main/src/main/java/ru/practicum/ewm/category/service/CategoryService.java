package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryShortDto;
import ru.practicum.ewm.category.mappers.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.IncorrectlyDateStateRequestException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NameAlreadyExistException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NotFoundException;
import ru.practicum.ewm.validation.DtoValidator;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final DtoValidator validator;

    public CategoryDto createCategory(CategoryDto category) {
        validator.validateCategory(category);
        Category stored;
        log.debug("Получен запрос на создание категории {}", category.getName());
        try {
            stored = categoryRepository.save(CategoryMapper.INSTANCE.toCategory(category));
        } catch (RuntimeException e) {
            throw new NameAlreadyExistException("Имя категории уже используется", "Не соблюдены условия уникальности имени",
                    LocalDateTime.now());
        }
        return CategoryMapper.INSTANCE.toDto(stored);
    }

    public void deleteCategory(Long id) {
        log.debug("Получен запрос на удаление категории {}", id);
        categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Категория с id" + id + "не найдена", "Запрашиваемый объект не найден или не доступен",
                        LocalDateTime.now()));
        try {
            categoryRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new IncorrectlyDateStateRequestException(
                    "Условия выполнения не соблюдены",
                    "Удалять можно только непривязанную категорию",
                    LocalDateTime.now());
        }
    }

    public Object update(Long id, CategoryShortDto updatingDto) {
        validator.validateCategoryForUpd(updatingDto);
        log.debug("Получен запрос обновления категории пользователем с id {}", id);
        Category stored = categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Категория с id" + id + "не найдена", "Запрашиваемый объект не найден или не доступен",
                        LocalDateTime.now()));
        checkNameForUniq(updatingDto);
        CategoryMapper.INSTANCE.updateCategory(updatingDto, stored);
        Category actualCategory = categoryRepository.save(stored);
        return CategoryMapper.INSTANCE.toDto(actualCategory);
    }

    public Object getCategoryById(Long id) {
        log.debug("Получен запрос на получение категории {}", id);
        Category stored = categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Категория с id" + id + "не найдена", "Запрашиваемый объект не найден или не доступен",
                        LocalDateTime.now()));
        return CategoryMapper.INSTANCE.toDto(stored);
    }

    public Object getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Поиск всех категорий с пагинацией");
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    private void checkNameForUniq(CategoryShortDto updatingDto) {
        if (StringUtils.isNotBlank(updatingDto.getName()) && categoryRepository.findAll().stream()
                .anyMatch(u -> u.getName().equals(updatingDto.getName()))) {
            throw new NameAlreadyExistException("Имя категории уже используется", "Не соблюдены условия уникальности имени",
                    LocalDateTime.now());
        }
    }
}
