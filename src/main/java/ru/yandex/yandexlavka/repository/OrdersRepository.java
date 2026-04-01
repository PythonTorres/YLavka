package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entity.OrderEntity;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<OrderEntity, Long> {
    @Query(nativeQuery = true,
            value = "SELECT * FROM (\n" +
                    "SELECT *, row_number() over (ORDER BY order_id) as rownum FROM orders\n" +
                    ") as table_with_rownum\n" +
                    "WHERE rownum > ? LIMIT ?;")
    List<OrderEntity> getOrderEntitiesByOffsetAndLimit(Integer offset, Integer limit);
    List<OrderEntity> getOrderEntitiesByCourierIsNull();
}
