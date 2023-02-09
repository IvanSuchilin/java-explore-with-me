package ru.practicum.ewm.request.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.category.mappers.CategoryMapper;
import ru.practicum.ewm.event.mappers.EventMapper;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.mappers.UserMapper;

@Mapper(componentModel = "spring",
        uses = {
                UserMapper.class,
                CategoryMapper.class,
                EventMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

   @Mapping(target = "event.id", source = "event")
    @Mapping(target = "requester.id", source = "requester")
    Request toRequest(RequestDto requestDto);


    @Mapping(source = "requester.id", target = "requester")
    @Mapping(source = "event.id", target = "event")
    RequestDto toRequestDto(Request request);
}
