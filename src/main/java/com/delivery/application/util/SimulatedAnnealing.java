package com.delivery.application.util;

import com.delivery.application.model.DeliveryPoint;
import com.delivery.application.model.Deliveryman;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter

public abstract class SimulatedAnnealing {
	private ArrayList<DeliveryPoint> deliveryPoints;
	private Deliveryman deliveryman;

	public SimulatedAnnealing(Deliveryman deliveryman, ArrayList<DeliveryPoint> deliveryPoints) {
		this.deliveryman = deliveryman;
		this.deliveryPoints = deliveryPoints;
	}

	public Route simulateAnnealing(
			double temperature,
			int numbersOfIteration,
			double coolingRate) throws CloneNotSupportedException {
		Route currentSolution = new Route(getDeliveryman(), new ArrayList<>(getDeliveryPoints()));
		currentSolution.calculateRoute();
		Route bestSolution = new Route(currentSolution);

		for (int i = 0; i < numbersOfIteration; i++) {
			if (temperature > 0.1) {
				currentSolution.swapPoints();
				currentSolution.calculateRoute();
				if (isCurrentSolutionBetter(currentSolution, bestSolution)) {
					bestSolution = new Route(currentSolution);
				} else if (needRevert(bestSolution, currentSolution, temperature)) {
					currentSolution.revertSwap();
				}
				temperature *= coolingRate;
			}
		}
		return bestSolution;
	}

	protected abstract boolean isCurrentSolutionBetter(Route currentSolution, Route bestSolution);

	protected abstract boolean needRevert(Route bestSolution, Route currentSolution, double temperature);
}
