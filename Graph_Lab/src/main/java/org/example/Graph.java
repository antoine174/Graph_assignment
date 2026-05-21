package org.example;

import org.example.interfaces.IGraph;

import java.util.*;

public class Graph implements IGraph {
    private final int vertices;
    private final List<List<Edge>> adjList;
    private final List<Edge> edgeList;
    boolean hasNeg;

    public Graph(int vertices) {
        this.vertices = vertices;
        this.adjList = new ArrayList<>(vertices);
        this.edgeList = new ArrayList<>();
        this.hasNeg = false;
        for (int i = 0; i < vertices; i++) {
            this.adjList.add(new ArrayList<>());
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
        if (this.vertices == 0) return new ArrayList<>();

        int[] dist = new int[this.vertices];
        int[] par = new int[this.vertices];
        boolean[] inMST = new boolean[this.vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(par, -1);
        PriorityQueue<Pair> pq = new PriorityQueue<>();
          for (int i= 0; i <this.vertices; i++) {
            if (!inMST[i]) {
                dist[i] = 0;
                pq.add(new Pair(0, i));
                while (!pq.isEmpty()){
                    Pair current = pq.poll();
                    int topNode = current.second();
                    if(inMST[topNode]){
                        continue;
                    }
                    inMST[topNode] = true;
                    for (Edge edge : adjList.get(topNode)) {
                        int adjNode = edge.v;
                        int weight = edge.weight;
                        if(!inMST[adjNode] && weight < dist[adjNode]){
                            dist[adjNode] = weight;
                            par[adjNode] = topNode;
                            pq.add(new Pair(weight, adjNode));
                        }
                    }
                }
            }
        }

        List<Edge> theEdges = new ArrayList<>();

        for(int i = 0; i < this.vertices; i++){
            if(par[i] != -1){
                theEdges.add(new Edge(par[i], i, dist[i]));
            }
        }

        return theEdges;
    }

    @Override
    public List<Edge> kruskalMST() {
        PriorityQueue<Edge> pq = new PriorityQueue<>();
        for(Edge edge : edgeList){
            pq.add(edge);
        }
        DSU dsu=new DSU(this.vertices);
        List<Edge> kruEdges=new ArrayList<>();
        while (!pq.isEmpty()){
            Edge temp=pq.poll();
            if(!dsu.join(temp.u,temp.v)){
              continue;
            }
            kruEdges.add(temp);
            if (kruEdges.size() == this.vertices-1) {
                break;
            }
        }
        return kruEdges;
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
                int adjNode = edge.v;
                int weight = edge.weight;

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
        List<Integer> topoList = new ArrayList<>();
        int[] state = new int[this.vertices];
        DFS(source, state, topoList);

        Collections.reverse(topoList);
        int[] dist = new int[this.vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;
        for (int u: topoList) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (Edge edge : adjList.get(u)) {
                    if (dist[edge.v] > dist[u] + edge.weight) {
                        dist[edge.v] = dist[u] + edge.weight;
                    }
                }
            }
        }
        return dist;
    }

    private void DFS(int u, int[] state, List<Integer> list) {
        state[u]= 1;
        for (Edge edge : adjList.get(u)) {
            if (state[edge.v] == 1) {
                throw new IllegalStateException("Cycle detected! This is not DAG");
            }
            if (state[edge.v] == 0) {
                DFS(edge.v, state, list);
            }
        }
        state[u] = 2;
        list.add(u);
    }

    public int[] dagShortestPathKahn(int source) {
        int[] inDegree = new int[this.vertices];
        for (int u = 0; u < this.vertices; u++) {
            for (Edge edge : adjList.get(u)) {
                inDegree[edge.v]++;
            }
        }
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < this.vertices; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }
        List<Integer> topoList = new ArrayList<>(this.vertices);
        int processedCount = 0;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            topoList.add(u);
            processedCount++;
            for (Edge edge : adjList.get(u)) {
                inDegree[edge.v]--;
                if (inDegree[edge.v] == 0) {
                    queue.add(edge.v);
                }
            }
        }

        if (processedCount != this.vertices) {
            throw new IllegalStateException("Cycle detected!");
        }

        int[] dist = new int[this.vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;
        for (int u : topoList) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (Edge edge : adjList.get(u)) {
                    if (dist[edge.v] > dist[u] + edge.weight) {
                        dist[edge.v] = dist[u] + edge.weight;
                    }
                    }
            }
        }

        return dist;
    }
}
