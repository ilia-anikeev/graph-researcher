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
import java.util.List;

public class ParsingUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectNode graphsListToJson(List<GraphModel> list) {
        ArrayNode arrayNode = list.stream().map(GraphModel::toJson).collect(
                mapper::createArrayNode,
                ArrayNode::add,
                ArrayNode::addAll
        );
        ObjectNode node = mapper.createObjectNode();
        node.set("graphs", arrayNode);
        return node;
    }

    public static ObjectNode vertices2DListToJson(List<List<Vertex>> list) {
        ArrayNode arrayNode = list.stream().map(ParsingUtil::verticesListToJson).collect(
                mapper::createArrayNode,
                ArrayNode::add,
                ArrayNode::addAll
        );
        ObjectNode node = mapper.createObjectNode();
        node.set("vertices2D", arrayNode);
        return node;
    }

    public static ObjectNode edgesListToJson(List<Edge> list) {
        ArrayNode arrayNode = edgesListToJsonArray(list);
        ObjectNode node = mapper.createObjectNode();
        node.set("edges", arrayNode);
        return node;
    }

    public static ArrayNode edgesListToJsonArray(List<Edge> list) {
        return list.stream().map(Edge::toJson).collect(
                mapper::createArrayNode,
                ArrayNode::add,
                ArrayNode::addAll
        );
    }

    public static ObjectNode verticesListToJson(List<Vertex> list) {
        ArrayNode arrayNode = verticesListToJsonArray(list);
        ObjectNode node = mapper.createObjectNode();
        node.set("vertices", arrayNode);
        return node;
    }

    public static ArrayNode verticesListToJsonArray(List<Vertex> list) {
        return list.stream().map(Vertex::toJson).collect(
                mapper::createArrayNode,
                ArrayNode::add,
                ArrayNode::addAll
        );
    }

    public static List<Edge> jsonToListEdges(JsonNode json) {
        ArrayNode jsonArray = (ArrayNode)json.get("edges");
        ArrayList<Edge> edges = new ArrayList<>();
        for (var e: jsonArray) {
            edges.add(new Edge(e));
        }
        return edges;
    }

    public static List<Vertex> jsonToListVertices(JsonNode json) {
        ArrayNode jsonArray = (ArrayNode)json.get("vertices");
        ArrayList<Vertex> vertices = new ArrayList<>();
        for (var v: jsonArray) {
            vertices.add(new Vertex(v));
        }
        return vertices;
    }

    public static List<GraphModel> jsonToListGraphs(JsonNode json) {
        ArrayNode jsonArray = (ArrayNode)json.get("graphs");
        List<GraphModel> graphs = new ArrayList<>();
        for (var v: jsonArray) {
            graphs.add(new GraphModel(v));
        }
        return graphs;
    }

    public static List<List<Vertex>> jsonTo2DListVertices(JsonNode json) {
        ArrayNode jsonArray = (ArrayNode)json.get("vertices2D");
        List<List<Vertex>> vertices2D = new ArrayList<>();
        for (var v: jsonArray) {
            vertices2D.add(jsonToListVertices(v));
        }
        return vertices2D;
    }

    public static GraphResearchInfo resultSetToGraphResearchInfo(ResultSet rs) throws SQLException, JsonProcessingException {
        rs.next();
        GraphResearchInfo info = new GraphResearchInfo();

        info.isConnected = rs.getBoolean("connectivity");
        if (rs.wasNull()) {
            info.isConnected = null;
        }
        ObjectMapper objectMapper = new ObjectMapper();


        String arg2 = rs.getString("bridges");
        if (arg2 != null) {
            info.bridges = ParsingUtil.jsonToListEdges(objectMapper.readTree(arg2));
        }

        String arg3 = rs.getString("articulation_points");
        if (arg3 != null) {
            info.articulationPoints = ParsingUtil.jsonToListVertices(objectMapper.readTree(arg3));
        }

        String arg4 = rs.getString("connected_components");
        if (arg4 != null) {
            info.connectedComponents = ParsingUtil.jsonTo2DListVertices(objectMapper.readTree(arg4));
        }

        return info;
    }


    private static final int EMPTY_MASK = 0;
    private static final int IS_DIRECTED = 1;
    private static final int IS_WEIGHTED = 1 << 1;
    private static final int HAS_MULTIPLE_EDGES = 1 << 2;
    private static final int HAS_SELF_LOOPS = 1 << 3;

    private static Graph<Vertex, WeightedEdge> getEmptyGraph(GraphModel graphModel) {
        int mask = EMPTY_MASK;
        if (graphModel.metadata.isDirected) {
            mask |= IS_DIRECTED;
        }
        if (graphModel.metadata.isWeighted) {
            mask |= IS_WEIGHTED;
        }
        if (graphModel.metadata.hasMultipleEdges) {
            mask |= HAS_MULTIPLE_EDGES;
        }
        if (graphModel.metadata.hasSelfLoops) {
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
            if (graphModel.metadata.isWeighted) {
                g.setEdgeWeight(e.getSource(), e.getTarget(), e.getWeight());
            }
        }
        return g;
    }
}
