package com.graphResearcher.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.model.*;
import org.jgrapht.Graph;
import org.jgrapht.graph.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParsingUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ArrayNode graphsListToJsonArray(List<GraphModel> list) {
        return list.stream().map(GraphModel::toJson).collect(
                mapper::createArrayNode,
                ArrayNode::add,
                ArrayNode::addAll
        );
    }

    public static ArrayNode edgesListToJsonArray(List<Edge> list) {
        return list.stream().map(Edge::toJson).collect(
                mapper::createArrayNode,
                ArrayNode::add,
                ArrayNode::addAll
        );
    }

    public static ArrayNode verticesListToJsonArray(List<Vertex> list) {
        return list.stream().map(Vertex::toJson).collect(
                mapper::createArrayNode,
                ArrayNode::add,
                ArrayNode::addAll
        );
    }

    public static GraphModel graphToGraphModel(Graph<Vertex, WeightedEdge> graph, GraphMetadata metadata) {
        return new GraphModel(new ArrayList<>(graph.vertexSet()), graph.edgeSet().stream().map(WeightedEdge::toEdge).toList(), metadata);
    }

    public static GraphResearchInfo resultSetToGraphResearchInfo(ResultSet rs) throws SQLException, JsonProcessingException {
        rs.next();
        GraphResearchInfo info = new GraphResearchInfo();
        info.isConnected = rs.getBoolean("is_connected");
        if (rs.wasNull()) {
            info.isConnected = null;
        }
        info.isBiconnected = rs.getBoolean("is_biconnected");
        if (rs.wasNull()) {
            info.isBiconnected = null;
        }
        info.isPlanar = rs.getBoolean("is_planar");
        if (rs.wasNull()) {
            info.isPlanar = null;
        }
        info.isChordal = rs.getBoolean("is_chordal");
        if (rs.wasNull()) {
            info.isChordal = null;
        }
        info.chromaticNumber = rs.getInt("chromatic_number");
        if (rs.wasNull()) {
            info.chromaticNumber = null;
        }

        return info;
    }

    public static GraphModel listVertexToSubgraph(List<Vertex> vertices, GraphModel graph) {
        Set<Vertex> vertexSet = new HashSet<>(vertices);

        List<Edge> edges = new ArrayList<>();
        for (Edge e: graph.getEdges()) {
            if (vertexSet.contains(e.getSource()) && vertexSet.contains(e.getTarget())) {
                edges.add(e);
            }
        }
        return new GraphModel(vertices, edges, graph.getMetadata());
    }

    private static Graph<Vertex, WeightedEdge> getEmptyGraph(GraphModel graphModel) {
        final int EMPTY_MASK = 0;
        final int IS_DIRECTED = 1;
        final int IS_WEIGHTED = 1 << 1;
        final int HAS_MULTIPLE_EDGES = 1 << 2;
        final int HAS_SELF_LOOPS = 1 << 3;
        int mask = EMPTY_MASK;
        if (graphModel.getMetadata().isDirected) {
            mask |= IS_DIRECTED;
        }
        if (graphModel.getMetadata().isWeighted) {
            mask |= IS_WEIGHTED;
        }
        if (graphModel.getMetadata().hasMultipleEdges) {
            mask |= HAS_MULTIPLE_EDGES;
        }
        if (graphModel.getMetadata().hasSelfLoops) {
            mask |= HAS_SELF_LOOPS;
        }
        return switch (mask) {
            case EMPTY_MASK -> new SimpleGraph<>(WeightedEdge.class);
            case IS_DIRECTED -> new SimpleDirectedGraph<>(WeightedEdge.class);
            case HAS_MULTIPLE_EDGES -> new Multigraph<>(WeightedEdge.class);
            case HAS_SELF_LOOPS -> new DefaultUndirectedGraph<>(WeightedEdge.class);
            case IS_WEIGHTED -> new SimpleWeightedGraph<>(WeightedEdge.class);
            case IS_DIRECTED | HAS_MULTIPLE_EDGES -> new DirectedMultigraph<>(WeightedEdge.class);
            case IS_DIRECTED | IS_WEIGHTED -> new SimpleDirectedWeightedGraph<>(WeightedEdge.class);
            case IS_DIRECTED | HAS_SELF_LOOPS -> new DefaultDirectedGraph<>(WeightedEdge.class);
            case HAS_MULTIPLE_EDGES | HAS_SELF_LOOPS -> new Pseudograph<>(WeightedEdge.class);
            case HAS_MULTIPLE_EDGES | IS_WEIGHTED -> new WeightedMultigraph<>(WeightedEdge.class);
            case HAS_SELF_LOOPS | IS_WEIGHTED -> new DefaultUndirectedWeightedGraph<>(WeightedEdge.class);
            case IS_DIRECTED | HAS_MULTIPLE_EDGES | HAS_SELF_LOOPS -> new DirectedPseudograph<>(WeightedEdge.class);
            case IS_DIRECTED | HAS_MULTIPLE_EDGES | IS_WEIGHTED -> new DirectedWeightedMultigraph<>(WeightedEdge.class);
            case IS_DIRECTED | HAS_SELF_LOOPS | IS_WEIGHTED -> new DefaultDirectedWeightedGraph<>(WeightedEdge.class);
            case HAS_MULTIPLE_EDGES | HAS_SELF_LOOPS | IS_WEIGHTED -> new WeightedPseudograph<>(WeightedEdge.class);
            default -> new DirectedWeightedPseudograph<>(WeightedEdge.class);
        };
    }

    public static Graph<Vertex, WeightedEdge> graphModelToGraph(GraphModel graphModel) {
        Graph<Vertex, WeightedEdge> g = getEmptyGraph(graphModel);
        for (Vertex v : graphModel.getVertices()) {
            g.addVertex(v);
        }
        for (Edge e : graphModel.getEdges()) {
            g.addEdge(e.getSource(), e.getTarget());
            if (graphModel.getMetadata().isWeighted) {
                g.setEdgeWeight(e.getSource(), e.getTarget(), e.getWeight());
            }
        }
        return g;
    }
}
