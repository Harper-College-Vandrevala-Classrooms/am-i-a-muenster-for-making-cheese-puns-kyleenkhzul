package com.csc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class CheeseAnalyzer {
    String fileName;
    int number_of_pasteurized_milk = 0,
        number_of_raw_milk = 0,
        number_of_organic_cheese_moisture_greater_than_forty_percent = 0,
        number_of_cheese_from_cow = 0,
        number_of_cheese_from_goat = 0,
        number_of_cheese_from_ewe = 0,
        number_of_cheese_from_buffalo = 0;

    public void analyzeCheeseData(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");
                String milkTreatmentType = data[9];
                double moisturePercent = Double.parseDouble(data[3]);
                int organic = Integer.parseInt(data[6]);
                String animal = data[8].toLowerCase();

                if (milkTreatmentType.equals("Raw Milk")) {
                    number_of_raw_milk++;
                } else {
                    number_of_pasteurized_milk++;
                }

                if (moisturePercent > 41.0 && organic == 1) {
                    number_of_organic_cheese_moisture_greater_than_forty_percent++;
                }

                switch (animal) {
                    case "cow":
                        number_of_cheese_from_cow++;
                        break;
                    case "goat":
                        number_of_cheese_from_goat++;
                        break;
                    case "ewe":
                        number_of_cheese_from_ewe++;
                        break;
                    case "buffalo":
                        number_of_cheese_from_buffalo++;
                        break;
                    default:
                        System.err.println("Unknown animal type: " + animal);
                        break;
                }
            }
            reader.close();
        } catch (FileNotFoundException err) {
            err.printStackTrace();
        } catch (IOException | NumberFormatException d) {
            d.printStackTrace();
        }
    }

    public void writeAnalysisResult() {
        try {
            String mostAnimal = compareMostCheesesFromAnAnimal(
                number_of_cheese_from_cow,
                number_of_cheese_from_goat,
                number_of_cheese_from_ewe,
                number_of_cheese_from_buffalo
            );

            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write("The number of cheeses that use pasteurized milk is: " + number_of_pasteurized_milk);
            writer.write("\nThe number of cheeses that use raw milk is: " + number_of_raw_milk);
            writer.write("\nThe number of organic cheeses that have a moisture percentage greater than 41.0% is: " + number_of_organic_cheese_moisture_greater_than_forty_percent);
            writer.write("\nThe animal that most cheeses in Canada comes from is: " + mostAnimal);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String compareMostCheesesFromAnAnimal(int cow, int goat, int ewe, int buffalo) {
        if (cow > goat && cow > ewe && cow > buffalo) {
            return "Cows";
        } else if (goat > cow && goat > ewe && goat > buffalo) {
            return "Goats";
        } else if (ewe > cow && ewe > goat && ewe > buffalo) {
            return "Ewes";
        } else {
            return "Buffalos";
        }
    }

    public static void main(String[] args) {
        CheeseAnalyzer analyzer = new CheeseAnalyzer();
        analyzer.analyzeCheeseData("cheese_data.csv");
        analyzer.writeAnalysisResult();
    }
}
