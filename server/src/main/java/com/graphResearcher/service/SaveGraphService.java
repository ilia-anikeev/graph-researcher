package com.graphResearcher.service;

import com.graphResearcher.model.Edge;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;

@Service
public class SaveGraphService {
    private static final int EMPTY_MASK = 0;
    private static final int IS_DIRECTED = 1;
    private static final int IS_WEIGHTED = 1 << 1;
    private static final int HAS_MULTIPLE_EDGES = 1 << 2;
    private static final int HAS_SELF_LOOPS = 1 << 3;

    private static Graph<Vertex, DefaultWeightedEdge> getEmptyGraph(GraphModel graph) {
        int mask = EMPTY_MASK;
        if (graph.info.isDirected) {
            mask |= IS_DIRECTED;
        }
        if (graph.info.isWeighted) {
            mask |= IS_WEIGHTED;
        }
        if (graph.info.hasMultipleEdges) {
            mask |= HAS_MULTIPLE_EDGES;
        }
        if (graph.info.hasSelfLoops) {
            mask |= HAS_SELF_LOOPS;
        }
        return switch (mask) {
            case EMPTY_MASK -> new SimpleGraph<>(DefaultWeightedEdge.class);
            case IS_DIRECTED -> new SimpleDirectedGraph<>(DefaultWeightedEdge.class);
            case HAS_MULTIPLE_EDGES -> new Multigraph<>(DefaultWeightedEdge.class);
            case HAS_SELF_LOOPS -> new DefaultUndirectedGraph<>(DefaultWeightedEdge.class);
            case IS_WEIGHTED -> new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
            case IS_DIRECTED | HAS_MULTIPLE_EDGES -> new DirectedMultigraph<>(DefaultWeightedEdge.class);
            case IS_DIRECTED | IS_WEIGHTED -> new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            case IS_DIRECTED | HAS_SELF_LOOPS -> new DefaultDirectedGraph<>(DefaultWeightedEdge.class);
            case HAS_MULTIPLE_EDGES | HAS_SELF_LOOPS -> new Pseudograph<>(DefaultWeightedEdge.class);
            case HAS_MULTIPLE_EDGES | IS_WEIGHTED -> new WeightedMultigraph<>(DefaultWeightedEdge.class);
            case HAS_SELF_LOOPS | IS_WEIGHTED -> new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);
            case IS_DIRECTED | HAS_MULTIPLE_EDGES | HAS_SELF_LOOPS -> new DirectedPseudograph<>(DefaultWeightedEdge.class);
            case IS_DIRECTED | HAS_MULTIPLE_EDGES | IS_WEIGHTED -> new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
            case IS_DIRECTED | HAS_SELF_LOOPS | IS_WEIGHTED -> new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            case HAS_MULTIPLE_EDGES | HAS_SELF_LOOPS | IS_WEIGHTED -> new WeightedPseudograph<>(DefaultWeightedEdge.class);
            default -> new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
        };
    }

    public static Graph<Vertex, DefaultWeightedEdge> buildGraph(GraphModel graph) {
        Graph<Vertex, DefaultWeightedEdge> g = getEmptyGraph(graph);
        for (Vertex v : graph.getVertices()) {
            g.addVertex(v);
        }
        for (Edge e : graph.getEdges()) {
            g.addEdge(e.getSource(), e.getTarget());
            if (graph.info.isWeighted) {
                g.setEdgeWeight(e.getSource(), e.getTarget(), e.getWeight());
            }
        }
        return g;
    }
}
