package com.delivery.application.service;

import com.delivery.application.model.DeliveryPoint;
import com.delivery.application.model.Deliveryman;
import com.delivery.application.repository.DeliveryPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutBuildService {

	private DeliveryPointRepository deliveryPointRepository;

	@Autowired
	public RoutBuildService(DeliveryPointRepository deliveryPointRepository) {
		this.deliveryPointRepository = deliveryPointRepository;
	}

	public List<DeliveryPoint> getAvailableByTime(Deliveryman deliveryman) {
		return deliveryPointRepository.findAvailableByTime(deliveryman.getWorkStart(), deliveryman.getWorkEnd());
	}
}
