import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;


import java.util.*;

public class ParisMetro {
    private Graph graph;

    public ParisMetro() {
        this.graph = new Graph();
    }

    public static ParisMetro readMetro(String fileName) throws IOException {
           ParisMetro parisMetro = new ParisMetro();

           BufferedReader reader = new BufferedReader(new FileReader(fileName));
           String line;

           line = reader.readLine();
           for (int i = 0; i < 376; i++) {
               // Process vertices
                 line = reader.readLine();
                 String[] parts = line.split(" ");
                 int station = Integer.parseInt(parts[0]);
                 parisMetro.graph.addVertex(station);
           }
           line = reader.readLine();
           for (int i = 0; i < 933; i++) {
               // Process edges
               line = reader.readLine();
               String[] parts = line.split(" ");
               int source = Integer.parseInt(parts[0]);
               int destination = Integer.parseInt(parts[1]);
               int weight = Integer.parseInt(parts[2]);

               parisMetro.graph.addEdge(source, destination, weight);
           }

           reader.close();
           return parisMetro;
    }

    // i) Identify all the stations belonging to the same line of a given station (DFS algorithm)
    public int[] identifyStationsOnSameLine(int station) {
        Graph.Vertex startVertex = graph.getVertex(station);

        if (startVertex != null) {
            System.out.print("Stations connected to station " + station + ": ");

            Set<Integer> visited = new HashSet<>();
            identifyStationsOnSameLineRecursive(startVertex, visited);

            // Print the same stations in array format
            System.out.println();
            int[] stationsArray = visited.stream().mapToInt(Integer::intValue).toArray();
            return stationsArray;
        } else {
            System.out.println("Station not found: " + station);
            int[] stationsArray = {};
            return stationsArray;
        }
    }

    private void identifyStationsOnSameLineRecursive(Graph.Vertex vertex, Set<Integer> visited) {
        int currentLine = vertex.getStation();  // Assuming the station number represents the line
        System.out.print(vertex.getStation() + " ");

        visited.add(vertex.getStation());

        for (Graph.Edge edge : vertex.getNeighbors()) {
            if (edge.isWalkingConnection()) {
                continue;  // Skip vertices connected by walking connections
            }

            if (!visited.contains(edge.getDestination().getStation())) {
                identifyStationsOnSameLineRecursive(edge.getDestination(), visited);
            }
        }
    }


    // ii) Find the shortest path between any two stations
    public void findShortestPath(int source, int destination) {
        Graph.Vertex sourceVertex = graph.getVertex(source);
        Graph.Vertex destinationVertex = graph.getVertex(destination);

        if (sourceVertex != null && destinationVertex != null) {
            DijkstraResult result = dijkstra(sourceVertex);

            System.out.println("Shortest path from " + source + " to " + destination + ":");
            printPath(result, destinationVertex);
            System.out.println("Total travel time: " + result.getDistance(destinationVertex) + " seconds");
        } else {
            System.out.println("Invalid source or destination station.");
        }
    }

    // iii) Find the shortest path between two stations when a given line is not functioning
    public void findShortestPathWithBrokenLine(int source, int destination, int brokenLineEndpoint) {
        Graph.Vertex sourceVertex = graph.getVertex(source);
        Graph.Vertex destinationVertex = graph.getVertex(destination);

        if (sourceVertex != null && destinationVertex != null) {
            DijkstraResult result = dijkstraWithBrokenLine(sourceVertex, graph.getVertex(brokenLineEndpoint));

            System.out.println("Shortest path from " + source + " to " + destination + " (considering broken line at " + brokenLineEndpoint + "):");
            printPath(result, destinationVertex);
            System.out.println("Total travel time: " + result.getDistance(destinationVertex) + " seconds");
        } else {
            System.out.println("Invalid source or destination station.");
        }
    }

    // Dijkstra's algorithm to find the shortest path
    private DijkstraResult dijkstra(Graph.Vertex source) {
        PriorityQueue<DijkstraNode> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(DijkstraNode::getDistance));
        Map<Graph.Vertex, Integer> distances = new HashMap<>();
        Map<Graph.Vertex, Graph.Vertex> previousVertices = new HashMap<>();

        // Initialize distances
        for (Graph.Vertex vertex : graph.getVertices()) {
            distances.put(vertex, Integer.MAX_VALUE);
            previousVertices.put(vertex, null);
        }
        distances.put(source, 0);

        priorityQueue.add(new DijkstraNode(source, 0));

        while (!priorityQueue.isEmpty()) {
            DijkstraNode currentNode = priorityQueue.poll();
            Graph.Vertex currentVertex = currentNode.getVertex();

            for (Graph.Edge edge : currentVertex.getNeighbors()) {
                int newDistance = distances.get(currentVertex) + edge.getWeight();

                if (newDistance < distances.get(edge.getDestination())) {
                    distances.put(edge.getDestination(), newDistance);
                    previousVertices.put(edge.getDestination(), currentVertex);
                    priorityQueue.add(new DijkstraNode(edge.getDestination(), newDistance));
                }
            }
        }

        return new DijkstraResult(distances, previousVertices);
    }

    // Dijkstra's algorithm considering a broken line
    private DijkstraResult dijkstraWithBrokenLine(Graph.Vertex source, Graph.Vertex brokenLineEndpoint) {
        PriorityQueue<DijkstraNode> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(DijkstraNode::getDistance));
        Map<Graph.Vertex, Integer> distances = new HashMap<>();
        Map<Graph.Vertex, Graph.Vertex> previousVertices = new HashMap<>();
        int[] restriction = identifyStationsOnSameLine(brokenLineEndpoint.getStation());

        // Initialize distances
        for (Graph.Vertex vertex : graph.getVertices()) {
            distances.put(vertex, Integer.MAX_VALUE);
            previousVertices.put(vertex, null);
        }
        distances.put(source, 0);

        priorityQueue.add(new DijkstraNode(source, 0));

        while (!priorityQueue.isEmpty()) {
            DijkstraNode currentNode = priorityQueue.poll();
            Graph.Vertex currentVertex = currentNode.getVertex();

            for (Graph.Edge edge : currentVertex.getNeighbors()) {
                // Skip edges connected to the broken line endpoint
                if (IntStream.of(restriction).anyMatch(x -> x == currentVertex.getStation()) || IntStream.of(restriction).anyMatch(x -> x == edge.getDestination().getStation())) {
                    continue;
                }

                int newDistance = distances.get(currentVertex) + edge.getWeight();

                if (IntStream.of(restriction).noneMatch(x -> x == currentVertex.getStation()) || IntStream.of(restriction).noneMatch(x -> x == edge.getDestination().getStation())) {
                    if (newDistance < distances.get(edge.getDestination())) {
                        distances.put(edge.getDestination(), newDistance);
                        previousVertices.put(edge.getDestination(), currentVertex);
                        priorityQueue.add(new DijkstraNode(edge.getDestination(), newDistance));
                    }
                }
            }
        }

        return new DijkstraResult(distances, previousVertices);
    }


    // Print the path from source to destination
    private void printPath(DijkstraResult result, Graph.Vertex destination) {
            LinkedList<Integer> path = new LinkedList<>();
            Graph.Vertex current = destination;

            while (current != null) {
                path.addFirst(current.getStation());
                current = result.getPreviousVertices().get(current);
            }

            // Convert LinkedList<Integer> to List<String> for String.join
            List<String> pathStrings = new ArrayList<>();
            for (Integer station : path) {
                pathStrings.add(String.valueOf(station));
            }

            System.out.println("Path: " + String.join(" -> ", pathStrings));
    }

    // DijkstraNode class to represent nodes in the priority queue
    private static class DijkstraNode {
        private Graph.Vertex vertex;
        private int distance;

        public DijkstraNode(Graph.Vertex vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        public Graph.Vertex getVertex() {
            return vertex;
        }

        public int getDistance() {
            return distance;
        }
    }

    // DijkstraResult class to store the result of Dijkstra's algorithm
    private static class DijkstraResult {
        private Map<Graph.Vertex, Integer> distances;
        private Map<Graph.Vertex, Graph.Vertex> previousVertices;

        public DijkstraResult(Map<Graph.Vertex, Integer> distances, Map<Graph.Vertex, Graph.Vertex> previousVertices) {
            this.distances = distances;
            this.previousVertices = previousVertices;
        }

        public int getDistance(Graph.Vertex destination) {
            return distances.get(destination);
        }

        public Map<Graph.Vertex, Graph.Vertex> getPreviousVertices() {
            return previousVertices;
        }
    }

    public static void main(String[] args) {
        try {
            ParisMetro parisMetro = ParisMetro.readMetro("metro.txt");

            if (args.length == 1) {
                // Question 2-i
                int station = Integer.parseInt(args[0]);
                parisMetro.identifyStationsOnSameLine(station);
            } else if (args.length == 2) {
                // Question 2-ii
                int source = Integer.parseInt(args[0]);
                int destination = Integer.parseInt(args[1]);
                parisMetro.findShortestPath(source, destination);
            } else if (args.length == 3) {
                // Question 2-iii
                int source = Integer.parseInt(args[0]);
                int destination = Integer.parseInt(args[1]);
                int brokenLineEndpoint = Integer.parseInt(args[2]);
                parisMetro.findShortestPathWithBrokenLine(source, destination, brokenLineEndpoint);
            } else {
                System.out.println("Invalid number of arguments.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
