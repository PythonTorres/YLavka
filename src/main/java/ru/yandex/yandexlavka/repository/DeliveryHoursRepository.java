package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entity.DeliveryHoursEntity;

import java.time.LocalTime;

@Repository
public interface DeliveryHoursRepository extends JpaRepository<DeliveryHoursEntity, Integer> {
    boolean existsByStartDateAndEndDate(LocalTime startDate, LocalTime endDate);
    DeliveryHoursEntity findByStartDateAndEndDate(LocalTime startDate, LocalTime endDate);
}
