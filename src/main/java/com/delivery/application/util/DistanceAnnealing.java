package com.delivery.application.util;


import com.delivery.application.model.DeliveryPoint;
import com.delivery.application.model.Deliveryman;

import java.util.ArrayList;

public class DistanceAnnealing extends SimulatedAnnealing {

	public DistanceAnnealing(Deliveryman deliveryman, ArrayList<DeliveryPoint> deliveryPoints) {
		super(deliveryman, deliveryPoints);
	}

	@Override
	protected boolean isCurrentSolutionBetter(Route currentSolution, Route bestSolution) {
		if (currentSolution.getCountOfSuccessDelivery() != bestSolution.getCountOfSuccessDelivery()) {
			return currentSolution.getCountOfSuccessDelivery() > bestSolution.getCountOfSuccessDelivery();
		} else {
			return currentSolution.getDistance() < bestSolution.getDistance();
		}
	}

	@Override
	protected boolean needRevert(Route bestSolution, Route currentSolution, double temperature) {
		return Math.exp((bestSolution.getDistance() - currentSolution.getDistance()) / temperature) < Math.random();
	}
}
