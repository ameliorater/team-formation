import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Graph {
    HashMap<Integer, List<Edge>> adjacencyLists = new HashMap<>();

    public Graph () { }

    //copy constructor
    public Graph (Graph graph) {
        this.adjacencyLists = new HashMap<>(graph.adjacencyLists);
    }

    public void addNode(int node) {
        //if (adjacencyLists.size() == 0) root = node; //if first node added, make root
        adjacencyLists.put(node, new ArrayList<>());
    }

    //will also add nodes if they don't exist
    //if directed, edge will be from first node to second node
    public void addEdge(int first, int second, double weight, boolean directed) {
        if (adjacencyLists.containsKey(first) && adjacencyLists.containsKey(second)) {
            List<Edge> firstList = adjacencyLists.get(first);
            firstList.add(new Edge(second, weight));
            adjacencyLists.put(first, firstList);

            if (!directed) {
                List<Edge> secondList = adjacencyLists.get(second);
                secondList.add(new Edge(first, weight));
                adjacencyLists.put(second, secondList);
            }
        } else {
            if (!adjacencyLists.containsKey(first)) addNode(first);
            if (!adjacencyLists.containsKey(second)) addNode(second);
            addEdge(first, second, weight, directed);
        }
    }

    public boolean hasEdge(int first, int second) {
        if (!adjacencyLists.containsKey(first)) return false; //todo: why was this needed
        return adjacencyLists.get(first).stream().map(Edge::getDestination).anyMatch(n -> n == second);
    }

    //returns zero for nonexistent edge
    public double getEdgeWeight (int first, int second) {
        if (!hasEdge(first, second)) return 0;
        return adjacencyLists.get(first).stream().filter(e -> e.destination == second).collect(Collectors.toList()).get(0).weight;
    }

    public boolean hasPath (int first, int second) {
        //use bfs
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        HashMap<Integer, Boolean> visited = new HashMap<>();

        queue.add(first);
        visited.put(first, true);
        while (queue.size() > 0) {
            int current = queue.poll();
            for (int i = 0; i < adjacencyLists.get(current).size(); i++) {
                //add all child nodes to the queue
                int next = adjacencyLists.get(current).get(i).destination;
                if (!visited.containsKey(next)) visited.put(next, false);
                if (!visited.get(next)) queue.add(next);
                visited.put(next, true);
            }
        }
        return visited.containsKey(second); //did we visit the second node?
    }

    public int numNodesUnreachable (int root) {
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        HashMap<Integer, Boolean> visited = new HashMap<>();

        queue.add(root);
        visited.put(root, true);
        while (queue.size() > 0) {
            int current = queue.poll();
            for (int i = 0; i < adjacencyLists.get(current).size(); i++) {
                //add all child nodes to the queue
                int next = adjacencyLists.get(current).get(i).destination;
                if (!visited.containsKey(next)) visited.put(next, false);
                if (!visited.get(next)) queue.add(next);
                visited.put(next, true);
            }
        }
        return adjacencyLists.size() - visited.size(); //returns 0 is graph is completely connected
    }

    @Override
    public String toString() {
        return adjacencyLists.toString();
    }
}

class Edge {
    int destination;
    double weight;

    public Edge (int destination, double weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public int getDestination () { return destination; }

    @Override
    public String toString() {
        return "" + destination + ": " + weight;
    }
}