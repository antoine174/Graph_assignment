package org.example.interfaces;

import org.example.Edge; // You will need to create this package/class
import java.util.List;

public interface IGraph {
    void addEdge(int u, int v, int weight);
    void addDirectedEdge(int u, int v, int weight);
    List<Edge> primMST();
    List<Edge> kruskalMST();
    int[] dijkstra(int source);
    int[] dagShortestPath(int source);
}