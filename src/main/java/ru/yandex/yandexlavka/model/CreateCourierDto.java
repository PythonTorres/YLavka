package ru.yandex.yandexlavka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateCourierDto {
    @JsonProperty("courier_type")
    private String courierType;
    private List<Integer> regions;
    @JsonProperty("working_hours")
    private List<String> workingHours;

    public CreateCourierDto() {
    }

    public CreateCourierDto(String courierType, List<Integer> regions, List<String> workingHours) {
        this.courierType = courierType;
        this.regions = regions;
        this.workingHours = workingHours;
    }
}
