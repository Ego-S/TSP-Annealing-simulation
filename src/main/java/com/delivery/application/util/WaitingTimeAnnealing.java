package com.delivery.application.util;

import com.delivery.application.model.DeliveryPoint;
import com.delivery.application.model.Deliveryman;

import java.util.ArrayList;

public class WaitingTimeAnnealing extends SimulatedAnnealing {
	public WaitingTimeAnnealing(Deliveryman deliveryman, ArrayList<DeliveryPoint> deliveryPoints) {
		super(deliveryman, deliveryPoints);
	}

	@Override
	protected boolean isCurrentSolutionBetter(Route currentSolution, Route bestSolution) {
		if (currentSolution.getCountOfSuccessDelivery() != bestSolution.getCountOfSuccessDelivery()) {
			return currentSolution.getCountOfSuccessDelivery() > bestSolution.getCountOfSuccessDelivery();
		} else {
			return currentSolution.getWaitingTimeInSec() < bestSolution.getWaitingTimeInSec();
		}
	}

	@Override
	protected boolean needRevert(Route bestSolution, Route currentSolution, double temperature) {
		return Math.exp((bestSolution.getWaitingTimeInSec() - currentSolution.getWaitingTimeInSec())
				/ temperature) < Math.random();
	}
}
