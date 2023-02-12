package ru.practicum.ewm.event.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.category.mappers.CategoryMapper;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.mappers.UserMapper;

@Mapper(componentModel = "spring",
        uses = {
                UserMapper.class,
                CategoryMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);
    @Mapping(target = "category.id", source = "category")
    Event toEvent (NewEventDto newEventDto);

    //EventDtoForCompilation toCompilationDto (EventFullDto eventFullDto);
    EventDtoForCompilation toCompilationDtoFromFull (EventFullDto eventFullDto);
    EventShortDto toEventShortDto (Event event);
    EventDto toEventDto (Event event);
    EventFullDto toEventFullDto (Event event);
    Location toEntity(Location location);

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "category", ignore = true)
    Event updateEventWithUser(EventUpdateDto eventUpdateDto, @MappingTarget Event event);
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "category", ignore = true)
    Event updateEventWithUser(EventUpdateAdminDto eventUpdateAdminDto, @MappingTarget Event stored);
}
