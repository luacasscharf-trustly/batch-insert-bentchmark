package activejdbc.examples.simple;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TimeController {
	private final Integer counter;
	private final String testType;
	private final Map<String, Duration> map;
	private String currentTest;
	private LocalDateTime currentTime;

	public TimeController(Integer counter, String testType) {
		this.counter = counter;
		this.testType = testType;
		map = new HashMap<>();
	}

	public void startTest(String testName) {
		currentTest = testName;
		currentTime = LocalDateTime.now();
	}

	public void endTest() {
		LocalDateTime endTime = LocalDateTime.now();
		Duration currentDuration = Duration.between(currentTime, endTime);
		map.put(currentTest, currentDuration);

		currentTest = "";
		currentTime = null;
	}

	public void printTimers() {
		System.out.println("------------------------");
		System.out.println("Test type: " + testType);
		System.out.println("Insert count: " + counter);
		System.out.println();
		map.entrySet()//
				.stream()//
				.sorted((a,b)-> a.getKey().compareTo(b.getKey()))//
				.forEach(
						pair -> {
							System.out.println(pair.getKey() + " -> " + pair.getValue().toMillis() + "ms");
						});
		System.out.println("------------------------");
	}
}
