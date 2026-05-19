package org.example;

import java.util.*;

public class GraphGenerator {
    private static final int MIN_WEIGHT = 1;
    private static final int MAX_WEIGHT = 1000;

    private static int getRandomWeight(Random random) {
        return random.nextInt(MAX_WEIGHT) + MIN_WEIGHT;
    }

    private static long encodeEdge(int u, int v) {
        int min = Math.min(u, v);
        int max = Math.max(u, v);
        return ((long) min << 32) | (max & 0xFFFFFFFFL);
    }

    public static Graph generateSparseGraph(int vertices, long seed) {
        Graph graph = new Graph(vertices);
        Random random = new Random(seed);
        int targetEdges = 5 * vertices;
        Set<Long> existingEdges = new HashSet<>();
        List<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < vertices; i++) nodes.add(i);
        Collections.shuffle(nodes, random);

        for (int i = 0; i < vertices - 1; i++) {
            int u = nodes.get(i);
            int v = nodes.get(i + 1);
            graph.addEdge(u, v, getRandomWeight(random));
            existingEdges.add(encodeEdge(u, v));
        }

        int edgesAdded = vertices - 1;
        while (edgesAdded < targetEdges) {
            int u = random.nextInt(vertices);
            int v = random.nextInt(vertices);

            if (u != v) {
                long edgeId = encodeEdge(u, v);
                if (!existingEdges.contains(edgeId)) {
                    graph.addEdge(u, v, getRandomWeight(random));
                    existingEdges.add(edgeId);
                    edgesAdded++;
                }
            }
        }
        return graph;
    }

    public static Graph generateDenseGraph(int vertices, long seed) {
        Graph graph = new Graph(vertices);
        Random random = new Random(seed);
        Set<Long> treeEdges = new HashSet<>();

        List<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < vertices; i++) nodes.add(i);
        Collections.shuffle(nodes, random);

        for (int i = 0; i < vertices - 1; i++) {
            int u = nodes.get(i);
            int v = nodes.get(i + 1);
            graph.addEdge(u, v, getRandomWeight(random));
            treeEdges.add(encodeEdge(u, v));
        }

        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                if (!treeEdges.contains(encodeEdge(i, j))) {
                    if (random.nextDouble() < 0.25) {
                        graph.addEdge(i, j, getRandomWeight(random));
                    }
                }
            }
        }
        return graph;
    }

    public static Graph generateCompleteGraph(int vertices, long seed) {
        Graph graph = new Graph(vertices);
        Random random = new Random(seed);

        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                graph.addEdge(i, j, getRandomWeight(random));
            }
        }
        return graph;
    }

    public static Graph generateDAG(int vertices, long seed) {
        Graph graph = new Graph(vertices);
        Random random = new Random(seed);
        int targetEdges = 5 * vertices;
        Set<Long> existingEdges = new HashSet<>();

        for (int i = 0; i < vertices - 1; i++) {
            graph.addDirectedEdge(i, i + 1, getRandomWeight(random));
            existingEdges.add(encodeEdge(i, i + 1));
        }

        int edgesAdded = vertices - 1;
        while (edgesAdded < targetEdges) {
            int u = random.nextInt(vertices - 1);
            int v = u + 1 + random.nextInt(vertices - u - 1);

            long edgeId = encodeEdge(u, v);
            if (!existingEdges.contains(edgeId)) {
                graph.addDirectedEdge(u, v, getRandomWeight(random));
                existingEdges.add(edgeId);
                edgesAdded++;
            }
        }
        return graph;
    }
}