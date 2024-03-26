package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GraphInfo {

    @JsonProperty("isDirected")
    public final boolean isDirected;

    @JsonProperty("isWeighted")
    public final boolean isWeighted;

    @JsonProperty("hasSelfLoops")
    public final boolean hasSelfLoops;

    @JsonProperty("hasMultipleEdges")
    public final boolean hasMultipleEdges;


    public GraphInfo(boolean isDirected, boolean isWeighted, boolean hasSelfLoops, boolean hasMultipleEdges) {
        this.isDirected = isDirected;
        this.isWeighted = isWeighted;
        this.hasSelfLoops = hasSelfLoops;
        this.hasMultipleEdges = hasMultipleEdges;
    }

    public GraphInfo(JsonNode json) {
        isDirected = json.get("isDirected").asBoolean();
        isWeighted = json.get("isWeighted").asBoolean();
        hasSelfLoops = json.get("hasSelfLoops").asBoolean();
        hasMultipleEdges = json.get("hasMultipleEdges").asBoolean();
    }

    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.put("isDirected", isDirected);
        json.put("isWeighted", isWeighted);
        json.put("hasSelfLoops", hasSelfLoops);
        json.put("hasMultipleEdges", hasMultipleEdges);

        return json;
    }
}
