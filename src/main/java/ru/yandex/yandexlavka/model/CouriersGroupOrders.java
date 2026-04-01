package ru.yandex.yandexlavka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CouriersGroupOrders {
    @JsonProperty(value = "courier_id")
    private Long courierId;
    private List<GroupOrders> orders;

    public CouriersGroupOrders() {
    }

    public CouriersGroupOrders(Long courierId, List<GroupOrders> orders) {
        this.courierId = courierId;
        this.orders = orders;
    }
}
