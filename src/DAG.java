import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DAG {
    Double[][] adjacencyMat;
    int n;
    public DAG (int n) { //n is number of nodes
        this.n = n;
        adjacencyMat = new Double[n][n];
    }

    //copy constructor
    public DAG (DAG graph) {
        this.adjacencyMat = graph.adjacencyMat.clone();
    }

    //will also add nodes if they don't exist
    public void addEdge(int fromNode, int toNode, double weight) {
        adjacencyMat[fromNode][toNode] = weight;
    }

    public double getEdge (int fromNode, int toNode) {
        return adjacencyMat[fromNode][toNode];
    }

    @Override
    public String toString() {
        return Arrays.deepToString(adjacencyMat);
    }
}