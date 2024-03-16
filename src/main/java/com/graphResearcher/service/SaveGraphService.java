package com.graphResearcher.service;

import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.springframework.stereotype.Service;

@Service
public class SaveGraphService {
    private static final int EMPTY_MASK = 0;
    private static final int IS_DIRECTED = 1 << 0;
    private static final int HAS_MULTIPLE_EDGES = 1 << 1;
    private static final int HAS_SELF_LOOPS = 1 << 2;

    private static Graph<Vertex, DefaultWeightedEdge> getEmptyGraph(GraphModel graph) {
        int mask = EMPTY_MASK;
        if (graph.info.isDirected) {
            mask |= IS_DIRECTED;
        }
        if (graph.info.hasMultipleEdges) {
            mask |= HAS_MULTIPLE_EDGES;
        }
        if (graph.info.hasSelfLoops) {
            mask |= HAS_SELF_LOOPS;
        }

        return switch (mask) {
            case EMPTY_MASK -> new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
            case IS_DIRECTED -> new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            case HAS_MULTIPLE_EDGES -> new WeightedMultigraph<>(DefaultWeightedEdge.class);
            case HAS_SELF_LOOPS -> new DefaultUndirectedWeightedGraph<>(DefaultWeightedEdge.class);
            case IS_DIRECTED | HAS_MULTIPLE_EDGES -> new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
            case IS_DIRECTED | HAS_SELF_LOOPS -> new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
            case HAS_MULTIPLE_EDGES | HAS_SELF_LOOPS -> new WeightedPseudograph<>(DefaultWeightedEdge.class);
            default -> new DirectedWeightedPseudograph<>(DefaultWeightedEdge.class);
        };
    }


    public static Graph<Vertex, DefaultWeightedEdge> buildGraph(GraphModel graph) {
        Graph<Vertex, DefaultWeightedEdge> g = getEmptyGraph(graph);
        for (int i = 0; i < graph.getVertices().size(); i++) {
            g.addVertex(graph.getVertices().get(i));
        }
        for (int i = 0; i < graph.getEdges().size(); i++) {
            g.addEdge(graph.getEdges().get(i).getSourceVertex(), graph.getEdges().get(i).getTargetVertex());
            if (graph.info.isWeighted){
                g.setEdgeWeight(graph.getEdges().get(i).getSourceVertex(), graph.getEdges().get(i).getTargetVertex(), graph.getEdges().get(i).weight);
            }
        }
        return g;
    }
}
