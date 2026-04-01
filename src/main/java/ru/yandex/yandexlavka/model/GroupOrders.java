package ru.yandex.yandexlavka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GroupOrders {
    @JsonProperty(value = "group_order_id")
    private Long groupOrderId;
    private List<OrderDto> orders;

    public GroupOrders() {
    }

    public GroupOrders(Long groupOrderId, List<OrderDto> orders) {
        this.groupOrderId = groupOrderId;
        this.orders = orders;
    }
}
