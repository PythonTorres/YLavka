package ru.yandex.yandexlavka.model;

import lombok.Data;

import java.util.List;

@Data
public class CreateCouriersResponse {
    private List<CourierDto> couriers;

    public CreateCouriersResponse() {
    }

    public CreateCouriersResponse(List<CourierDto> couriers) {
        this.couriers = couriers;
    }
}
