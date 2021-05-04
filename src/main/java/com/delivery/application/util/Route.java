package com.delivery.application.util;

import com.delivery.application.model.DeliveryPoint;
import com.delivery.application.model.Deliveryman;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;

@Data
@AllArgsConstructor
public class Route {

	private Deliveryman deliveryman;
	private ArrayList<DeliveryPoint> route;
	private ArrayList<DeliveryPoint> previousRoute;
	private LocalTime finishTime;
	private int countOfSuccessDelivery;
	private double distance;
	private int waitingTimeInSec;

	public Route(Deliveryman deliveryman, ArrayList<DeliveryPoint> points) {
		route = points;
		previousRoute = new ArrayList<>();
		this.deliveryman = deliveryman;
	}

	public Route(Route route) throws CloneNotSupportedException {
		this(
				Deliveryman.getClone(route.getDeliveryman()),
				clonePoints(route.getRoute()),
				route.getPreviousRoute() == null ? new ArrayList<>() : clonePoints(route.getPreviousRoute()),
				route.getFinishTime(),
				route.getCountOfSuccessDelivery(),
				route.getDistance(),
				route.getWaitingTimeInSec());
	}

	public void generateInitialRout() {
		Collections.shuffle(route);
	}

	public void swapPoints() {
		int a = generateRandomIndex();
		int b = generateRandomIndex();
		previousRoute = clonePoints(route);
		DeliveryPoint pointX = route.get(a);
		DeliveryPoint pointY = route.get(b);
		route.set(a, pointY);
		route.set(b, pointX);
	}

	public void revertSwap() {
		route = previousRoute;
	}

	public void calculateRoute() {
		//zeroing route data
		countOfSuccessDelivery = 0;
		distance = 0;
		LocalTime arrivalTime = deliveryman.getWorkStart();
		finishTime = deliveryman.getWorkStart();
		waitingTimeInSec = 0;

		double currentLatitude = deliveryman.getLatitude();
		double currentLongitude = deliveryman.getLongitude();
		double deliverymanSpeed = deliveryman.getSpeed();

		for (DeliveryPoint point : route) {
			double way = point.getDistance(currentLatitude, currentLongitude);
			int secondsInTravel = (int) (60 * 60 * way / deliverymanSpeed);

			/*
			  additional check. In the application description information about the delivery time is
			  stored in LocalTime, not in LocalDateTime (it is possible that the deliveryman can reach the
			  delivery point the next day). The best solution, in my opinion, would be to change the data format
			  in the application description (in the Database and in the input file)
			 */
			if (!isSameDay(arrivalTime.toSecondOfDay(), secondsInTravel)) {
				break;
			}

			if (isDeliverySuccess(arrivalTime.plus(secondsInTravel, ChronoUnit.SECONDS), point.getDeliveryTo())) {
				countOfSuccessDelivery++;
				distance += way;

				arrivalTime = arrivalTime.plus(secondsInTravel, ChronoUnit.SECONDS);
				if (arrivalTime.isBefore(point.getDeliveryFrom())) {
					arrivalTime = point.getDeliveryFrom();
				}
				waitingTimeInSec =
						Math.max(waitingTimeInSec, arrivalTime.toSecondOfDay() - point.getDeliveryFrom().toSecondOfDay());
				arrivalTime = arrivalTime.plus(deliveryman.getSpendAtPoint().toSecondOfDay(), ChronoUnit.SECONDS);

				finishTime = arrivalTime;

				currentLatitude = point.getLatitude();
				currentLongitude = point.getLongitude();
			}
		}
	}

	public int getSecTimeInRoad() {
		return deliveryman.getWorkStart().toSecondOfDay() - finishTime.toSecondOfDay();
	}

	private int generateRandomIndex() {
		return (int) (Math.random() * route.size());
	}

	private boolean isDeliverySuccess(LocalTime arrivalTime, LocalTime deliveryTo) {
		return arrivalTime.isBefore(deliveryTo);
	}

	private boolean isSameDay(int arrivalTimeSeconds, int secondsInTravel) {
		return LocalTime.MAX.toSecondOfDay() - (arrivalTimeSeconds + secondsInTravel) >= 0;
	}

	private static ArrayList<DeliveryPoint> clonePoints(ArrayList<DeliveryPoint> oldPoints) {
		ArrayList<DeliveryPoint> newPoints = new ArrayList<>();
		for (DeliveryPoint point : oldPoints) {
			try {
				newPoints.add(DeliveryPoint.clonePoint(point));
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return newPoints;
	}
}
