package ru.yandex.yandexlavka.util;

import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.Duration;
import java.time.LocalTime;

@Data
public class StartAndEndTime {
    LocalTime start;
    LocalTime end;

    public StartAndEndTime(String hours) {
        String[] timeParts = hours.split("-");
        String startString = timeParts[0];
        String endString = timeParts[1];

        String[] startParts = startString.split(":");
        int startHour = Integer.parseInt(startParts[0]);
        int startMinute = Integer.parseInt(startParts[1]);
        this.start = LocalTime.of(startHour, startMinute);

        String[] endParts = endString.split(":");
        int endHour = Integer.parseInt(endParts[0]);
        int endMinute = Integer.parseInt(endParts[1]);
        this.end = LocalTime.of(endHour, endMinute);
    }

    public boolean hasFreeTime(LocalTime time) {
        if (start.isAfter(end))
            return false;
        if (LocalTime.MIN.plus(Duration.between(start, end)).isAfter(time)){
            start = start.plus(Duration.between(LocalTime.MIN, time));
            return true;
        }
        return false;
    }
}
