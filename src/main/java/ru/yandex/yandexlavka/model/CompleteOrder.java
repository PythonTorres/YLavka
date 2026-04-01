package ru.yandex.yandexlavka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompleteOrder {
    @JsonProperty(value = "courier_id")
    private Long courierId;
    @JsonProperty(value = "order_id")
    private Long orderId;
    @JsonProperty(value = "complete_time")
    private String completeTime;

    public CompleteOrder() {
    }

    public CompleteOrder(Long courierId, Long orderId, String completeTime) {
        this.courierId = courierId;
        this.orderId = orderId;
        this.completeTime = completeTime;
    }
}
