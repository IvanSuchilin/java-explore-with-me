package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Object createComment(Long userId, NewCommentDto newCommentDto) {
        log.info("Получен запрос на создание комментария для события id {}", newCommentDto.getEventId());
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id" + userId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        Event stored = eventRepository.findById(newCommentDto.getEventId()).orElseThrow(() ->
                new NotFoundException("Событие с id" + newCommentDto.getEventId() + "не найдено",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        Comment newComment = new Comment(null, newCommentDto.getText(), commentator, stored, LocalDateTime.now());
        return commentRepository.save(newComment);
    }

    public void deleteCommentByIdByOwner(Long userId, Long commentId) {
        log.info("Получен запрос на удаление комментария {}", commentId);
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id" + userId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        Comment stored = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id" + commentId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        if (!Objects.equals(commentator.getId(), stored.getId())) {
            throw new NotFoundException("Удалять комментарий может автор или администратор",
                    "Пользователь не автор", LocalDateTime.now());
        }
        commentRepository.deleteById(commentId);
    }

    public void deleteCommentByIdByAdmin(Long commentId) {
        log.info("Получен запрос на удаление комментария администратором{}", commentId);
        Comment stored = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id" + commentId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        commentRepository.deleteById(commentId);
    }
}
