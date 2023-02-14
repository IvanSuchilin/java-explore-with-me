package ru.practicum.ewm.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository <Comment, Long> {
    List<Comment> getAllByEventId(Long eventId);

    List<Comment> getAllByUserId(Long userId);
}
