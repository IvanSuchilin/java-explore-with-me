package ru.practicum.ewm.comment.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.comment.dto.UpdateCommentDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.mappers.EventMapper;
import ru.practicum.ewm.user.mappers.UserMapper;

@Mapper(componentModel = "spring",
        uses = {
                UserMapper.class,
                EventMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "userId", source = "user.id")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "event", ignore = true)
    Comment updateComment(UpdateCommentDto updateCommentDto, @MappingTarget Comment storedComment);
}
