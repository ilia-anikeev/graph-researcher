package com.graphResearcher.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.model.Edge;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.GraphResearchInfo;
import com.graphResearcher.model.Vertex;

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

        info.connectivity = rs.getBoolean("connectivity");
        if (rs.wasNull()) {
            info.connectivity = null;
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
}
