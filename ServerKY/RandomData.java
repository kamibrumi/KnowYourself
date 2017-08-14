import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ThreadLocalRandom;
import java.io.PrintWriter;
import java.io.IOException;

public class RandomData {
	public static void main(String[] args) {
		//training strips (50 of them)
		PrintWriter writer = null;
		try{
			writer = new PrintWriter("sampleForModel.txt");//, "UTF-8"
		} catch (IOException e) {
			System.out.println("FATAL: Raised exception while trying to write to sampleForModel.txt");// do something
		}
		ArrayList<Integer> locations = new ArrayList<Integer>();
		for (int i = 0; i < 50; ++i) { //now we are iterating over the strips
			int stripId = i%8;
			int day = 1 + i/8;
			int durationInStrips = ThreadLocalRandom.current().nextInt(1, 3);
			int gb = ThreadLocalRandom.current().nextInt(0, 100);
			int location = ThreadLocalRandom.current().nextInt(0, 50);
			locations.add(location);

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

			writer.println(day + " " + gb + " " + location
					   + " " + averageTemp + " " + stdDevTemp
					   + " " + averagePressure + " " + stdDevPressure
					   + " " + averageHumidity + " " + stdDevHumidity
					   + " " + averageClouds + " " + stdDevClouds
					   + " " + averageWind + " " + stdDevWind + " " + stripId + " " + durationInStrips);

		}
		writer.close();
		try {
			writer = new PrintWriter("sampleForPrediction.txt");//, "UTF-8"
		} catch (IOException e) {
			System.out.println("FATAL: Raised exception while trying to write to sampleForPrediction.txt");// do something
		}
		//predicted strips(24 of them)
		for (int i = 0; i < 24; ++i) {
			int stripId = i%8;
			int day = 1 + i/8;
			int durationInStrips = ThreadLocalRandom.current().nextInt(1, 3);
			//now the locations are a subset of those generated in the training, otherwise we can
			//get errors: the locations ArrayList is being used
			//int location = ThreadLocalRandom.current().nextInt(0, 50);

			Double futureAverageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double futureStdDevTemp = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double futureAveragePressure = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double futureStdDevPressure = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double futureAverageHumidity = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double futureStdDevHumidity = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double futureAverageClouds = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double futureStdDevClouds = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double futureAverageWind = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double futureStdDevWind = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);


			writer.println(day + " " + locations.get(i) 
					   + " " + futureAverageTemp + " " + futureStdDevTemp
					   + " " + futureAveragePressure + " " + futureStdDevPressure
					   + " " + futureAverageHumidity + " " + futureStdDevHumidity
					   + " " + futureAverageClouds + " " + futureStdDevClouds
					   + " " + futureAverageWind + " " + futureStdDevWind + " "
                      			   + " " + stripId + " " + durationInStrips);
		}
		writer.close();
	}
}
