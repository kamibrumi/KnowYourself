import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;

public class RandomData {
	public static void main(String[] args) {
		for (int i = 1; i < 50; ++i) {
			if (i == 7) System.out.print(7);
			else System.out.print(i%7);

			int gb = ThreadLocalRandom.current().nextInt(0, 100);
			int location = ThreadLocalRandom.current().nextInt(0, 50);

			Double nightAverageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightStdDevTemp = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double nightAveragePressure = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightStdDevPressure = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double nightAverageHumidity = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightStdDevHumidity = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double nightAverageClouds = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightStdDevClouds = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double nightAverageWind = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightStdDevWind = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayAverageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayStdDevTemp = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayAveragePressure = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayStdDevPressure = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayAverageHumidity = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayStdDevHumidity = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayAverageClouds = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayStdDevClouds = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayAverageWind = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayStdDevWind = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double nightTomorrowAverageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightTomorrowStdDevTemp = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double nightTomorrowAveragePressure = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightTomorrowStdDevPressure = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double nightTomorrowAverageHumidity = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightTomorrowStdDevHumidity = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double nightTomorrowAverageClouds = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightTomorrowStdDevClouds = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double nightTomorrowAverageWind = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightTomorrowStdDevWind = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayTomorrowAverageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayTomorrowStdDevTemp = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayTomorrowAveragePressure = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayTomorrowStdDevPressure = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayTomorrowAverageHumidity = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayTomorrowStdDevHumidity = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayTomorrowAverageClouds = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayTomorrowStdDevClouds = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayTomorrowAverageWind = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayTomorrowStdDevWind = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			System.out.println(" " + gb + " " + location
						    + " " + nightAverageTemp + " " + nightStdDevTemp
						    + " " + nightAveragePressure + " " + nightStdDevPressure
						    + " " + nightAverageHumidity + " " + nightStdDevHumidity
						    + " " + nightAverageClouds + " " + nightStdDevClouds
						    + " " + nightAverageWind + " " + nightStdDevWind
						    + " " + dayAverageTemp + " " + dayStdDevTemp
						    + " " + dayAveragePressure + " " + dayStdDevPressure
						    + " " + dayAverageHumidity + " " + dayStdDevHumidity
						    + " " + dayAverageClouds + " " + dayStdDevClouds
						    + " " + dayAverageWind + " " + dayStdDevWind
						    + " " + nightTomorrowAverageTemp + " " + nightTomorrowStdDevTemp
						    + " " + nightTomorrowAveragePressure + " " + nightTomorrowStdDevPressure
						    + " " + nightTomorrowAverageHumidity + " " + nightTomorrowStdDevHumidity
						    + " " + nightTomorrowAverageClouds + " " + nightTomorrowStdDevClouds
						    + " " + nightTomorrowAverageWind + " " + nightTomorrowStdDevWind
						    + " " + dayTomorrowAverageTemp + " " + dayTomorrowStdDevTemp
						    + " " + dayTomorrowAveragePressure + " " + dayTomorrowStdDevPressure
						    + " " + dayTomorrowAverageHumidity + " " + dayTomorrowStdDevHumidity
						    + " " + dayTomorrowAverageClouds + " " + dayTomorrowStdDevClouds
						    + " " + dayTomorrowAverageWind + " " + dayTomorrowStdDevWind);
		}
	}
}
