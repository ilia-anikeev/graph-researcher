package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    @JsonProperty("source")
    private Vertex source;

    @JsonProperty("target")
    private Vertex target;

    @JsonProperty("weight")
    private double weight;

    @JsonProperty("data")
    private String data;

    public Edge(JsonNode json) {
        source = new Vertex(json.get("source"));
        target = new Vertex(json.get("target"));
        weight = json.get("weight").asInt();
        data = json.get("data").asText();
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
