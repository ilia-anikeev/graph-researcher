package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.util.Converter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class GraphModel {

    private final List<Vertex> vertices;

    private final List<Edge> edges;

    private final GraphMetadata metadata;

    public GraphModel(List<Vertex> vertices, List<Edge> edges, GraphMetadata metadata) {
        this.vertices = vertices;
        this.edges = edges;
        this.metadata = metadata;
    }

    public GraphModel(JsonNode json) {
        var vertexListIt = json.get("vertices").elements();
        List<Vertex> vertexList = new ArrayList<>();
        while (vertexListIt.hasNext()) {
            vertexList.add(new Vertex(vertexListIt.next()));
        }
        vertices = vertexList;

        var edgesListIt = json.get("edges").elements();
        List<Edge> edgesList = new ArrayList<>();
        while (edgesListIt.hasNext()) {
            edgesList.add(new Edge(edgesListIt.next()));
        }
        edges = edgesList;

        metadata = new GraphMetadata(json.get("info"));
    }

    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.set("vertices", Converter.verticesListToJsonArray(vertices));
        json.set("edges", Converter.edgesListToJsonArray(edges));
        json.set("info", metadata.toJson());
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GraphModel other)) {
            return false;
        }
        boolean equals = true;
        equals &= getMetadata().equals(other.getMetadata());

        equals &= getEdges().equals(other.getEdges());

        equals &= getVertices().equals(other.getVertices());
        return equals;
    }
}
