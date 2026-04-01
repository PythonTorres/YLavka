package ru.yandex.yandexlavka.repository;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entity.CourierEntity;
import ru.yandex.yandexlavka.entity.OrderEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouriersRepository extends JpaRepository<CourierEntity, Long> {

    @Query(nativeQuery = true,
    value = "SELECT * FROM (\n" +
            "SELECT *, row_number() over (ORDER BY courier_id) as rownum FROM couriers\n" +
            ") as table_with_rownum\n" +
            "WHERE rownum > ? LIMIT ?;")
    List<CourierEntity> getCourierEntitiesByOffsetAndLimit(Integer offset, Integer limit);

    @Query(nativeQuery = true,
    value = "SELECT sum(o.cost) FROM couriers c, orders o\n" +
            "WHERE c.courier_id = ?\n" +
            "AND c.courier_id = o.courier_id\n" +
            "AND o.completed_time >= ?\n" +
            "AND o.completed_time < ?;")
    Integer getSumOfEarningsByIdAndTimeRange(Long courierId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(nativeQuery = true,
    value = "SELECT count(o.order_id) FROM couriers c, orders o\n" +
            "WHERE c.courier_id = ?\n" +
            "AND c.courier_id = o.courier_id\n" +
            "AND o.completed_time >= ?\n" +
            "AND o.completed_time < ?;")
    Integer getCountOfCompletedOrdersByIdAndTimeRange(Long courierId, LocalDateTime startDate, LocalDateTime endDate);

}
