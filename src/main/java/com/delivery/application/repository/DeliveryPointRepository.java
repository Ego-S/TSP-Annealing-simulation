package com.delivery.application.repository;

import com.delivery.application.model.DeliveryPoint;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface DeliveryPointRepository extends CrudRepository<DeliveryPoint, Long> {
	@Query("SELECT dp FROM DeliveryPoint dp WHERE dp.deliveryTo > :start AND dp.deliveryFrom < :end")
	List<DeliveryPoint> findAvailableByTime(LocalTime start, LocalTime end);

}
