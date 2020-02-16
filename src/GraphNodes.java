//import java.util.*;
//import java.util.stream.Collectors;
//
//public class Graph {
//    HashMap<Node, List<Edge>> adjacencyLists = new HashMap<>();
//
//    public Graph () { }
//
//    //copy constructor
//    public Graph (Graph graph) {
//        this.adjacencyLists = new HashMap<>(graph.adjacencyLists);
//    }
//
//    public void addNode(Node node) {
//        //if (adjacencyLists.size() == 0) root = node; //if first node added, make root
//        adjacencyLists.put(node, new ArrayList<>());
//    }
//
//    //will also add nodes if they don't exist
//    //if directed, edge will be from first node to second node
//    public void addEdge(Node first, Node second, double weight, boolean directed) {
//        if (adjacencyLists.containsKey(first) && adjacencyLists.containsKey(second)) {
//            List<Edge> firstList = adjacencyLists.get(first);
//            firstList.add(new Edge(second, weight));
//            adjacencyLists.put(first, firstList);
//
//            if (!directed) {
//                List<Edge> secondList = adjacencyLists.get(second);
//                secondList.add(new Edge(first, weight));
//                adjacencyLists.put(second, secondList);
//            }
//        } else {
//            addNode(first);
//            addNode(second);
//            addEdge(first, second, weight, directed);
//        }
//    }
//
//    public boolean hasEdge(Node first, Node second) {
//        return adjacencyLists.get(first).stream().map(Edge::getDestination).anyMatch(second::equals);
//    }
//
//    //returns zero for nonexistent edge
//    public double getEdgeWeight (Node first, Node second) {
//        if (!hasEdge(first, second)) return 0;
//        return adjacencyLists.get(first).stream().filter(e -> e.destination.equals(second)).collect(Collectors.toList()).get(0).weight;
//    }
//
//    public boolean hasPath (Node first, Node second) {
//        //use bfs
//        ArrayDeque<Node> queue = new ArrayDeque<>();
//        HashMap<Node, Boolean> visited = new HashMap<>();
//
//        queue.add(first);
//        visited.put(first, true);
//        while (queue.size() > 0) {
//            Node current = queue.poll();
//            for (int i = 0; i < adjacencyLists.get(current).size(); i++) {
//                //add all child nodes to the queue
//                Node next = adjacencyLists.get(current).get(i).destination;
//                if (!visited.containsKey(next)) visited.put(next, false);
//                if (!visited.get(next)) queue.add(next);
//                visited.put(next, true);
//            }
//        }
//        return visited.containsKey(second); //did we visit the second node?
//    }
//
//    public int numNodesUnreachable (Node root) {
//        ArrayDeque<Node> queue = new ArrayDeque<>();
//        HashMap<Node, Boolean> visited = new HashMap<>();
//
//        queue.add(root);
//        visited.put(root, true);
//        while (queue.size() > 0) {
//            Node current = queue.poll();
//            for (int i = 0; i < adjacencyLists.get(current).size(); i++) {
//                //add all child nodes to the queue
//                Node next = adjacencyLists.get(current).get(i).destination;
//                if (!visited.containsKey(next)) visited.put(next, false);
//                if (!visited.get(next)) queue.add(next);
//                visited.put(next, true);
//            }
//        }
//        return adjacencyLists.size() - visited.size(); //returns 0 is graph is completely connected
//    }
//}
//
//class Node {
//    int property;
//
//    public Node (int property) {
//        this.property = property;
//    }
//
//    @Override
//    public String toString() {
//        return "" + property;
//    }
//}
//
//class Edge {
//    Node destination;
//    double weight;
//
//    public Edge (Node destination, double weight) {
//        this.destination = destination;
//        this.weight = weight;
//    }
//
//    public Node getDestination () { return destination; }
//}