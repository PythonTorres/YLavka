package ru.yandex.yandexlavka.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.yandexlavka.model.CreateCourierDto;

import java.util.List;

@Entity
@Data
@Table(name="couriers")
public class CourierEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courierId;
    private String courierType;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "couriers_regions", joinColumns = @JoinColumn(name = "courier_id"), inverseJoinColumns = @JoinColumn(name = "region_id"))
    private List<RegionEntity> regions;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "couriers_working_hours", joinColumns = @JoinColumn(name = "courier_id"), inverseJoinColumns = @JoinColumn(name = "working_hours_id"))
    private List<WorkingHoursEntity> workingHours;
    @OneToMany(mappedBy = "courier", fetch = FetchType.LAZY)
    private List<OrderEntity> orders;


    public CourierEntity() {
    }

    public CourierEntity(Long courierId, String courierType, List<RegionEntity> regions, List<WorkingHoursEntity> workingHours) {
        this.courierId = courierId;
        this.courierType = courierType;
        this.regions = regions;
        this.workingHours = workingHours;
    }

    public CourierEntity(CreateCourierDto createCourierDto) {
        this.courierId = null;
        this.courierType = createCourierDto.getCourierType();
        this.regions = createCourierDto.getRegions().stream().map(RegionEntity::new).toList();
        this.workingHours = createCourierDto.getWorkingHours().stream().map(WorkingHoursEntity::new).toList();
    }

    public Integer getRatingCoefficient() {
        return this.courierType.equals("FOOT") ? 3 : this.courierType.equals("BIKE") ? 2 : this.courierType.equals("AUTO") ? 1 : null;
    }

    public Integer getEarningsCoefficient() {
        return this.courierType.equals("FOOT") ? 2 : this.courierType.equals("BIKE") ? 3 : this.courierType.equals("AUTO") ? 4 : null;
    }

    @Override
    public String toString() {
        return "CourierEntity{" +
                "courierId=" + courierId +
                '}';
    }
}
