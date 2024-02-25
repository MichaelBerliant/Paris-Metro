import java.util.*;

class Graph {
    // Represents the graph vertices
    private List<Vertex> vertices;

    // Graph constructor initializes the vertices list
    public Graph() {
        this.vertices = new ArrayList<>();
    }

    // Adds a new vertex to the graph
    public void addVertex(int station) {
        vertices.add(new Vertex(station));
    }

    // Adds an edge between source and destination with a specified weight
    public void addEdge(int source, int destination, int weight) {
        // Get the source and destination vertices
        Vertex sourceVertex = getVertex(source);
        Vertex destinationVertex = getVertex(destination);

        // Check if the edge represents a walking connection
        boolean isWalkingConnection = (weight == -1);

        // If it's a walking connection, change the weight to 90
        if (isWalkingConnection) {
            weight = 90;
        }

        // Add the edge to the source vertex's neighbors
        sourceVertex.addNeighbor(destinationVertex, weight, isWalkingConnection);
    }

    // Get all vertices in the graph
    public List<Vertex> getVertices() {
        return vertices;
    }

    // Get a specific vertex by station number
    public Vertex getVertex(int station) {
        for (Vertex vertex : vertices) {
            if (vertex.getStation() == station) {
                return vertex;
            }
        }
        return null;
    }

    // Represents a vertex in the graph
    static class Vertex {
        // Station number of the vertex
        private int station;

        // List of neighbors (edges) connected to this vertex
        private List<Edge> neighbors;

        // Vertex constructor initializes the station and neighbors list
        public Vertex(int station) {
            this.station = station;
            this.neighbors = new ArrayList<>();
        }

        // Get the station number of the vertex
        public int getStation() {
            return station;
        }

        // Get the list of neighbors (edges) connected to this vertex
        public List<Edge> getNeighbors() {
            return neighbors;
        }

        // Add a new neighbor (edge) to this vertex
        public void addNeighbor(Vertex destination, int weight, boolean isWalkingConnection) {
            neighbors.add(new Edge(destination, weight, isWalkingConnection));
        }
    }

    // Represents an edge between two vertices in the graph
    static class Edge {
        // Destination vertex of the edge
        private Vertex destination;

        // Weight of the edge
        private int weight;

        // Indicates whether the edge represents a walking connection
        private boolean isWalkingConnection;

        // Edge constructor initializes the destination, weight, and walking connection status
        public Edge(Vertex destination, int weight, boolean isWalkingConnection) {
            this.destination = destination;
            this.weight = weight;
            this.isWalkingConnection = isWalkingConnection;
        }

        // Get the destination vertex of the edge
        public Vertex getDestination() {
            return destination;
        }

        // Get the weight of the edge
        public int getWeight() {
            return weight;
        }

        // Check if the edge represents a walking connection
        public boolean isWalkingConnection() {
            return isWalkingConnection;
        }
    }
}
