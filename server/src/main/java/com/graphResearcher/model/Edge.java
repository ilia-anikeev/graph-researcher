package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class Edge {
    public Vertex source;

    public Vertex target;

    public double weight;

    public String data;

    public Edge(Vertex source, Vertex target, double weight, String data) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.data = data;
    }

    public Edge(JsonNode json) {
        source = new Vertex(json.get("source"));
        target = new Vertex(json.get("target"));
        weight = json.get("weight").asInt();
        data = json.get("data").asText();
    }

    @Override
    public int hashCode() {
        return source.getIndex() + 17 * target.getIndex();
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
        json.put("data", data);
        return json;
    }
}
