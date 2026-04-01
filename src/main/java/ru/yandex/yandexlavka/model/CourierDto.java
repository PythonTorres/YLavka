package ru.yandex.yandexlavka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.yandexlavka.entity.CourierEntity;
import ru.yandex.yandexlavka.entity.RegionEntity;
import ru.yandex.yandexlavka.entity.WorkingHoursEntity;

import java.util.List;

@Data
public class CourierDto {
    @JsonProperty("courier_id")
    private Long courierId;
    @JsonProperty("courier_type")
    private String courierType;
    private List<Integer> regions;
    @JsonProperty("working_hours")
    private List<String> workingHours;

    public CourierDto() {
    }

    public CourierDto(Long courierId, String courierType, List<Integer> regions, List<String> workingHours) {
        this.courierId = courierId;
        this.courierType = courierType;
        this.regions = regions;
        this.workingHours = workingHours;
    }

    public CourierDto(CourierEntity courierEntity) {
        this.courierId = courierEntity.getCourierId();
        this.courierType = courierEntity.getCourierType();
        this.regions = courierEntity.getRegions().stream().map(RegionEntity::getRegionId).toList();
        this.workingHours = courierEntity.getWorkingHours().stream().map(WorkingHoursEntity::toString).toList();
    }
}
