package ru.yandex.yandexlavka.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.yandexlavka.model.*;
import ru.yandex.yandexlavka.util.CourierOptional;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.entity.CourierEntity;
import ru.yandex.yandexlavka.entity.DeliveryHoursEntity;
import ru.yandex.yandexlavka.entity.OrderEntity;
import ru.yandex.yandexlavka.repository.CouriersRepository;
import ru.yandex.yandexlavka.repository.DeliveryHoursRepository;
import ru.yandex.yandexlavka.repository.OrdersRepository;
import ru.yandex.yandexlavka.repository.RegionsRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrdersService {
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private DeliveryHoursRepository deliveryHoursRepository;
    @Autowired
    private RegionsRepository regionsRepository;
    @Autowired
    private CouriersRepository couriersRepository;

    @Transactional
    public OrderEntity createOrder(CreateOrderDto createOrderDto) {
        OrderEntity orderEntity = new OrderEntity(createOrderDto);
        regionsRepository.save(orderEntity.getRegions());
        for (DeliveryHoursEntity deliveryHours : orderEntity.getDeliveryHours()) {
            DeliveryHoursEntity foundEntity = deliveryHoursRepository.findByStartDateAndEndDate(deliveryHours.getStartDate(), deliveryHours.getEndDate());
            if (foundEntity != null) {
                deliveryHours.setDeliveryHoursId(foundEntity.getDeliveryHoursId());
            }
        }
        deliveryHoursRepository.saveAll(orderEntity.getDeliveryHours());
        OrderEntity createdOrder = ordersRepository.save(orderEntity);
        return createdOrder;
    }

    @Transactional
    public List<OrderEntity> completeOrders(List<CompleteOrder> completeOrders) {
        List<OrderEntity> orderEntities = new ArrayList<>();
        Map<Long, OrderEntity> addedEntities = new HashMap<>();
        for (CompleteOrder completeOrder : completeOrders) {
            OrderEntity orderEntity;
            CourierEntity courierEntity;
            try {
                orderEntity = ordersRepository.getReferenceById(completeOrder.getOrderId());
                courierEntity = couriersRepository.getReferenceById(completeOrder.getCourierId());
            } catch (EntityNotFoundException e) {
                return null;
            }
            if (orderEntity.getCourier() != null && !orderEntity.getCourier().getCourierId().equals(courierEntity.getCourierId())) {
                return null;
            }
            if (orderEntity.getCompletedTime() != null && !orderEntity.getCompletedTime().equals(LocalDateTime.parse(completeOrder.getCompleteTime(), DateTimeFormatter.ISO_ZONED_DATE_TIME))) {
                return null;
            }
            orderEntity.setCompletedTime(completeOrder.getCompleteTime());
            orderEntity.setCourier(courierEntity);
            if (addedEntities.containsKey(orderEntity.getOrderId()) && (
                    !addedEntities.get(orderEntity.getOrderId()).getCompletedTime().equals(orderEntity.getCompletedTime()) ||
                    !addedEntities.get(orderEntity.getOrderId()).getCourier().getCourierId().equals(orderEntity.getCourier().getCourierId()))) {
                return null;
            }
            OrderEntity orderEntityToAdd = new OrderEntity(orderEntity);
            addedEntities.put(orderEntity.getOrderId(), orderEntityToAdd);
            orderEntities.add(orderEntity);
        }

        ordersRepository.saveAll(orderEntities);
        return orderEntities;
    }

    @Transactional
    public OrderAssignResponse assignCouriersToGroupOrders() {
        OrderAssignResponse response = new OrderAssignResponse();
        List<CouriersGroupOrders> couriersGroupOrders = new ArrayList<>();

        List<OrderEntity> orders = ordersRepository.getOrderEntitiesByCourierIsNull();
        List<CourierEntity> couriers = couriersRepository.findAll();

        orders.sort((o1, o2) -> o1.getCost() - o2.getCost());

        List<CourierOptional> footCouriers = couriers.stream().filter(t -> "FOOT".equals(t.getCourierType()))
                .map(CourierOptional::new).toList();
        List<CourierOptional> bikeCouriers = couriers.stream().filter(t -> "BIKE".equals(t.getCourierType()))
                .map(CourierOptional::new).toList();
        List<CourierOptional> autoCouriers = couriers.stream().filter(t -> "AUTO".equals(t.getCourierType()))
                .map(CourierOptional::new).toList();

        boolean flag = true;
        while (flag) {
            boolean courierAssigned = false;
            for (CourierOptional courier : autoCouriers) {
                Double totalWeight = 40.0;
                Set<Integer> visitedRegions = new HashSet<>();
                OrderEntity order = getOrderWithLowestCost(orders, courier, totalWeight, LocalTime.of(0, 8));
                if (order == null)
                    continue;
                courierAssigned = true;

                GroupOrders groupOrders = new GroupOrders(order.getOrderId(), new ArrayList<>());
                List<OrderDto> orderDtoList = new ArrayList<>();
                orderDtoList.add(new OrderDto(order));

                order.setCourier(couriersRepository.getReferenceById(courier.getId()));
                visitedRegions.add(order.getRegions().getRegionId());
                totalWeight -= order.getWeight();

                for (int i = 0; i < 6; i++) {
                    OrderEntity order1 = getOrderWithHighestCost(orders, courier, totalWeight,
                            LocalTime.of(0, 4), visitedRegions, 3);
                    if (order1 == null) {
                        break;
                    }

                    orderDtoList.add(new OrderDto(order));

                    order1.setCourier(couriersRepository.getReferenceById(courier.getId()));
                    totalWeight -= order.getWeight();
                }

                groupOrders.setOrders(orderDtoList);
                boolean courierExists = false;
                for (CouriersGroupOrders courierWithGroups : couriersGroupOrders) {
                    if (courierWithGroups.getCourierId().equals(courier.getId())) {
                        courierWithGroups.getOrders().add(groupOrders);
                        courierExists = true;
                        break;
                    }
                }
                List<GroupOrders> groupOrdersForCourier = new ArrayList<>();
                groupOrdersForCourier.add(groupOrders);
                if (!courierExists) {
                    couriersGroupOrders.add(new CouriersGroupOrders(courier.getId(), groupOrdersForCourier));
                }
            }
            flag = courierAssigned;
        }

        flag = true;
        while (flag) {
            boolean courierAssigned = false;
            for (CourierOptional courier : bikeCouriers) {
                Double totalWeight = 20.0;
                Set<Integer> visitedRegions = new HashSet<>();
                OrderEntity order = getOrderWithLowestCost(orders, courier, totalWeight, LocalTime.of(0, 12));
                if (order == null)
                    continue;
                courierAssigned = true;
                GroupOrders groupOrders = new GroupOrders(order.getOrderId(), new ArrayList<>());
                List<OrderDto> orderDtoList = new ArrayList<>();
                orderDtoList.add(new OrderDto(order));
                order.setCourier(couriersRepository.getReferenceById(courier.getId()));
                visitedRegions.add(order.getRegions().getRegionId());
                totalWeight -= order.getWeight();

                for (int i = 0; i < 3; i++) {
                    OrderEntity order1 = getOrderWithHighestCost(orders, courier, totalWeight,
                            LocalTime.of(0, 8), visitedRegions, 2);
                    if (order1 == null) {
                        break;
                    }
                    orderDtoList.add(new OrderDto(order));
                    order1.setCourier(couriersRepository.getReferenceById(courier.getId()));
                    totalWeight -= order.getWeight();
                }
                groupOrders.setOrders(orderDtoList);
                boolean courierExists = false;
                for (CouriersGroupOrders courierWithGroups : couriersGroupOrders) {
                    if (courierWithGroups.getCourierId().equals(courier.getId())) {
                        courierWithGroups.getOrders().add(groupOrders);
                        courierExists = true;
                        break;
                    }
                }
                List<GroupOrders> groupOrdersForCourier = new ArrayList<>();
                groupOrdersForCourier.add(groupOrders);
                if (!courierExists) {
                    couriersGroupOrders.add(new CouriersGroupOrders(courier.getId(), groupOrdersForCourier));
                }
            }
            flag = courierAssigned;
        }

        flag = true;
        while (flag) {
            boolean courierAssigned = false;
            for (CourierOptional courier : footCouriers) {
                Double totalWeight = 10.0;
                Set<Integer> visitedRegions = new HashSet<>();
                OrderEntity order = getOrderWithLowestCost(orders, courier, totalWeight, LocalTime.of(0, 25));
                if (order == null)
                    continue;
                courierAssigned = true;
                GroupOrders groupOrders = new GroupOrders(order.getOrderId(), new ArrayList<>());
                List<OrderDto> orderDtoList = new ArrayList<>();
                orderDtoList.add(new OrderDto(order));
                order.setCourier(couriersRepository.getReferenceById(courier.getId()));
                visitedRegions.add(order.getRegions().getRegionId());
                totalWeight -= order.getWeight();

                for (int i = 0; i < 1; i++) {
                    OrderEntity order1 = getOrderWithHighestCost(orders, courier, totalWeight,
                            LocalTime.of(0, 10), visitedRegions, 1);
                    if (order1 == null) {
                        break;
                    }
                    orderDtoList.add(new OrderDto(order));
                    order1.setCourier(couriersRepository.getReferenceById(courier.getId()));
                    totalWeight -= order.getWeight();
                }
                groupOrders.setOrders(orderDtoList);
                boolean courierExists = false;
                for (CouriersGroupOrders courierWithGroups : couriersGroupOrders) {
                    if (courierWithGroups.getCourierId().equals(courier.getId())) {
                        courierWithGroups.getOrders().add(groupOrders);
                        courierExists = true;
                        break;
                    }
                }
                List<GroupOrders> groupOrdersForCourier = new ArrayList<>();
                groupOrdersForCourier.add(groupOrders);
                if (!courierExists) {
                    couriersGroupOrders.add(new CouriersGroupOrders(courier.getId(), groupOrdersForCourier));
                }
            }
            flag = courierAssigned;
        }

        ordersRepository.saveAll(orders);
        response.setCouriers(couriersGroupOrders);
        return response;

    }

    private OrderEntity getOrderWithLowestCost(List<OrderEntity> orders, CourierOptional courier,
                                         Double availableWeight, LocalTime timeRequired) {
        for (OrderEntity order: orders) {
            if (order.getCourier() == null && order.getWeight() <= availableWeight && courier.getRegions().contains(order.getRegions().getRegionId())
                    && courier.hasTimeForOrder(timeRequired)) {
                return order;
            }
        }
        return null;
    }

    private OrderEntity getOrderWithHighestCost(List<OrderEntity> orders, CourierOptional courier,
                                          Double availableWeight, LocalTime timeRequired, Set<Integer> regions, Integer maxCountRegions) {
        for (int i = orders.size() - 1; i > -1; i--) {
            if (orders.get(i).getCourier() == null && orders.get(i).getWeight() <= availableWeight) {
                if (regions.contains(orders.get(i).getRegions().getRegionId()) || regions.size() < maxCountRegions) {
                    if (courier.hasTimeForOrder(timeRequired)) {
                        regions.add(orders.get(i).getRegions().getRegionId());
                        return orders.get(i);
                    }
                }
            }
        }
        return null;
    }
}
