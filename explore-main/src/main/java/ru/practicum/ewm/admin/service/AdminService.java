package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mappers.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NameAlreadyExistException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mappers.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.validation.DtoValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private  final DtoValidator validator;

    public UserDto createUser(UserDto user) {
        validator.validateUserDto(user);
        log.debug("Получен запрос на создание пользователя {}", user.getName());
        User saveUser;
        try {
            saveUser = userRepository.save(UserMapper.INSTANCE.toUser(user));
        } catch  (RuntimeException e) {
            throw new NameAlreadyExistException("Имя (почта) уже используется", "Не соблюдены условия уникальности имени (почты)"
                    , LocalDateTime.now());
        }
        return UserMapper.INSTANCE.toDto(saveUser);
    }

    public void deleteUser(Long id) {
        log.debug("Получен запрос DELETE /admin/users/{userId}");
        User stored = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Пользователь c id" + id + " не найден"));
        userRepository.deleteById(id);
    }

    public Object getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            log.info("Поиск всех пользователей с пагинацией");

            return userRepository.findAll(pageable).stream()
                    .map(UserMapper.INSTANCE::toDto)
                    .collect(Collectors.toList());
        } else {
            log.info("Поиск указанных пользователей");
            return userRepository.findAllById(ids).stream()
                    .map(UserMapper.INSTANCE::toDto)
                    .collect(Collectors.toList());
        }
    }

    public CategoryDto createCategory(CategoryDto category) {
        validator.validateCategory(category);
        Category stored;
        log.debug("Получен запрос на создание категории {}", category.getName());
        try {
             stored = categoryRepository.save(CategoryMapper.INSTANCE.toCategory(category));
        } catch (RuntimeException e) {
            throw new NameAlreadyExistException("Имя категории уже используется", "Не соблюдены условия уникальности имени"
            , LocalDateTime.now());
    }
        return CategoryMapper.INSTANCE.toDto(stored);
    }

    public void deleteCategory(Long id) {
        log.debug("Получен запрос DELETE /admin/category/{catId}");
        Category stored = categoryRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Категория c id" + id + " не найдена"));
        categoryRepository.deleteById(id);
    }

}
