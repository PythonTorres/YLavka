package ru.yandex.yandexlavka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CompleteOrderRequestDto {
    @JsonProperty(value = "complete_info")
    private List<CompleteOrder> completeInfo;

    public CompleteOrderRequestDto() {
    }

    public CompleteOrderRequestDto(List<CompleteOrder> completeInfo) {
        this.completeInfo = completeInfo;
    }
}
