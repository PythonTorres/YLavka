package ru.yandex.yandexlavka.model;

import lombok.Data;

import java.util.List;

@Data
public class GetCouriersResponse {
    private List<CourierDto> couriers;
    private Integer limit;
    private Integer offset;

    public GetCouriersResponse() {
    }

    public GetCouriersResponse(List<CourierDto> couriers, Integer limit, Integer offset) {
        this.couriers = couriers;
        this.limit = limit;
        this.offset = offset;
    }
}
