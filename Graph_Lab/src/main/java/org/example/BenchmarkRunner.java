package org.example;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;

public class BenchmarkRunner {

    private static final int VERTICES = 5000;
    private static final int RUNS = 5;
    private static final long SEED = 42L;

    public static void main(String[] args) {

        System.out.println("Generating Graphs (V = " + VERTICES + ")");

        long genStart = System.currentTimeMillis();
        Graph sparseGraph = GraphGenerator.generateSparseGraph(VERTICES, SEED);
        Graph denseGraph = GraphGenerator.generateDenseGraph(VERTICES, SEED);
        Graph completeGraph = GraphGenerator.generateCompleteGraph(VERTICES, SEED);
        Graph dag = GraphGenerator.generateDAG(VERTICES, SEED);
        System.out.println("Graph generation took " + (System.currentTimeMillis() - genStart) / 1000.0 + " seconds.\n");

        StringBuilder mstCsv = new StringBuilder("Graph Topology,Algorithm,Mean (ms),Median (ms),Standard Deviation (ms)\n");
        StringBuilder ssspCsv = new StringBuilder("Graph Topology,Algorithm,Mean (ms),Median (ms),Standard Deviation (ms)\n");
        StringBuilder dagCsv = new StringBuilder("Graph Topology,Algorithm,Mean (ms),Median (ms),Standard Deviation (ms),Speed-up Multiplier,Time Decrease (%)\n");

        System.out.println("--- 1. MST Construction Benchmarks ---");
        runMSTBenchmark("Sparse Graph (E ~ 5V)", sparseGraph, mstCsv);
        runMSTBenchmark("Dense Graph (E ~ 25%)", denseGraph, mstCsv);
        runMSTBenchmark("Complete Graph (E ~ 100%)", completeGraph, mstCsv);

        System.out.println("\n 2. SSSP Calculation (General Graphs) ");
        int source = 0;
        runDijkstraBenchmark("Sparse Graph (E ~ 5V)", sparseGraph, source, ssspCsv);
        runDijkstraBenchmark("Dense Graph (E ~ 25%)", denseGraph, source, ssspCsv);
        runDijkstraBenchmark("Complete Graph (E ~ 100%)", completeGraph, source, ssspCsv);
        runDijkstraBenchmark("DAG (Directed Acyclic)", dag, source, ssspCsv);

        System.out.println("\n 3. SSSP Calculation (DAG Topology 3-Way Comparison) ");
        runDAGComparisonBenchmark(dag, source, dagCsv);

        saveToFile("MST_Results.csv", mstCsv.toString());
        saveToFile("SSSP_Results.csv", ssspCsv.toString());
        saveToFile("DAG_Results.csv", dagCsv.toString());

        System.out.println("\n  3 clean CSV files generated    !");
    }

    private static void runMSTBenchmark(String graphType, Graph graph, StringBuilder csv) {
        double[] primTimes = new double[RUNS];
        double[] kruskalTimes = new double[RUNS];

        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            graph.primMST();
            primTimes[i] = (System.nanoTime() - start) / 1_000_000.0;

            start = System.nanoTime();
            graph.kruskalMST();
            kruskalTimes[i] = (System.nanoTime() - start) / 1_000_000.0;
        }

        printStats(graphType, "Prim's Algorithm", primTimes, csv);
        printStats(graphType, "Kruskal's Algorithm", kruskalTimes, csv);
    }

    private static void runDijkstraBenchmark(String graphType, Graph graph, int source, StringBuilder csv) {
        double[] times = new double[RUNS];
        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            graph.dijkstra(source);
            times[i] = (System.nanoTime() - start) / 1_000_000.0;
        }
        printStats(graphType, "Dijkstra's Algorithm", times, csv);
    }

    private static void runDAGComparisonBenchmark(Graph dag, int source, StringBuilder csv) {
        double[] dijkstraTimes = new double[RUNS];
        double[] dagDfsTimes = new double[RUNS];
        double[] dagKahnTimes = new double[RUNS];

        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            dag.dijkstra(source);
            dijkstraTimes[i] = (System.nanoTime() - start) / 1_000_000.0;

            start = System.nanoTime();
            dag.dagShortestPath(source);
            dagDfsTimes[i] = (System.nanoTime() - start) / 1_000_000.0;

            start = System.nanoTime();
            dag.dagShortestPathKahn(source);
            dagKahnTimes[i] = (System.nanoTime() - start) / 1_000_000.0;
        }

        double[] dStats = calculateStats(dijkstraTimes);
        double[] dfsStats = calculateStats(dagDfsTimes);
        double[] kahnStats = calculateStats(dagKahnTimes);

        System.out.printf(Locale.US, "Dijkstra         | Mean: %8.2f ms\n", dStats[0]);
        System.out.printf(Locale.US, "DAG (DFS)        | Mean: %8.2f ms\n", dfsStats[0]);
        System.out.printf(Locale.US, "DAG (Kahn Array) | Mean: %8.2f ms\n", kahnStats[0]);

        // Calculate Speed-ups compared to Dijkstra
        double dfsMultiplier = dStats[0] / dfsStats[0];
        double dfsPercentage = ((dStats[0] - dfsStats[0]) / dStats[0]) * 100;

        double kahnMultiplier = dStats[0] / kahnStats[0];
        double kahnPercentage = ((dStats[0] - kahnStats[0]) / dStats[0]) * 100;

        // Write perfectly formatted CSV rows
        csv.append(String.format(Locale.US, "\"%s\",\"%s\",%.2f,%.2f,%.2f,1.00x,0.00%%\n",
                "DAG", "Dijkstra", dStats[0], dStats[1], dStats[2]));

        csv.append(String.format(Locale.US, "\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2fx,%.2f%%\n",
                "DAG", "Linear (DFS)", dfsStats[0], dfsStats[1], dfsStats[2], dfsMultiplier, dfsPercentage));

        csv.append(String.format(Locale.US, "\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2fx,%.2f%%\n",
                "DAG", "Linear (Kahn)", kahnStats[0], kahnStats[1], kahnStats[2], kahnMultiplier, kahnPercentage));
    }


    private static void printStats(String graphType, String algoName, double[] times, StringBuilder csv) {
        double[] stats = calculateStats(times);
        System.out.printf(Locale.US, "%-25s on %-25s | Mean: %8.2f ms | Median: %8.2f ms | StdDev: %8.2f ms\n",
                algoName, graphType, stats[0], stats[1], stats[2]);

        csv.append(String.format(Locale.US, "\"%s\",\"%s\",%.2f,%.2f,%.2f\n",
                graphType, algoName, stats[0], stats[1], stats[2]));
    }

    private static double[] calculateStats(double[] times) {
        double sum = 0;
        for (double t : times) sum += t;
        double mean = sum / RUNS;
        double[] sorted = times.clone();
        Arrays.sort(sorted);
        double median = sorted[RUNS / 2];
        double varianceSum = 0;
        for (double t : times) varianceSum += Math.pow(t - mean, 2);
        return new double[]{mean, median, Math.sqrt(varianceSum / RUNS)};
    }

    private static void saveToFile(String filename, String content) {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Error writing " + filename);
        }
    }
}
