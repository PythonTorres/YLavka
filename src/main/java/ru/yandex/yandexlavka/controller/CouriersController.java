package ru.yandex.yandexlavka.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.entity.CourierEntity;
import ru.yandex.yandexlavka.model.*;
import ru.yandex.yandexlavka.repository.CouriersRepository;
import ru.yandex.yandexlavka.service.CouriersService;
import ru.yandex.yandexlavka.util.JsonValidator;
import ru.yandex.yandexlavka.util.RateLimited;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("couriers")
public class CouriersController {
    @Autowired
    private CouriersRepository couriersRepository;
    @Autowired
    private CouriersService couriersService;
    private ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping()
    @RateLimited
    public ResponseEntity createCourier(@RequestBody(required = false) String json) {
        if (json == null) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        JsonValidator jsonValidator = new JsonValidator("CreateCourierRequest");
        if (!jsonValidator.validateJsonString(json)){
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        CreateCourierRequest createCourierRequest;
        try {
            createCourierRequest = objectMapper.readValue(json, CreateCourierRequest.class);
        } catch (JsonProcessingException e) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }

        List<CourierDto> createdCouriers = new ArrayList<>();
        for (CreateCourierDto createCourierDto : createCourierRequest.getCouriers()) {
            createdCouriers.add(new CourierDto(couriersService.createCourier(createCourierDto)));
        }

        return new ResponseEntity(new CreateCouriersResponse(createdCouriers), HttpStatus.OK);
    }

    @GetMapping("{id}")
    @RateLimited
    public ResponseEntity getCourierById(@PathVariable String id) {
        if (id == null || id.isEmpty()) return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        Long courierId;
        CourierEntity courierEntity;
        CourierDto courierDto;
        try {
            courierId = Long.valueOf(id);
            courierEntity = couriersRepository.getReferenceById(courierId);
            courierDto = new CourierDto(courierEntity);
        }
        catch (NumberFormatException e) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        catch (EntityNotFoundException e) {
            return new ResponseEntity("{}", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(courierDto, HttpStatus.OK);
    }

    @GetMapping()
    @RateLimited
    public ResponseEntity getCouriers(@RequestParam(required = false) String limit, @RequestParam(required = false) String offset) {
        Integer limitInt;
        Integer offsetInt;
        try {
            limitInt = limit == null || limit.isEmpty() ? 1 : Integer.valueOf(limit);
            offsetInt = offset == null || offset.isEmpty() ? 0 : Integer.valueOf(offset);
        }
        catch (NumberFormatException e) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        if (limitInt < 1 || offsetInt < 0) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        //List<CourierEntity> courierEntities = couriersRepository.findAll(PageRequest.of(0, offsetInt + limitInt)).stream().skip(offsetInt).toList();
        List<CourierEntity> courierEntities = couriersRepository.getCourierEntitiesByOffsetAndLimit(offsetInt, limitInt);
        GetCouriersResponse response = new GetCouriersResponse(courierEntities.stream().map(CourierDto::new).toList(), limitInt, offsetInt);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("meta-info/{courier_id}")
    @RateLimited
    public ResponseEntity getCourierMetaInfo(@PathVariable(value = "courier_id") String courierId,
                                             @RequestParam(required = false) String startDate,
                                             @RequestParam(required = false) String endDate) {
        if (startDate == null || endDate == null || courierId == null || startDate.isEmpty() || endDate.isEmpty() || courierId.isEmpty())
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        Long courierIdLong;
        LocalDateTime startDateTimestamp;
        LocalDateTime endDateTimestamp;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
            courierIdLong = Long.valueOf(courierId);
            startDateTimestamp = LocalDateTime.parse(startDate + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            endDateTimestamp = LocalDateTime.parse(endDate + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (NumberFormatException e) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        } catch (DateTimeParseException e) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        if (!couriersRepository.existsById(courierIdLong))
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);

        CourierEntity courierEntity = couriersRepository.getReferenceById(courierIdLong);
        Integer rating = couriersService.getCourierRatingByIdAndTimeRange(courierIdLong, startDateTimestamp, endDateTimestamp);
        Integer earnings = couriersService.getCourierEarningsByIdAndTimeRange(courierIdLong, startDateTimestamp, endDateTimestamp);

        GetCourierMetaInfoResponse courierMetaInfoResponse = new GetCourierMetaInfoResponse(courierEntity, rating, earnings);

        return new ResponseEntity(courierMetaInfoResponse, HttpStatus.OK);
    }

    @GetMapping("assignments")
    @RateLimited
    public ResponseEntity couriersAssignments(@RequestParam String date, @RequestParam(value = "courier_id") String courierId) {
        LocalDate localDate;
        Long courierIdLong;
        if (date == null || date.isEmpty())
            localDate = LocalDate.now();
        else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                localDate = LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
            }
        }
        if (courierId == null || courierId.isEmpty()) {
            //should return all couriers
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        try {
            courierIdLong = Long.valueOf(courierId);
        } catch (NumberFormatException e) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        if (!couriersRepository.existsById(courierIdLong)) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        LocalDateTime startDate = localDate.atStartOfDay();
        LocalDateTime endDate = localDate.plusDays(1L).atStartOfDay();

        //return courier and his group orders completed date between start and end

        return new ResponseEntity(HttpStatus.OK);
    }
}
