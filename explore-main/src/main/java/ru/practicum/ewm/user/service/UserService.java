package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NameAlreadyExistException;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NotFoundException;
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
public class UserService {
    private final UserRepository userRepository;
    private final DtoValidator validator;

    public UserDto createUser(UserDto user) {
        validator.validateUserDto(user);
        log.debug("Получен запрос на создание пользователя {}", user.getName());
        User saveUser;
        try {
            saveUser = userRepository.save(UserMapper.INSTANCE.toUser(user));
        } catch (RuntimeException e) {
            throw new NameAlreadyExistException("Имя (почта) уже используется", "Не соблюдены условия уникальности имени (почты)",
                    LocalDateTime.now());
        }
        return UserMapper.INSTANCE.toDto(saveUser);
    }

    public void deleteUser(Long id) {
        log.debug("Получен запрос на удаление пользователя {}", id);
        userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь с id" + id + "не найден", "Запрашиваемый объект не найден или не доступен",
                        LocalDateTime.now()));
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
}
