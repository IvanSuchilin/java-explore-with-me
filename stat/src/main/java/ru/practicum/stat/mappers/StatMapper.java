package ru.practicum.stat.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.stat.dto.EndpointHitDto;
import ru.practicum.stat.model.EndpointHit;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatMapper {
    StatMapper INSTANCE = Mappers.getMapper(StatMapper.class);

    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);
}
