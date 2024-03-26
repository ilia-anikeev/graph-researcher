package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.util.ParsingUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class GraphModel {

    private final List<Vertex> vertices;

    private final List<Edge> edges;

    public final GraphMetadata metadata;

    public GraphModel(List<Vertex> vertices, List<Edge> edges, GraphMetadata metadata) {
        this.vertices = vertices;
        this.edges = edges;
        this.metadata = metadata;
    }

    public GraphModel(JsonNode json) {
        var it = json.get("vertices").elements();
        List<Vertex> vertexList = new ArrayList<>();
        while (it.hasNext()) {
            vertexList.add(new Vertex(it.next()));
        }
        vertices = vertexList;

        var it1 = json.get("edges").elements();
        List<Edge> edgesList = new ArrayList<>();
        while (it1.hasNext()) {
            edgesList.add(new Edge(it1.next()));
        }

        edges = edgesList;

        metadata = new GraphMetadata(json.get("info"));
    }

    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.set("vertices", ParsingUtil.verticesListToJsonArray(vertices));
        json.set("edges", ParsingUtil.edgesListToJsonArray(edges));
        json.set("info", metadata.toJson());
        return json;
    }
}

/*
{
    "vertices": [{"index": 1, "data": "aloha"}],
    "edges": [],
    "info": {}
}

 */
