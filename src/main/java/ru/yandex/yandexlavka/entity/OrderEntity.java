package ru.yandex.yandexlavka.entity;
;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.yandexlavka.model.CompleteOrder;
import ru.yandex.yandexlavka.model.CreateOrderDto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Entity
@Data
@Table(name="orders")
@EqualsAndHashCode(of = "orderId")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private Float weight;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regions")
    private RegionEntity regions;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "orders_delivery_hours", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "delivery_hours_id"))
    private List<DeliveryHoursEntity> deliveryHours;
    private Integer cost;
    private LocalDateTime completedTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id")
    private CourierEntity courier;

    public OrderEntity() {
    }

    public OrderEntity(Long orderId, Float weight, RegionEntity regions, List<DeliveryHoursEntity> deliveryHours, Integer cost, LocalDateTime completedTime) {
        this.orderId = orderId;
        this.weight = weight;
        this.regions = regions;
        this.deliveryHours = deliveryHours;
        this.cost = cost;
        this.completedTime = completedTime;
    }

    public OrderEntity(CreateOrderDto createOrderDto) {
        this.orderId = null;
        this.weight = createOrderDto.getWeight();
        this.regions = new RegionEntity(createOrderDto.getRegions());
        this.deliveryHours = createOrderDto.getDeliveryHours().stream().map(DeliveryHoursEntity::new).toList();
        this.cost = createOrderDto.getCost();
        this.completedTime = null;
    }

    public OrderEntity(OrderEntity orderEntity) {
        this.orderId = orderEntity.getOrderId();
        this.weight = orderEntity.getWeight();
        this.regions = orderEntity.getRegions();
        this.deliveryHours = orderEntity.getDeliveryHours();
        this.cost = orderEntity.getCost();
        this.completedTime = orderEntity.getCompletedTime();
        this.courier = orderEntity.getCourier();
    }

    public void setCompletedTime(String completedTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        this.completedTime = LocalDateTime.parse(completedTime, formatter);
    }
}
