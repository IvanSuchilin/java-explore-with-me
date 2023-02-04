package ru.practicum.ewm.category.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDto toDto(Category category);
    Category toCategory(CategoryDto categoryDto);
}
