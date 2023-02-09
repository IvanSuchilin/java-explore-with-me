package ru.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatusUpdateDto {
    private List<Long> ids;
    private Request.RequestStatus status;
}
