package ru.yandex.yandexlavka.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.yandexlavka.entity.DeliveryHoursEntity;
import ru.yandex.yandexlavka.entity.OrderEntity;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class OrderDto {
    @JsonProperty(value = "order_id")
    private Long orderId;
    private Float weight;
    private Integer regions;
    @JsonProperty(value = "delivery_hours")
    private List<String> deliveryHours;
    private Integer cost;
    @JsonProperty(value = "completed_time", required = false)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private String completedTime;

    public OrderDto() {
    }

    public OrderDto(Long orderId, Float weight, Integer regions, List<String> deliveryHours, Integer cost, String completedTime) {
        this.orderId = orderId;
        this.weight = weight;
        this.regions = regions;
        this.deliveryHours = deliveryHours;
        this.cost = cost;
        this.completedTime = completedTime;
    }

    public OrderDto(OrderEntity orderEntity) {
        this.orderId = orderEntity.getOrderId();
        this.weight = orderEntity.getWeight();
        this.regions = orderEntity.getRegions().getRegionId();
        this.deliveryHours = orderEntity.getDeliveryHours().stream().map(DeliveryHoursEntity::toString).toList();
        this.cost = orderEntity.getCost();
        if (orderEntity.getCompletedTime() == null)
            this.completedTime = null;
        else {
            ZonedDateTime zonedDateTime = ZonedDateTime.of(orderEntity.getCompletedTime(), ZoneOffset.UTC);
            this.completedTime = zonedDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        }
    }
}
