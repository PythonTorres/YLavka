package ru.yandex.yandexlavka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderDto {
    private Float weight;
    private Integer regions;
    @JsonProperty(value = "delivery_hours")
    private List<String> deliveryHours;
    private Integer cost;

    public CreateOrderDto() {
    }

    public CreateOrderDto(Float weight, Integer regions, List<String> deliveryHours, Integer cost) {
        this.weight = weight;
        this.regions = regions;
        this.deliveryHours = deliveryHours;
        this.cost = cost;
    }
}
