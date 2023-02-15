package ru.practicum.ewm.comment.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.mappers.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.QComment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.RequestValidationExceptions.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.validation.DtoValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final DtoValidator validator;

    public Object createComment(Long userId, NewCommentDto newCommentDto) {
        log.info("Получен запрос на создание комментария для события id {}", newCommentDto.getEventId());
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id" + userId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        Event stored = eventRepository.findById(newCommentDto.getEventId()).orElseThrow(() ->
                new NotFoundException("Событие с id" + newCommentDto.getEventId() + "не найдено",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        validator.validateNewCommentDto(newCommentDto);
        Comment newComment = new Comment(null, newCommentDto.getText(), commentator, stored, LocalDateTime.now());
        return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(newComment));
    }

    public void deleteCommentByIdByOwner(Long userId, Long commentId) {
        log.info("Получен запрос на удаление комментария {}", commentId);
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id" + userId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        Comment stored = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id" + commentId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        if (!Objects.equals(commentator.getId(), stored.getUser().getId())) {
            throw new NotFoundException("Удалять комментарий может автор или администратор",
                    "Пользователь не автор", LocalDateTime.now());
        }
        commentRepository.deleteById(commentId);
    }

    public void deleteCommentByIdByAdmin(Long commentId) {
        log.info("Получен запрос на удаление комментария администратором{}", commentId);
        commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id" + commentId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        commentRepository.deleteById(commentId);
    }

    public Object updateCommentForEvent(Long commentId, Long userId, UpdateCommentDto dto) {
        log.info("Получен запрос на обновление комментария {}", commentId);
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id" + userId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        Comment stored = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id" + commentId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        if (!Objects.equals(commentator.getId(), stored.getUser().getId())) {
            throw new NotFoundException("Обновлять комментарий может только автор",
                    "Пользователь не автор", LocalDateTime.now());
        }
        validator.validateUpdCommentDto(dto);
        Comment updComment = CommentMapper.INSTANCE.updateComment(dto, stored);
        return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(updComment));
    }

    public List<CommentDto> getCommentsByEventId(Long eventId) {
        log.info("Получен запрос на получение комментариев для события {}", eventId);
        return commentRepository.getAllByEventId(eventId).stream().map(CommentMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getAllForUser(Long userId) {
        log.info("Получен запрос на получение комментариев для пользователя {}", userId);
        return commentRepository.getAllByUserId(userId).stream().map(CommentMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }

    public CommentDto getCommentByIdForUser(Long userId, Long commentId) {
        log.info("Получен запрос на получение информации о комментарии {} для пользователя {}", commentId, userId);
        User commentator = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id" + userId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        Comment stored = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id" + commentId + "не найден",
                        "Запрашиваемый объект не найден или не доступен", LocalDateTime.now()));
        if (!Objects.equals(commentator.getId(), stored.getUser().getId())) {
            throw new NotFoundException("Получить информацию о комментарии может только автор",
                    "Пользователь не автор", LocalDateTime.now());
        }
        return CommentMapper.INSTANCE.toCommentDto(stored);
    }

    public List<CommentDto> getAll(LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "createdOn"));
        Predicate predicate = createPredicate(rangeStart, rangeEnd);
        return commentRepository.findAll(Objects.requireNonNull(predicate), pageable).getContent()
                .stream()
                .map(CommentMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }

    private BooleanBuilder createPredicate(LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd) {
        QComment qComment = QComment.comment;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (rangeStart != null) {
            booleanBuilder.and(qComment.createdOn.after(rangeStart));
        }
        if (rangeEnd != null) {
            booleanBuilder.and(qComment.createdOn.before(rangeEnd));
        }
        if (rangeStart == null) {
            booleanBuilder.and(qComment.createdOn.after(LocalDateTime.now()));
        }
        return booleanBuilder;
    }
}
