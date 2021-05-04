package com.delivery.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalTime;

@Data
public class Deliveryman implements Cloneable {
	@JsonProperty("work_start")
	private LocalTime workStart;
	@JsonProperty("work_end")
	private LocalTime workEnd;
	@JsonProperty("spend_at_point")
	private LocalTime spendAtPoint;
	private double speed;
	private double latitude;
	private double longitude;

	public static Deliveryman getClone(Deliveryman original) throws CloneNotSupportedException {
		return (Deliveryman) original.clone();
	}
}
