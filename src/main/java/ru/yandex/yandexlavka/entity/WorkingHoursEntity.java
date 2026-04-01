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
@Table(name="working_hours")
public class WorkingHoursEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer workingHoursId;
    private LocalTime startDate;
    private LocalTime endDate;

    @ManyToMany(mappedBy = "workingHours", fetch = FetchType.LAZY)
    List<CourierEntity> couriers;

    public WorkingHoursEntity() {
    }

    public WorkingHoursEntity(Integer workingHoursId, LocalTime startDate, LocalTime endDate, List<CourierEntity> couriers) {
        this.workingHoursId = workingHoursId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.couriers = couriers;
    }

    public WorkingHoursEntity(String workingHours) {
        List<String> startAndEnd = Arrays.stream(workingHours.split("-")).toList();
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
