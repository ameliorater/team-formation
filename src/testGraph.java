public class testGraph {
    public static void main(String[] args) {
        Graph graph = new Graph();

//        Node node1 = new Node(1);
//        Node node2 = new Node(2);
//        Node node3 = new Node(3);
//        Node node4 = new Node(4);
//
//        graph.addNode(node1);
//        graph.addNode(node2);
//
//        System.out.println(graph.hasEdge(node1, node2));
//        System.out.println(graph.getEdgeWeight(node1, node2));
//
//        graph.addEdge(node1, node2, 0.3, true);
//        graph.addEdge(node3, node4, 0.5, true);
//
//        System.out.println(graph.hasEdge(node1, node2));
//        System.out.println(graph.getEdgeWeight(node1, node2));
//
//        System.out.println(graph.hasPath(node1, node4));
//
//        graph.addEdge(node2, node4, 0.7, true);
//        System.out.println(graph.hasPath(node1, node4));
//        System.out.println(graph.numNodesUnreachable(node4));

        graph.addEdge(1, 1, 0.3, true);
        graph.addEdge(1, 2, 0.5, true);

        System.out.println(graph);
    }
}
