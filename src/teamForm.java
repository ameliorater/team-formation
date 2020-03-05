import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class teamForm {
    public static Graph prefDAG;
    public static Graph mergedGraph;

    public static double k1 = -0.75; //for person <-> person mean (directed to undirected graph)
    public static double k2 = 1e-6; //for group means (all the people within a proposed group)
    public static double k3 = 1e-6; //for group <-> group <-> group ... mean

    public static double timeout = 20000; //max runtime (in millis)

    public static void main(String[] args) throws Exception {
        BufferedReader csvReader = new BufferedReader(new FileReader("test-team-form-data.csv"));
        String row;
        String[] namesInput = csvReader.readLine().split(",");
        String[] names = Arrays.copyOfRange(namesInput,1, namesInput.length); //remove leading comma
        prefDAG = new Graph();
        int n = names.length;

        int fromIndex = 0;
        while ((row = csvReader.readLine()) != null) {
            String[] ratings = row.split(",");
            for (int i = 1; i < ratings.length; i++) {
                prefDAG.addEdge(fromIndex, i - 1, Double.parseDouble(ratings[i]), true);
            }
            fromIndex++;
        }
        csvReader.close();

        mergedGraph = new Graph();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mergedGraph.addEdge(i, j, getMeanEdgeWeight(i, j, prefDAG, k1), false);
            }
        }

        //todo: make a formula to take desired runtime (perms to test) and get cooling parameters
        int permsTested = 0;
        double temp = 1.58; //was 20 //was 1.58 //was 5 || //was 27
        double coolingRate = 0.000174; //was 0.00008 //was 0.000174 //was 0.000457 || //was 0.00126

        List<Integer> bestOrder = getRandomIndexList(n);
        List<Integer> currentOrder = new ArrayList<>(bestOrder);
        double bestMean = 0;

        long startTime = System.currentTimeMillis();
        while (temp > 0.05 && System.currentTimeMillis() - startTime < timeout) {
            List<Integer> newOrder = new ArrayList<>(currentOrder);

            //todo: guarantee group swap
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
            //if (permsTested % 10000 == 0) System.out.println(temp);

            if (currentMean > bestMean) {
                bestOrder = new ArrayList<>(currentOrder);
                bestMean = currentMean;
                //System.out.println("best mean: " + currentMean);
                //System.out.println("best order: " + bestOrder);
            }
            permsTested++;
        }

        List<String> bestSortNames = bestOrder.stream().map(i -> names[i]).collect(Collectors.toList());
        System.out.println("best grouping: " + formatGroups(bestSortNames, 4));
        System.out.println("best mean: " + bestMean);
        System.out.println("perms tested: " + permsTested);
        System.out.println("runtime (ms): " + (System.currentTimeMillis() - startTime));
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
                    pairMeans.add(mergedGraph.getEdgeWeight(order.get(i + p * nodesInP), order.get(j + p * nodesInP)));
                }
            }
            groupMeans.add(getMean(pairMeans, k2));
        }
        return getMean(groupMeans, k3);
    }

    public static double acceptanceProbability (double currentMean, double newMean, double temp) {
        if (newMean > currentMean) return 1.0; //better, accept automatically
        return Math.exp((newMean - currentMean) / temp);
    }

    //uses bowl function to weight values
    public static double getMeanEdgeWeight(int firstNode, int secondNode, Graph graph, double k) {
        double firstRating = graph.getEdgeWeight(firstNode, secondNode);
        double secondRating = graph.getEdgeWeight(secondNode, firstNode);
        return getMean(Arrays.asList(firstRating * bowl(firstRating), secondRating * bowl(secondRating)), k);
    }

//    public static double getMeanEdgeWeight(int firstNode, int secondNode, Graph graph, double k) {
//        return getMean(Arrays.asList(graph.getEdgeWeight(firstNode, secondNode), graph.getEdgeWeight(secondNode, firstNode)), k);
//    }

    //k determines the type of mean: 1 is arithmetic, 0 is geometric, -1 is harmonic or something
    public static double getMean(List<Double> numbers, double k) {
        double numerator = 0;
        for (double num : numbers) {
            numerator += Math.pow(num, k);
        }
        return Math.pow(numerator / numbers.size(), 1 / k);
    }

    //steven's fancy bowl function (weights mean function for pairwise merging)
    public static double bowl (double x) {
        double wa = 0.5;
        double w1 = 1.25;
        double w2 = 1.0;

        return ((wa/(w1 + w2)) * (
                (w1/(1.0 - 4.0/49.0)) * (1.0 - (1.0 / (0.4* Math.pow(x - 5.0, 2.0) + 1.0))) +
                        w2*(Math.sqrt(((3.0/25.0)* Math.pow(x - 5.0, 2.0) + 1.0)) - 1.0)
        ) + 1.0);
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