package ru.yandex.yandexlavka.util;

import lombok.Data;
import ru.yandex.yandexlavka.entity.CourierEntity;
import ru.yandex.yandexlavka.entity.RegionEntity;
import ru.yandex.yandexlavka.entity.WorkingHoursEntity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CourierOptional {
    private Long id;
    private String type;
    private List<Integer> regions;
    private List<StartAndEndTime> workingHours = new ArrayList<>();

    public CourierOptional(CourierEntity courier) {
        this.id = courier.getCourierId();
        this.type = courier.getCourierType();
        this.regions = courier.getRegions().stream().map(RegionEntity::getRegionId).toList();
        for (String hours: courier.getWorkingHours().stream().map(WorkingHoursEntity::toString).toList()) {
            workingHours.add(new StartAndEndTime(hours));
        }
    }

    public boolean hasTimeForOrder(LocalTime timeRequired) {
        for (StartAndEndTime hours: workingHours) {
            if (hours.hasFreeTime(timeRequired))
                return true;
        }
        return false;
    }
}
