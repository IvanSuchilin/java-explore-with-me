package ru.practicum.ewm.stats.mappers;

import dto.EndpointHitDto;
import ru.practicum.ewm.stats.model.EndpointHit;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatMapper {
    StatMapper INSTANCE = Mappers.getMapper(StatMapper.class);

    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);
}
