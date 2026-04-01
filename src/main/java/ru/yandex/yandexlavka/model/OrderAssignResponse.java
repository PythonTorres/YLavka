package ru.yandex.yandexlavka.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderAssignResponse {
    private String date;
    private List<CouriersGroupOrders> couriers;

    public OrderAssignResponse() {
    }

    public OrderAssignResponse(String date, List<CouriersGroupOrders> couriers) {
        this.date = date;
        this.couriers = couriers;
    }
}
