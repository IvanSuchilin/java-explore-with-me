package mappers;

import dto.EndpointHitDto;
import model.EndpointHit;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatMapper {
    StatMapper INSTANCE = Mappers.getMapper(StatMapper.class);

    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);
}
