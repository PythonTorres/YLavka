package ru.yandex.yandexlavka.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.entity.CourierEntity;
import ru.yandex.yandexlavka.entity.WorkingHoursEntity;
import ru.yandex.yandexlavka.model.CreateCourierDto;
import ru.yandex.yandexlavka.repository.CouriersRepository;
import ru.yandex.yandexlavka.repository.RegionsRepository;
import ru.yandex.yandexlavka.repository.WorkingHoursRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class CouriersService {
    @Autowired
    private CouriersRepository couriersRepository;
    @Autowired
    private RegionsRepository regionsRepository;
    @Autowired
    private WorkingHoursRepository workingHoursRepository;

    @Transactional
    public CourierEntity createCourier(CreateCourierDto createCourierDto) {
        CourierEntity courierDto = new CourierEntity(createCourierDto);
        regionsRepository.saveAll(courierDto.getRegions());
        for (WorkingHoursEntity workingHours : courierDto.getWorkingHours()) {
            WorkingHoursEntity foundEntity = workingHoursRepository.findByStartDateAndEndDate(workingHours.getStartDate(), workingHours.getEndDate());
            if (foundEntity != null) {
                workingHours.setWorkingHoursId(foundEntity.getWorkingHoursId());
            }
        }
        workingHoursRepository.saveAll(courierDto.getWorkingHours());
        CourierEntity createdCourier = couriersRepository.save(courierDto);
        return createdCourier;
    }

    public Integer getCourierRatingByIdAndTimeRange(Long courierId, LocalDateTime startDate, LocalDateTime endDate) {
        CourierEntity courierEntity = couriersRepository.getReferenceById(courierId);
        if (courierEntity.getOrders() == null || courierEntity.getOrders().size() == 0)
            return null;
        Integer numberOfCompletedOrders = couriersRepository.getCountOfCompletedOrdersByIdAndTimeRange(courierId, startDate, endDate);
        if (numberOfCompletedOrders == 0)
            return null;
        Long numberOfHoursBetweenDates = ChronoUnit.HOURS.between(startDate, endDate);
        Double doubleResult = (numberOfCompletedOrders.doubleValue() / numberOfHoursBetweenDates.doubleValue()) * courierEntity.getRatingCoefficient().doubleValue();
        return (int) Math.round(doubleResult);
    }

    public Integer getCourierEarningsByIdAndTimeRange(Long courierId, LocalDateTime startDate, LocalDateTime endDate) {
        CourierEntity courierEntity = couriersRepository.getReferenceById(courierId);
        if (courierEntity.getOrders() == null || courierEntity.getOrders().size() == 0)
            return null;
        Integer sumOfCostsOfCompletedOrders = couriersRepository.getSumOfEarningsByIdAndTimeRange(courierId, startDate, endDate);
        if (sumOfCostsOfCompletedOrders == null)
            return null;
        return sumOfCostsOfCompletedOrders * courierEntity.getEarningsCoefficient();
    }
}
