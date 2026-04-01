package ru.yandex.yandexlavka.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Entity
@Data
@Table(name="delivery_hours")
public class DeliveryHoursEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryHoursId;
    private LocalTime startDate;
    private LocalTime endDate;
    @ManyToMany(mappedBy = "deliveryHours", fetch = FetchType.LAZY)
    List<OrderEntity> orders;

    public DeliveryHoursEntity() {
    }

    public DeliveryHoursEntity(Integer deliveryHoursId, LocalTime startDate, LocalTime endDate, List<OrderEntity> orders) {
        this.deliveryHoursId = deliveryHoursId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.orders = orders;
    }

    public DeliveryHoursEntity(String deliveryHours) {
        List<String> startAndEnd = Arrays.stream(deliveryHours.split("-")).toList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
        this.startDate = LocalTime.parse(startAndEnd.get(0), formatter);
        this.endDate = LocalTime.parse(startAndEnd.get(1), formatter);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return this.startDate.format(formatter) + "-" + this.endDate.format(formatter);
    }
}
