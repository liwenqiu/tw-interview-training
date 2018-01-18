package me.liwenqiu.tw.trains;

import me.liwenqiu.tw.trains.domain.RouteMap;
import me.liwenqiu.tw.trains.exception.TrainRuntimeException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liwenqiu@gmail.com
 */
public class Application {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Error: no input file");
            System.exit(-1);
        }


        Application app = new Application();
        app.outputAnswerOfHardCodeQuestions(app.constructMap(args[0]));
    }


    protected RouteMap constructMap(String inputFile) throws IOException {
        BufferedReader reader = null;
        try {
            // Load input file and parse to {AB1, AC2, BD3} format
            reader = new BufferedReader(new FileReader(new File(inputFile)));
            String line;
            List<String> elements = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                String[] token = line.split(",");
                for (String t : token) {
                    elements.add(t); // AB5
                }
            }
            // Construct route map
            return RouteMap.build(elements);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    protected void outputAnswerOfHardCodeQuestions(RouteMap map) {
        for (int i = 1; i <= 10; i++) {
            try {
                switch (i) {
                    case 1:
                        print(i, map.calculateDistanceOfTowns("A", "B", "C"));
                        break;
                    case 2:
                        print(i, map.calculateDistanceOfTowns("A", "D"));
                        break;
                    case 3:
                        print(i, map.calculateDistanceOfTowns("A", "D", "C"));
                        break;
                    case 4:
                        print(i, map.calculateDistanceOfTowns("A", "E", "B", "C", "D"));
                        break;
                    case 5:
                        print(i, map.calculateDistanceOfTowns("A", "E", "D"));
                        break;
                    case 6:
                        print(i, map.findNumberOfPathsToTownWithMaxStopLimit("C", "C", 3));
                        break;
                    case 7:
                        print(i, map.findNumberOfPathsToTownAtSpecifiedStop("A", "C", 4));
                        break;
                    case 8:
                        print(i, map.findShortestPathBetweenTowns("A", "C"));
                        break;
                    case 9:
                        print(i, map.findShortestPathBetweenTowns("B", "B"));
                        break;
                    case 10:
                        print(i, map.findNumberOfPathBetweenTownsWithMaxDistanceLimit("C", "C", 30));
                        break;
                }
            } catch (TrainRuntimeException e) {
                System.err.println("#" + i + ": " + e.getMessage());
            }
        }
    }

    protected void print(int id, int result) {
        System.out.println("#" + id + ": " + result);
    }

}
