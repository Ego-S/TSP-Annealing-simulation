package com.delivery.application;

import com.delivery.application.model.DeliveryPoint;
import com.delivery.application.model.Deliveryman;
import com.delivery.application.service.RoutBuildService;
import com.delivery.application.util.DistanceAnnealing;
import com.delivery.application.util.FinishTimeAnnealing;
import com.delivery.application.util.Route;
import com.delivery.application.util.WaitingTimeAnnealing;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.exit;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Value("${temperature}")
	private double temperature;
	@Value("${numberOfIterations}")
	private int numberOfIterations;
	@Value("${coolingRate}")
	private double coolingRate;

	@Autowired
	private RoutBuildService routBuildService;

	@Autowired
	private ObjectMapper objectMapper;

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);
		application.setBannerMode(Banner.Mode.OFF);
		application.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		String path;

//		if (args[0] == null) {
//			System.out.println("\nERROR: Please, specify the path to the json file with deliveryman description");
//		} else {
//			path = args[0];
//		}

		//HARDCODE input
		path = "data/deliveryman.json";

		Deliveryman man;
		try	{
			man = objectMapper.readValue(new File(path), Deliveryman.class);
		} catch (JsonMappingException e) {
			System.out.println("\nERROR: Incorrect json file\n");
			return;
		}
		if (man.getSpeed() <= 0) {
			System.out.println("\nERROR: Deliveryman speed must be greater than 0\n");
			return;
		}

		List<DeliveryPoint> points = routBuildService.getAvailableByTime(man);
		Map<String, Route> result = new HashMap<>();

		DistanceAnnealing distanceAnnealing =
				new DistanceAnnealing(Deliveryman.getClone(man), (ArrayList<DeliveryPoint>) points);
		Route bestSolutionByDistance = distanceAnnealing.simulateAnnealing(temperature, numberOfIterations, coolingRate);
		result.put("Наименьшее расстояние. ", bestSolutionByDistance);

		FinishTimeAnnealing finishTimeAnnealing =
				new FinishTimeAnnealing(Deliveryman.getClone(man), (ArrayList<DeliveryPoint>) points);
		Route bestSolutionByFinishTime = finishTimeAnnealing.simulateAnnealing(temperature, numberOfIterations, coolingRate);
		result.put("Окончание работы. ", bestSolutionByFinishTime);

		WaitingTimeAnnealing waitingTimeAnnealing =
				new WaitingTimeAnnealing(Deliveryman.getClone(man), (ArrayList<DeliveryPoint>) points);
		Route bestSolutionByWaitingTime = waitingTimeAnnealing.simulateAnnealing(temperature, numberOfIterations, coolingRate);
		result.put("Время ожидания. ", bestSolutionByWaitingTime);

		printResult(result);

		exit(0);
	}




	private void printResult(Map<String, Route> result) {
		System.out.println("________");
		for (String key : result.keySet()) {
			printSolution(key, result.get(key));
		}
	}

	private void printSolution(String name, Route solution) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);

		ArrayList<DeliveryPoint> points = solution.getRoute();

		for (int i = 0; i < solution.getCountOfSuccessDelivery(); i++) {
			sb.append("[").append(points.get(i).getId()).append("]");
			if (i < solution.getCountOfSuccessDelivery() - 1) {
				sb.append(" => ");
			} else {
				sb.append(" | ");
			}
		}
		sb.append(getDistanceInfo(solution));
		sb.append(getTimeInRoadInfo(solution));
		sb.append(getWaitingTimeInfo(solution));
		sb.append(getFinishTimeInfo(solution));
		sb.append(getFailedDeliveryInfo(solution)).append("\n________");
		System.out.println(sb.toString());
	}

	private String getDistanceInfo(Route route) {
		long km = Math.round(route.getDistance());
		return "Расстояние: " + km + " км. ";
	}

	private String getTimeInRoadInfo(Route route) {
		LocalTime timeInRoad = route.getFinishTime().minus(route.getDeliveryman().getWorkStart().toSecondOfDay(),
				ChronoUnit.SECONDS);
		return "Время в пути: " + getCorrectTimeInfo(timeInRoad);
	}

	private String getWaitingTimeInfo(Route route) {
		LocalTime time = LocalTime.MAX.plus(route.getWaitingTimeInSec(), ChronoUnit.SECONDS);
		return "Время ожидания: " + getCorrectTimeInfo(time);
	}

	private String getFinishTimeInfo(Route route) {
		LocalTime time = route.getFinishTime();
		int hour = time.getHour();
		int minutes = time.getMinute();
		int seconds = time.getSecond();
		if (seconds >= 30) {
			minutes++;
		}
		return "Работа закончена в " + hour + ":" + minutes + ". ";
	}

	private String getFailedDeliveryInfo(Route solution) {
		StringBuilder sb = new StringBuilder();
		for (int i = solution.getCountOfSuccessDelivery(); i < solution.getRoute().size(); i++) {
			sb.append("[").append(solution.getRoute().get(i).getId()).append("]");
			if (i < solution.getRoute().size() - 1) {
				sb.append(", ");
			}
		}
		return "Не выполнено: " + sb.toString();
	}

	private String getCorrectTimeInfo(LocalTime time) {
		int hours = time.getHour();
		int minutes = time.getMinute();
		int seconds = time.getSecond();
		if (seconds >= 30) {
			minutes++;
		}
		String hoursWord = "час";
		if (hours > 1 && hours < 5) {
			hoursWord = "часа";
		}
		if (hours >= 5) {
			hoursWord = "часов";
		}
		return hours + " " + hoursWord + " " + minutes + " минут. ";
	}
}
