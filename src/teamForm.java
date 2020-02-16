import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class teamForm {
    public static Graph prefDAG;
    public static Graph mergedGraph;
    public static double k = 0.0001;
    public static double timeout = 20000; //max runtime (in millis)

    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);
        BufferedReader csvReader = new BufferedReader(new FileReader("test-team-form-data.csv"));
        String row;
        String[] names = csvReader.readLine().split(",");
        prefDAG = new Graph();
        int n = names.length - 1;

        //testing
        //System.out.println(formatGroups(Arrays.asList(Arrays.copyOfRange(names, 1, names.length)), 4));

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

        mergedGraph = new Graph();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.println("i: " + i + " j: " + j + " weight: " + getMeanEdgeWeight(i, j, prefDAG, k));
                mergedGraph.addEdge(i, j, getMeanEdgeWeight(i, j, prefDAG, k), false);
            }
        }

        System.out.println(mergedGraph);

        int permsTested = 0;
        double temp = 20;
        double coolingRate = 0.00008;

        List<Integer> bestOrder = getRandomIndexList(n);
        List<Integer> currentOrder = new ArrayList<>(bestOrder);
        double bestMean = 0;

        long startTime = System.currentTimeMillis();
        while (temp > 0.0001 && System.currentTimeMillis() - startTime < timeout) {
            List<Integer> newOrder = new ArrayList<>(currentOrder);
            int pos1 = (int) (Math.random() * n);
            int pos2 = (int) (Math.random() * n);

            //swap positions
            newOrder.set(pos1, currentOrder.get(pos2));
            newOrder.set(pos2, currentOrder.get(pos1));

            double currentMean = getOrderMean(currentOrder);
            double newMean = getOrderMean(newOrder);

            if (acceptanceProbability(currentMean, newMean, temp) > Math.random()) {
                currentOrder = new ArrayList<>(newOrder);
                currentMean = newMean;
            }

            temp *= 1 - coolingRate;
            if (permsTested % 10000 == 0) System.out.println(temp);

            if (currentMean > bestMean) {
                bestOrder = new ArrayList<>(currentOrder);
                bestMean = currentMean;
                System.out.println("best mean: " + currentMean);
                System.out.println("best order: " + bestOrder);
            }
            permsTested++;
        }

        List<String> bestSortNames = bestOrder.stream().map(i -> names[i + 1]).collect(Collectors.toList());
        System.out.println(formatGroups(bestSortNames, 4));
        System.out.println("perms tested: " + permsTested);
        System.out.println("runtime (s): " + (System.currentTimeMillis() - startTime)/1000);
    }

    public static double getOrderMean (List<Integer> order) {
        //order parameter represents the groups (first four in first group, etc.)
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
                    pairMeans.add(getMeanEdgeWeight(order.get(i + p * nodesInP), order.get(j + p * nodesInP), mergedGraph, k));
                    //System.out.println(getMeanEdgeWeight(randomIndexList.get(i + p*nodesInP), randomIndexList.get(j + p*nodesInP), mergedGraph, k));
                }
            }
            groupMeans.add(getMean(pairMeans, k));
            //System.out.println(getMean(pairMeans, k));
        }
        return getMean(groupMeans, k);
    }

    public static double acceptanceProbability (double currentMean, double newMean, double temp) {
        if (newMean > currentMean) return 1.0; //better, accept automatically
        return Math.exp((newMean - currentMean) / temp);
    }

    public static double getMeanEdgeWeight(int firstNode, int secondNode, Graph graph, double k) {
//        System.out.println("first edge weight: " + graph.getEdgeWeight(firstNode, secondNode));
//        System.out.println("second edge weight: " + graph.getEdgeWeight(secondNode, firstNode));

        return getMean(Arrays.asList(graph.getEdgeWeight(firstNode, secondNode), graph.getEdgeWeight(secondNode, firstNode)), k);
    }

    //k determines the type of mean: 1 is arithmetic, 0 is geometric, -1 is harmonic or something
    public static double getMean(List<Double> numbers, double k) {
        double numerator = 0;
        for (double num : numbers) {
            numerator += Math.pow(num, k);
        }
        //return (int)(Math.pow(numerator/numbers.size(), 1/k) * 1000)/1000.0;
        return Math.pow(numerator / numbers.size(), 1 / k);
    }

    public static List<Integer> getRandomIndexList(int length) {
        List<Integer> range = IntStream.rangeClosed(0, length - 1).boxed().collect(Collectors.toList());
        Collections.shuffle(range, new Random());
        return range;
    }

    //alphabetizes for easier comparison
    public static List<List<String>> formatGroups (List<String> names, int numGroups) {
        int groupSize = names.size() / numGroups;
        List<List<String>> groups = new ArrayList<>();
        List<String> currentGroup = new ArrayList<>();
        for (int i = 0; i <= names.size() - groupSize; i += groupSize) { //starting index for group
            for (int j = 0; j < groupSize; j++) {
                currentGroup.add(names.get(i + j));
            }
            groups.add(currentGroup);
            currentGroup = new ArrayList<>();
        }
        for (List<String> group : groups) {
            Collections.sort(group);
        }
        groups.sort(Comparator.comparing(l -> l.get(0)));
        return groups;
    }
}
