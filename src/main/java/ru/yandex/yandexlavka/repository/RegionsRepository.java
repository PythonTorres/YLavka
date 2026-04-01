package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.entity.RegionEntity;

@Repository
public interface RegionsRepository extends JpaRepository<RegionEntity, Integer> {
}
