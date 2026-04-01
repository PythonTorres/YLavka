package ru.yandex.yandexlavka.model;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    List<CreateOrderDto> orders;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(List<CreateOrderDto> orders) {
        this.orders = orders;
    }
}
