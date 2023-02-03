package ru.practicum.ewm.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.exceptions.userExceptions.EmailAlreadyExistException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mappers.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    public UserDto createUser(UserDto user) {
        log.debug("Получен запрос на создание пользователя {}", user.getName());
        User saveUser;
        try {
            saveUser = userRepository.save(UserMapper.INSTANCE.toUser(user));
        } catch (Throwable e) {
            throw new EmailAlreadyExistException("Пользователь с такой почтой уже существует");
        }
        return UserMapper.INSTANCE.toDto(saveUser);
    }

    public void delete(Long id) {
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

}
