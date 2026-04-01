package ru.yandex.yandexlavka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.yandexlavka.entity.OrderEntity;
import ru.yandex.yandexlavka.model.*;
import ru.yandex.yandexlavka.repository.OrdersRepository;
import ru.yandex.yandexlavka.service.OrdersService;
import ru.yandex.yandexlavka.util.JsonValidator;
import ru.yandex.yandexlavka.util.RateLimited;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("orders")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrdersRepository ordersRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping()
    @RateLimited
    public ResponseEntity createOrder(@RequestBody(required = false) String json) {
        if (json == null) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        JsonValidator jsonValidator = new JsonValidator("CreateOrderRequest");
        if (!jsonValidator.validateJsonString(json))
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        CreateOrderRequest createOrderRequest;
        try {
            createOrderRequest = objectMapper.readValue(json, CreateOrderRequest.class);
        } catch (JsonProcessingException e) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        List<OrderDto> createdOrders = new ArrayList<>();
        for (CreateOrderDto createOrderDto : createOrderRequest.getOrders()) {
            createdOrders.add(new OrderDto(ordersService.createOrder(createOrderDto)));
        }

        return new ResponseEntity(createdOrders, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @RateLimited
    public ResponseEntity getOrder(@PathVariable String id) {
        if (id == null || id.isEmpty()) return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        Long orderId;
        OrderEntity orderEntity;
        OrderDto orderDto;
        try {
            orderId = Long.valueOf(id);
            orderEntity = ordersRepository.getReferenceById(orderId);
            orderDto = new OrderDto(orderEntity);
        }
        catch (NumberFormatException e) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        catch (EntityNotFoundException e) {
            return new ResponseEntity("{}", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(orderDto, HttpStatus.OK);
    }

    @GetMapping()
    @RateLimited
    public ResponseEntity getOrders(@RequestParam(required = false) String limit, @RequestParam(required = false) String offset) {
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

        List<OrderEntity> orderEntities = ordersRepository.getOrderEntitiesByOffsetAndLimit(offsetInt, limitInt);
        List<OrderDto> response = orderEntities.stream().map(OrderDto::new).toList();
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("complete")
    @RateLimited
    public ResponseEntity completeOrder(@RequestBody(required = false) String json) {
        if (json == null) return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);

        JsonValidator jsonValidator = new JsonValidator("CompleteOrderRequestDto");
        if (!jsonValidator.validateJsonString(json))
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        CompleteOrderRequestDto completeOrderRequestDto;
        try {
            completeOrderRequestDto = objectMapper.readValue(json, CompleteOrderRequestDto.class);
        } catch (JsonProcessingException e) {
            return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
        }
        List<OrderEntity> savedEntities = ordersService.completeOrders(completeOrderRequestDto.getCompleteInfo());
        if (savedEntities == null) return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);

        List<OrderDto> completedOrders = savedEntities.stream().map(OrderDto::new).toList();


        return new ResponseEntity(completedOrders, HttpStatus.OK);
    }

    @PostMapping("assign")
    @RateLimited
    public ResponseEntity ordersAssign(@RequestParam String date) {
        LocalDate localDate;
        if (date == null || date.isEmpty())
            localDate = LocalDate.now();
        else
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                localDate = LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                return new ResponseEntity("{}", HttpStatus.BAD_REQUEST);
            }

        OrderAssignResponse orderAssignResponse = ordersService.assignCouriersToGroupOrders();
        orderAssignResponse.setDate(date);

        return new ResponseEntity(orderAssignResponse, HttpStatus.OK);
    }
}
