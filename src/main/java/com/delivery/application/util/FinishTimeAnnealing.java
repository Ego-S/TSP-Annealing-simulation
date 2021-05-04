package com.delivery.application.util;

import com.delivery.application.model.DeliveryPoint;
import com.delivery.application.model.Deliveryman;

import java.util.ArrayList;

public class FinishTimeAnnealing extends SimulatedAnnealing {

	public FinishTimeAnnealing(Deliveryman deliveryman, ArrayList<DeliveryPoint> deliveryPoints) {
		super(deliveryman, deliveryPoints);
	}

	@Override
	protected boolean isCurrentSolutionBetter(Route currentSolution, Route bestSolution) {
		if (currentSolution.getCountOfSuccessDelivery() != bestSolution.getCountOfSuccessDelivery()) {
			return currentSolution.getCountOfSuccessDelivery() > bestSolution.getCountOfSuccessDelivery();
		} else {
			return currentSolution.getFinishTime().isBefore(bestSolution.getFinishTime());
		}
	}

	@Override
	protected boolean needRevert(Route bestSolution, Route currentSolution, double temperature) {
		return Math.exp(
				(bestSolution.getFinishTime().toSecondOfDay() - currentSolution.getFinishTime().toSecondOfDay())
						/ temperature) < Math.random();
	}
}
