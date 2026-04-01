package ru.yandex.yandexlavka.entity;

import jakarta.persistence.*;
import lombok.Data;


import java.util.List;

@Entity
@Data
@Table(name="regions")
public class RegionEntity {
    @Id
    private Integer regionId;

    @ManyToMany(mappedBy = "regions", fetch = FetchType.LAZY)
    private List<CourierEntity> couriers;

    @OneToMany(mappedBy = "regions", fetch = FetchType.LAZY)
    private List<OrderEntity> orders;

    public RegionEntity() {
    }

    public RegionEntity(Integer regionId) {
        this.regionId = regionId;
    }

    public RegionEntity(Integer regionId, List<CourierEntity> couriers) {
        this.regionId = regionId;
        this.couriers = couriers;
    }
}
