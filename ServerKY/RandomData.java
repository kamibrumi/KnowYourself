import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Math;
import java.util.concurrent.ThreadLocalRandom;

public class RandomData {
	public static void main(String[] args) {
		for (int i = 1; i < 500; ++i) {
			if (i == 7) System.out.print(7);
			else System.out.print(i%7);

			int gbBite = ThreadLocalRandom.current().nextInt(0, 1000);

			Double nightAverageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightStdDev = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayAverageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayStdDev = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double nightTomorrowAverageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double nightTomorrowStdDev = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			Double dayTomorrowAverageTemp = ThreadLocalRandom.current().nextDouble(10, 30 + 1);
			Double dayTomorrowStdDev = ThreadLocalRandom.current().nextDouble(0.5, 5. + 1);

			String gb = "good";
			if ((gbBite%2) == 0) gb = "bad";

			System.out.println(" " + gb + " " + nightAverageTemp + " " + nightStdDev + " " + dayAverageTemp + " " + 							dayStdDev + " " + nightTomorrowAverageTemp + " " + nightTomorrowStdDev + " " + 							dayTomorrowAverageTemp + " " + dayTomorrowStdDev);
		}
	}
}
