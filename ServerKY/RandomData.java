import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ThreadLocalRandom;

public class RandomData {
	public static void main(String[] args) {
		//training strips (50 of them)
		for (int i = 1; i < 50; ++i) { //now we are iterating over the strips
			int stripId = i%8;
			int day = 1 + i/8;
			int durationInStrips = ThreadLocalRandom.current().nextInt(1, 3);
			int gb = ThreadLocalRandom.current().nextInt(0, 100);
			int location = ThreadLocalRandom.current().nextInt(0, 50);


			Double averageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double stdDevTemp = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double averagePressure = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double stdDevPressure = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double averageHumidity = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double stdDevHumidity = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double averageClouds = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double stdDevClouds = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double averageWind = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double stdDevWind = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			try{
				 PrintWriter writer = new PrintWriter("sampleForModel.txt");//, "UTF-8"
				 writer.println(day + " " + gb + " " + location
						    + " " + averageTemp + " " + stdDevTemp
						    + " " + averagePressure + " " + stdDevPressure
						    + " " + averageHumidity + " " + stdDevHumidity
						    + " " + averageClouds + " " + stdDevClouds
						    + " " + averageWind + " " + stdDevWind + " " + stripId + " " + durationInStrips);
				 writer.close();
			} catch (IOException e) {
				System.out.println("FATAL: Raised exception while trying to write to sampleForModel.txt");// do something
			}

		}
		//predicted strips(24 of them)
		for (int i = 0; i < 24; ++i) {
			int stripId = i%8;
			int day = 1 + i/8;
			int durationInStrips = ThreadLocalRandom.current().nextInt(1, 3);
			int location = ThreadLocalRandom.current().nextInt(0, 50);

			Double tomorrowAverageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double tomorrowStdDevTemp = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double tomorrowAveragePressure = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double tomorrowStdDevPressure = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double tomorrowAverageHumidity = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double tomorrowStdDevHumidity = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double tomorrowAverageClouds = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double tomorrowStdDevClouds = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double tomorrowAverageWind = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double tomorrowStdDevWind = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);


			try{
				 PrintWriter writer = new PrintWriter("sampleForPrediction.txt");//, "UTF-8"
				 writer.println(day + " " + gb + " " + location
						    + " " + averageTemp + " " + stdDevTemp
						    + " " + averagePressure + " " + stdDevPressure
						    + " " + averageHumidity + " " + stdDevHumidity
						    + " " + averageClouds + " " + stdDevClouds
						    + " " + averageWind + " " + stdDevWind + " " + stripId + " " + durationInStrips);
				 writer.close();
			} catch (IOException e) {
				System.out.println("FATAL: Raised exception while trying to write to sampleForPrediction.txt");// do something
			}


			System.out.println(day + " " + location + tomorrowAverageTemp + " " + tomorrowStdDevTemp
						    + " " + tomorrowAveragePressure + " " + tomorrowStdDevPressure
						    + " " + tomorrowAverageHumidity + " " + tomorrowStdDevHumidity
						    + " " + tomorrowAverageClouds + " " + tomorrowStdDevClouds
						    + " " + tomorrowAverageWind + " " + tomorrowStdDevWind + " "
                      + " " + stripId + " " + durationInStrips);
	}
}
