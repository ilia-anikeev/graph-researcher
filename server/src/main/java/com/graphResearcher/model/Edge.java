package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Edge {
    private Vertex source;

    private Vertex target;

    private double weight;

    private String data;

    public Edge(JsonNode json) {
        source = new Vertex(json.get("source"));
        target = new Vertex(json.get("target"));
        weight = json.get("weight").asInt();
        data = json.get("data").asText();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Edge edge)) {
            return false;
        }
        return this.source.equals(edge.source) && this.target.equals(edge.target) && this.data.equals(edge.data);
    }

    public ObjectNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.set("source", source.toJson());
        json.set("target", target.toJson());
        json.put("weight", weight);
        json.put("data", data);
        return json;
    }
}