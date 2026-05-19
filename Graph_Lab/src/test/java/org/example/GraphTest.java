package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class GraphTest {

    @Test
    public void testMSTEquivalence() {
        Graph graph = GraphGenerator.generateSparseGraph(100, 42L);
        List<Edge> primEdges = graph.primMST();
        List<Edge> kruskalEdges = graph.kruskalMST();

        long primWeight = 0;
        for (Edge e : primEdges) primWeight += e.weight;

        long kruskalWeight = 0;
        for (Edge e : kruskalEdges) kruskalWeight += e.weight;

        assertEquals(primWeight, kruskalWeight, "Prim and Kruskal muse be equal");
    }

    @Test
    public void testSSSDEquivalence() {
        Graph dag = GraphGenerator.generateDAG(100, 42L);
        int source = 0;

         int[] dijkstraDist = dag.dijkstra(source);
        int[] dagDist = dag.dagShortestPath(source);
        int[] kahnDist = dag.dagShortestPathKahn(source);

         assertArrayEquals(dijkstraDist, dagDist, "Dijkstra and DAG (DFS) .");
        assertArrayEquals(dijkstraDist, kahnDist, "Dijkstra and DAG (Kahn) .");
    }

    @Test
    public void testCycleDetection() {
        Graph badDag = new Graph(4);
        badDag.addDirectedEdge(0, 1, 10);
        badDag.addDirectedEdge(1, 2, 10);
        badDag.addDirectedEdge(2, 0, 10);
        badDag.addDirectedEdge(2, 3, 10);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            badDag.dagShortestPath(0);
        });


        assertTrue(exception.getMessage().contains("Cycle detected"));
    }
}