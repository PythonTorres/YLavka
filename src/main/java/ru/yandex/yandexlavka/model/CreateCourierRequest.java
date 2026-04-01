package ru.yandex.yandexlavka.model;

import lombok.Data;

import java.util.List;

@Data
public class CreateCourierRequest {
    private List<CreateCourierDto> couriers;

    public CreateCourierRequest() {
    }

    public CreateCourierRequest(List<CreateCourierDto> couriers) {
        this.couriers = couriers;
    }
}
