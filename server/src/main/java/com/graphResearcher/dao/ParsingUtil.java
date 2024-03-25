package com.graphResearcher.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.model.Edge;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.Vertex;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Collection;

public class ParsingUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    public GraphModel graphToGraphModel(Graph<Vertex, Edge> graph) {
        return null;
    }

    public static ObjectNode edgesCollectionToJson(Collection<Edge> collection) {
        ArrayNode arrayNode = collection.stream().map(Edge::toJson).collect(
                mapper::createArrayNode,
                ArrayNode::add,
                ArrayNode::addAll
        );
        ObjectNode node = mapper.createObjectNode();
        node.set("edges", arrayNode);
        return node;
    }

    public static ObjectNode verticesCollectionToJson(Collection<Vertex> collection) {
        ArrayNode arrayNode = collection.stream().map(Vertex::toJson).collect(
                mapper::createArrayNode,
                ArrayNode::add,
                ArrayNode::addAll
        );
        ObjectNode node = mapper.createObjectNode();
        node.set("vertices", arrayNode);
        return node;
    }

    public static ArrayList<Edge> jsonToListEdges(JsonNode json) {
        ArrayNode jsonArray = (ArrayNode)json.get("edges");
        ArrayList<Edge> edges = new ArrayList<>();
        for (var e: jsonArray) {
            edges.add(new Edge(e));
        }
        return edges;
    }

    public static ArrayList<Vertex> jsonToListVertices(JsonNode json) {
        ArrayNode jsonArray = (ArrayNode)json.get("vertices");
        ArrayList<Vertex> vertices = new ArrayList<>();
        for (var v: jsonArray) {
            vertices.add(new Vertex(v));
        }
        return vertices;
    }
}
