package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entity.WorkingHoursEntity;

import java.time.LocalTime;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHoursEntity, Integer> {
    boolean existsByStartDateAndEndDate(LocalTime startDate, LocalTime endDate);
    WorkingHoursEntity findByStartDateAndEndDate(LocalTime startDate, LocalTime endDate);
}
