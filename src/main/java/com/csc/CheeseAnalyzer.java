package com.csc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheeseAnalyzer {
    String fileName; 

    // counters for data
    int number_of_pasteurized_milk = 0,
        number_of_raw_milk = 0,
        number_of_organic_cheese_moisture_greater_than_forty_percent = 0,
        number_of_cheese_from_cow = 0,
        number_of_cheese_from_goat = 0,
        number_of_cheese_from_ewe = 0,
        number_of_cheese_from_buffalo = 0;

    /*
     * This method takes each line of cheese data, processes it, and then analyzes it. It utilizes helper functions
     * removeCommasInQuotes(), and fillEmptyFields(). From there, it takes relevant data from each line of code
     * and stores it into various counter variables.
     * @param fileName, String, file to be analyzed
     */
    public void analyzeCheeseData(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine(); // skip header

            // while-loop for reading the text file
            while ((line = reader.readLine()) != null) {

                // removes the commas inside quotes of a line
                String formattedLine = removeCommasInQuotes(line);

                // splits each data column by commas and stores it into a String array
                String[] data = formattedLine.split(",");

                // fills any empty data values with "NULL"
                fillEmptyFields(data);

                // assigns milkTreatmentType from column 8
                String milkTreatmentType = data[9].toLowerCase();

                // assigns type of animal from column 7
                String animal = data[8].toLowerCase();

                // checks if able to parse moisture % into double
                double moisturePercent;
                if(data[3].equals("NULL")) {
                    moisturePercent = 0.0;
                } else {
                    moisturePercent = Double.parseDouble(data[3]);
                }

                // checks if able to parse organic # into int
                int organic;
                if(data[6].equals("NULL")) {
                    organic = -1;
                } else {
                    organic = Integer.parseInt(data[6]);
                }

                // counts whether treatment type is from raw milk or pasteurized
                if (milkTreatmentType.equals("raw milk")) {
                    number_of_raw_milk++;
                } else if(milkTreatmentType.equals("pasteurized")) {
                    number_of_pasteurized_milk++;
                } else {
                    continue; // if neither, just continue
                }

                // checks for if the cheese is organic AND moisture % is greater than 41.0
                if (moisturePercent > 41.0 && organic == 1) {
                    number_of_organic_cheese_moisture_greater_than_forty_percent++;
                }

                // switch case for counter variables for what animal the cheese comes from
                // accounts for special cases present in the CSV file
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
                    case "cow and goat":
                        number_of_cheese_from_cow++;
                        number_of_cheese_from_goat++;
                        break;
                    case "ewe and cow":
                        number_of_cheese_from_ewe++;
                        number_of_cheese_from_goat++;
                        break;
                    case "ewe and goat": 
                        number_of_cheese_from_ewe++;
                        number_of_cheese_from_goat++;
                        break;
                    case "buffalo cow":
                        number_of_cheese_from_buffalo++;
                        number_of_cheese_from_cow++;
                        break;
                    case "\"cow goat and ewe\"":
                        number_of_cheese_from_buffalo++;
                        number_of_cheese_from_goat++;
                        number_of_cheese_from_ewe++;
                        break;
                    default:
                        System.err.println("Unknown animal type: " + animal);
                        System.out.println(data[0]);
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

    /*
     * This function writes the results of the analysis to an output.txt file. It utilizes a helper function compareMostCheesesFromAnAnimal();
     */
    public void writeAnalysisResult() {
        try {

            // returns which animal has the most products
            String mostAnimal = compareMostCheesesFromAnAnimal(
                number_of_cheese_from_cow,
                number_of_cheese_from_goat,
                number_of_cheese_from_ewe,
                number_of_cheese_from_buffalo
            );

            // writing to output.txt
            BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write("The number of cheeses that use pasteurized milk is: " + number_of_pasteurized_milk);
            writer.write("\nThe number of cheeses that use raw milk is: " + number_of_raw_milk);
            writer.write("\nThe number of organic cheeses that have a moisture percentage greater than 41.0% is: " + number_of_organic_cheese_moisture_greater_than_forty_percent);
            writer.write("\nThe animal that most cheeses in Canada comes from are " + mostAnimal + " with " + number_of_cheese_from_cow + " total products.");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * This function compares the most cheeses from each animal and returns which animal has the greatest
     * amount of products
     * @param int, amount of cow cheese products
     * @param int, amount of goat cheese products
     * @param int, amount of ewe cheese products
     * @param int, amount of buffalo cheese products
     * @return Animal with highest # of products
     */
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

    /*
     * This helper function fills empty fields within a String array of data with NULL
     * @param data, String array to be analyzed
     */
    public void fillEmptyFields(String[] data) {
        for(int i = 0; i < data.length; i++) {
            if(data[i].trim().isEmpty()) {
                data[i] = "NULL";
            }
        }
    }

    /*
     * This helper function takes a String of line and removes any commas within quotes.
     * @param line, String to be processed
     * @return processed string without commas in quotes
     */
    public String removeCommasInQuotes(String line) {
        // Use regex to find quoted strings and replace commas inside them
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(line);
        
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // Replace commas in the quoted string
            String quotedString = matcher.group(1).replace(",", "");
            matcher.appendReplacement(sb, "\"" + quotedString + "\"");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void main(String[] args) {
        CheeseAnalyzer analyzer = new CheeseAnalyzer();
        analyzer.analyzeCheeseData("cheese_data.csv");
        analyzer.writeAnalysisResult();
    }
}
