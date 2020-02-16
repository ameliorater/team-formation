import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class teamForm {
    public static Graph prefDAG;
    public static double k = 0.0001;

    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);
        BufferedReader csvReader = new BufferedReader(new FileReader("test-team-form-data.csv"));
        String row;
        String[] names = csvReader.readLine().split(",");
        prefDAG = new Graph();
        int n = names.length - 1;

        int fromIndex = 0;
        while ((row = csvReader.readLine()) != null) {
            String[] ratings = row.split(",");
            System.out.println(ratings.length);
            for (int i = 1; i < ratings.length; i++) {
                System.out.print(Double.parseDouble(ratings[i]) + " ");
                prefDAG.addEdge(fromIndex, i - 1, Double.parseDouble(ratings[i]), true);
            }
            fromIndex++;
            System.out.println();
        }
        csvReader.close();

        System.out.println(prefDAG);

        Graph mergedGraph = new Graph();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.println("i: " + i + " j: " + j + " weight: " + getMeanEdgeWeight(i, j, prefDAG, k));
                mergedGraph.addEdge(i, j, getMeanEdgeWeight(i, j, prefDAG, k), false);
            }
        }

        System.out.println(mergedGraph);

        double bestSortMean = 0;
        List<Integer> bestSort = new ArrayList<>();
        int permsTested = 0;
        while (true) {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 10000) {
                //represents the groups (first four in first group, etc.)
                List<Integer> randomIndexList = getRandomIndexList(n);
                //System.out.println(randomIndexList);
                int nodesInP = 4; //4 nodes per partition (people per team)
                int numPartitions = 4;
                List<Double> groupMeans = new ArrayList<>();
                for (int p = 0; p < numPartitions; p++) {
                    //find the fancy mean of these nodesInP nodes
                    List<Double> pairMeans = new ArrayList<>();
                    for (int i = 0; i < nodesInP; i++) { //todo: make not count all twice
                        for (int j = 0; j < nodesInP; j++) {
                            if (i == j) continue;
                            //System.out.println("i + p*nodesInP: " + (i + p*nodesInP) + " j + p*nodesInP: " + (j + p*nodesInP));
                            pairMeans.add(getMeanEdgeWeight(randomIndexList.get(i + p * nodesInP), randomIndexList.get(j + p * nodesInP), mergedGraph, k));
                            //System.out.println(getMeanEdgeWeight(randomIndexList.get(i + p*nodesInP), randomIndexList.get(j + p*nodesInP), mergedGraph, k));
                        }
                    }
                    groupMeans.add(getMean(pairMeans, k));
                    //System.out.println(getMean(pairMeans, k));
                }
                double sortOverallMean = getMean(groupMeans, k);
                if (sortOverallMean > bestSortMean) {
                    bestSortMean = sortOverallMean;
                    bestSort = randomIndexList;
                    System.out.println("best mean: " + bestSortMean);
                    System.out.println("best sort: " + bestSort);
                }
                permsTested++;
            }

            System.out.println("Continue? (yes/no)");
            while (!userInput.hasNext()) { }
            if (userInput.next().equals("yes")) continue;
            else break;
        }

        List<String> bestSortNames = bestSort.stream().map(i -> names[i + 1]).collect(Collectors.toList());
        System.out.println(bestSortNames);
        System.out.println("perms tested: " + permsTested);

    }

    public static double getMeanEdgeWeight (int firstNode, int secondNode, Graph graph, double k) {
//        System.out.println("first edge weight: " + graph.getEdgeWeight(firstNode, secondNode));
//        System.out.println("second edge weight: " + graph.getEdgeWeight(secondNode, firstNode));

        return getMean(Arrays.asList(graph.getEdgeWeight(firstNode, secondNode), graph.getEdgeWeight(secondNode, firstNode)), k);
    }

    //k determines the type of mean: 1 is arithmetic, 0 is geometric, -1 is harmonic or something
    public static double getMean (List<Double> numbers, double k) {
        double numerator = 0;
        for (double num: numbers) {
            numerator += Math.pow(num, k);
        }
        //return (int)(Math.pow(numerator/numbers.size(), 1/k) * 1000)/1000.0;
        return Math.pow(numerator/numbers.size(), 1/k);
    }

    public static List<Integer> getRandomIndexList (int length) {
        List<Integer> range = IntStream.rangeClosed(0, length - 1).boxed().collect(Collectors.toList());
        Collections.shuffle(range, new Random());
        return range;
    }
}
