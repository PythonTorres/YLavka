package ru.yandex.yandexlavka.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.yandexlavka.entity.CourierEntity;
import ru.yandex.yandexlavka.entity.RegionEntity;
import ru.yandex.yandexlavka.entity.WorkingHoursEntity;

import java.util.List;

@Data
public class GetCourierMetaInfoResponse {
    @JsonProperty(value = "courier_id")
    private Long courierId;
    @JsonProperty(value = "courier_type")
    private String courierType;
    private List<Integer> regions;
    @JsonProperty(value = "working_hours")
    private List<String> workingHours;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer rating;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer earnings;

    public GetCourierMetaInfoResponse() {
    }

    public GetCourierMetaInfoResponse(Long courierId, String courierType, List<Integer> regions, List<String> workingHours, Integer rating, Integer earnings) {
        this.courierId = courierId;
        this.courierType = courierType;
        this.regions = regions;
        this.workingHours = workingHours;
        this.rating = rating;
        this.earnings = earnings;
    }

    public GetCourierMetaInfoResponse(CourierEntity courierEntity, Integer rating, Integer earnings) {
        this.courierId = courierEntity.getCourierId();
        this.courierType = courierEntity.getCourierType();
        this.regions = courierEntity.getRegions().stream().map(RegionEntity::getRegionId).toList();
        this.workingHours = courierEntity.getWorkingHours().stream().map(WorkingHoursEntity::toString).toList();
        this.rating = rating;
        this.earnings = earnings;
    }
}
