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
        if (json.get("data") != null)
            data = json.get("data").asText();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge other)) {
            return false;
        }
        return this.source.equals(other.source) && this.target.equals(other.target);
    }

    public ObjectNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.set("source", source.toJson());
        json.set("target", target.toJson());
        json.put("weight", weight);
        if (data != null) json.put("data", data);
        return json;
    }
}
