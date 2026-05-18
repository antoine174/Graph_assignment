package org.example;

import org.example.interfaces.IGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Graph implements IGraph {
    private final int vertices;
    private final List<List<Edge>> adjList;
    private final List<Edge> edgeList;
    boolean hasNeg;

    public Graph(int vertices, List<List<Edge>> adjList, List<Edge> edgeList) {
        this.vertices = vertices;
        this.adjList = adjList;
        this.edgeList = edgeList;
        this.hasNeg=false;
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    @Override
    public void addEdge(int u, int v, int weight) {
        if(weight<0){
            this.hasNeg=true;
        }
        Edge fEdge = new Edge(u, v, weight);
        Edge bEdge = new Edge(v, u, weight);
        adjList.get(u).add(fEdge);
        adjList.get(v).add(bEdge);
        edgeList.add(fEdge);
    }

    @Override
    public void addDirectedEdge(int u, int v, int weight) {
        if(weight<0){
            this.hasNeg=true;
        }
        Edge fEdge = new Edge(u, v, weight);
        adjList.get(u).add(fEdge);
        edgeList.add(fEdge);
    }

    @Override
    public List<Edge> primMST() {
        int source = 0;

        int[] dist = new int[this.vertices];
        int[] par = new int[this.vertices];
        boolean[] inMST = new boolean[this.vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(par, -1);
        dist[source] = 0;
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        pq.add(new Pair(0, source));
        while (!pq.isEmpty()){
            Pair current = pq.poll();
            int disNode = current.first();
            int topNode = current.second();
            if(inMST[topNode]){
                continue;
            }
            inMST[topNode]=true;

            for (Edge edge : adjList.get(topNode)) {
                int adjNode = edge.v();
                int weight = edge.weight();

                 if(!inMST[adjNode] &&  weight<dist[adjNode]){
                     dist[adjNode]=weight;
                     par[adjNode]=topNode;
                     pq.add(new Pair(weight,adjNode));
                 }
            }


        }
        List<Edge> theEdges=new ArrayList<>();
        for(int i=0;i<this.vertices;i++){
            if(i!=source){
                if(par[i]!=-1){
                    theEdges.add(new Edge(par[i],i,dist[i]));
                }
            }

        }


        return theEdges;
    }

    @Override
    public List<Edge> kruskalMST() {

        return List.of();
    }

    @Override
    public int[] dijkstra(int source) {
        if (this.hasNeg) {
            throw new IllegalStateException("Dijkstra's algorithm does not support negative edge weights.");
        }

        int[] dist = new int[this.vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        PriorityQueue<Pair> pq = new PriorityQueue<>();
        pq.add(new Pair(0, source));

        while (!pq.isEmpty()) {
            Pair current = pq.poll();
            int disNode = current.first();
            int topNode = current.second();
    if (disNode > dist[topNode]) {
                continue;
            }

            for (Edge edge : adjList.get(topNode)) {
                int adjNode = edge.v();
                int weight = edge.weight();

                 if (disNode + weight < dist[adjNode]) {
                    dist[adjNode] = disNode + weight;
                    pq.add(new Pair(dist[adjNode], adjNode));
                }
            }
        }

        return dist;
    }

    @Override
    public int[] dagShortestPath(int source) {
        return new int[0];
    }
}
