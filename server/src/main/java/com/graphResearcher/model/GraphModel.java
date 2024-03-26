package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.util.ParsingUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class GraphModel {

    @JsonProperty("vertices")
    private List<Vertex> vertices;

    @JsonProperty("edges")
    private List<Edge> edges;

    @JsonProperty("info")
    public final GraphMetadata info;

    public GraphModel(List<Vertex> vertices, List<Edge> edges, GraphMetadata info) {
        this.vertices = vertices;
        this.edges = edges;
        this.info = info;
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

        info = new GraphMetadata(json.get("info"));
    }

    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.set("vertices", ParsingUtil.verticesListToJsonArray(vertices));
        json.set("edges", ParsingUtil.edgesListToJsonArray(edges));
        json.set("info", info.toJson());
        return json;
    }
}
