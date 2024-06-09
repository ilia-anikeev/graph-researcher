package com.graphResearcher.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.model.*;
import com.graphResearcher.model.graphInfo.GraphResearchInfo;
import org.jgrapht.Graph;
import org.jgrapht.graph.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Converter {
    private static final int EMPTY_MASK = 0;
    private static final int IS_DIRECTED = 1;
    private static final int IS_WEIGHTED = 1 << 1;
    private static final int HAS_MULTIPLE_EDGES = 1 << 2;
    private static final int HAS_SELF_LOOPS = 1 << 3;

    private static Graph<Vertex, WeightedEdge> getEmptyGraph(GraphModel graphModel) {
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
            g.addEdge(e.source, e.target);
            if (graphModel.getMetadata().isWeighted) {
                g.setEdgeWeight(e.source, e.target, e.weight);
            }
        }
        return g;
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ArrayNode listListVerticesToJsonArray(List<List<Vertex>> list) {
        return list.stream().map(Converter::verticesListToJsonArray).collect(
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

    public static ArrayNode GraphModelListToJsonArray(List<GraphModel> list) {
        return list.stream().map(GraphModel::toJson).collect(
                mapper::createArrayNode,
                ArrayNode::add,
                ArrayNode::addAll
        );
    }

    public static JsonNode mapOfVertexAndListEdgesToJsonArray(Map<Vertex, List<Edge>> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        for (Map.Entry<Vertex, List<Edge>> entry: map.entrySet()) {
            json.set(entry.getKey().getIndex() + "", Converter.edgesListToJsonArray(entry.getValue()));
        }
        return json;
    }

    public static GraphModel graphToGraphModel(Graph<Vertex, WeightedEdge> graph, GraphMetadata metadata) {
        return new GraphModel(new ArrayList<>(graph.vertexSet()), graph.edgeSet().stream().map(WeightedEdge::toEdge).toList(), metadata);
    }

    public static GraphResearchInfo resultSetToGraphResearchInfo(ResultSet rs) throws SQLException, JsonProcessingException {
        rs.next();
        GraphResearchInfo info = new GraphResearchInfo();
        info.connectivityInfo.isConnected = rs.getBoolean("is_connected");
        if (rs.wasNull()) {
            info.connectivityInfo.isConnected = null;
        }
        info.connectivityInfo.isBiconnected = rs.getBoolean("is_biconnected");
        if (rs.wasNull()) {
            info.connectivityInfo.isBiconnected = null;
        }
        info.planarityInfo.isPlanar = rs.getBoolean("is_planar");
        if (rs.wasNull()) {
            info.planarityInfo.isPlanar = null;
        }
        info.chordalityInfo.isChordal = rs.getBoolean("is_chordal");
        if (rs.wasNull()) {
            info.chordalityInfo.isChordal = null;
        }
        info.chordalityInfo.chromaticNumber = rs.getInt("chromatic_number");
        if (rs.wasNull()) {
            info.chordalityInfo.chromaticNumber = null;
        }
        info.bipartitePartitioningInfo.isBipartite = rs.getBoolean("is_bipartite");
        if (rs.wasNull()) {
            info.bipartitePartitioningInfo.isBipartite = null;
        }
        return info;
    }

    public static GraphModel listVertexToSubgraph(List<Vertex> vertices, GraphModel graph) {
        Set<Vertex> vertexSet = new HashSet<>(vertices);

        List<Edge> edges = new ArrayList<>();
        for (Edge e : graph.getEdges()) {
            if (vertexSet.contains(e.source) && vertexSet.contains(e.target)) {
                edges.add(e);
            }
        }
        return new GraphModel(vertices, edges, graph.getMetadata());
    }

    public static String getFields(GraphResearchInfo info, int graphID) {
        StringBuilder answer = new StringBuilder();

        answer.append(graphID);

        if (info.connectivityInfo.isConnected != null) {
            answer.append(", '");
            answer.append(info.connectivityInfo.isConnected);
            answer.append("'");
        }
        if (info.connectivityInfo.isBiconnected != null) {
            answer.append(", '");
            answer.append(info.connectivityInfo.isBiconnected);
            answer.append("'");
        }
        if (info.connectivityInfo.articulationPoints != null) {
            answer.append(", '");
            answer.append(info.connectivityInfo.articulationPoints.size());
            answer.append("'");
        }
        if (info.connectivityInfo.bridges != null) {
            answer.append(", '");
            answer.append(info.connectivityInfo.bridges.size());
            answer.append("'");
        }
        if (info.connectivityInfo.connectedComponents != null) {
            answer.append(", '");
            answer.append(info.connectivityInfo.connectedComponents.size());
            answer.append("'");
        }
        if (info.connectivityInfo.blocks != null) {
            answer.append(", '");
            answer.append(info.connectivityInfo.blocks.size());
            answer.append("'");
        }
        if (info.planarityInfo.isPlanar != null) {
            answer.append(", '");
            answer.append(info.planarityInfo.isPlanar);
            answer.append("'");
        }
        if (info.chordalityInfo.isChordal != null) {
            answer.append(", '");
            answer.append(info.chordalityInfo.isChordal);
            answer.append("'");
        }
        if (info.chordalityInfo.chromaticNumber != null) {
            answer.append(", '");
            answer.append(info.chordalityInfo.chromaticNumber);
            answer.append("'");
        }
        if (info.bipartitePartitioningInfo.isBipartite != null) {
            answer.append(", '");
            answer.append(info.bipartitePartitioningInfo.isBipartite);
            answer.append("'");
        }
        return answer.toString();
    }

    public static String getFieldsNames(GraphResearchInfo info) {
        List<String> notNullFields = new ArrayList<>();
        notNullFields.add("graph_id");
        if (info.connectivityInfo.isConnected != null) {
            notNullFields.add("is_connected");
        }
        if (info.connectivityInfo.isBiconnected != null) {
            notNullFields.add("is_biconnected");
        }
        if (info.connectivityInfo.articulationPoints != null) {
            notNullFields.add("articulation_points");
        }
        if (info.connectivityInfo.bridges != null) {
            notNullFields.add("bridges");
        }
        if (info.connectivityInfo.connectedComponents != null) {
            notNullFields.add("connected_components");
        }
        if (info.connectivityInfo.blocks != null) {
            notNullFields.add("blocks");
        }
        if (info.planarityInfo.isPlanar != null) {
            notNullFields.add("is_planar");
        }
        if (info.chordalityInfo.isChordal != null) {
            notNullFields.add("is_chordal");
        }
        if (info.chordalityInfo.chromaticNumber != null) {
            notNullFields.add("chromatic_number");
        }
        if (info.bipartitePartitioningInfo.isBipartite != null) {
            notNullFields.add("is_bipartite");
        }
        StringBuilder fields = new StringBuilder();
        for (int i = 0; i < notNullFields.size(); ++i) {
            if (i == 0) {
                fields.append(notNullFields.get(i));
            } else {
                fields.append(", ").append(notNullFields.get(i));
            }
        }
        return fields.toString();
    }

    public static GraphModel buildGraphFromMatrix(int[][] matrix, GraphMetadata metadata) {
        int n = matrix.length;
        Map<Integer, Vertex> vertexMap = new HashMap<>();
        for (int i = 0; i < n; ++i) {
            vertexMap.put(i, new Vertex(i, i + ""));
        }
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < i; ++j) {
                if (matrix[i][j] == 1) {
                    Edge e = new Edge(vertexMap.get(i), vertexMap.get(j), 1.0, "");
                    edges.add(e);
                }
            }
        }
        return new GraphModel(vertexMap.values().stream().toList(), edges, metadata);
    }
}
