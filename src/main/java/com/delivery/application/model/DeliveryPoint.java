package com.delivery.application.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalTime;

@Data

@Entity
@Table(name = "delivery_point")
public class DeliveryPoint implements Cloneable {
	private final static int EARTH_RADIUS_IN_KM = 6371;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Column(name = "latitude")
	private double latitude;

	@Column(name = "longitude")
	private double longitude;

	@Column(name = "delivery_from")
	private LocalTime deliveryFrom;

	@Column(name = "delivery_to")
	private LocalTime deliveryTo;

	public double getDistance(double latitudeFrom, double longitudeFrom) {

		// convert from degrees to radians
		double latitudeFromInRad = Math.toRadians(latitudeFrom);
		double longitudeFromInRad = Math.toRadians(longitudeFrom);
		double latitudeToInRad = Math.toRadians(this.latitude);
		double longitudeToInRad = Math.toRadians(this.longitude);

		// implement Haversine formula
		double longitudeDiff = longitudeFromInRad - longitudeToInRad;
		double latitudeDiff = latitudeFromInRad - latitudeToInRad;
		double a = Math.pow(Math.sin(latitudeDiff / 2), 2)
				+ Math.cos(latitudeFromInRad) * Math.cos(latitudeToInRad)
				* Math.pow(Math.sin(longitudeDiff / 2), 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return c * EARTH_RADIUS_IN_KM;
	}

	public static DeliveryPoint clonePoint(DeliveryPoint point) throws CloneNotSupportedException {
		return (DeliveryPoint) point.clone();
	}
}
