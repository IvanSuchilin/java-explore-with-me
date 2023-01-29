package ru.practicum.stat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


public interface StatDto {
     String getApp();
     String getUri();
     int getHits();
}
