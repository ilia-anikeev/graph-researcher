package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GraphMetadata {
    public int graphID;
    public String graphName;
    public final boolean isDirected;
    public final boolean isWeighted;
    public final boolean hasSelfLoops;
    public final boolean hasMultipleEdges;

    public GraphMetadata(int graphID, String graphName, boolean isDirected, boolean isWeighted, boolean hasSelfLoops, boolean hasMultipleEdges) {
        this.graphID = graphID;
        this.graphName = graphName;
        this.isDirected = isDirected;
        this.isWeighted = isWeighted;
        this.hasSelfLoops = hasSelfLoops;
        this.hasMultipleEdges = hasMultipleEdges;
    }


    public GraphMetadata(String graphName, boolean isDirected, boolean isWeighted, boolean hasSelfLoops, boolean hasMultipleEdges) {
        this.graphName = graphName;
        this.isDirected = isDirected;
        this.isWeighted = isWeighted;
        this.hasSelfLoops = hasSelfLoops;
        this.hasMultipleEdges = hasMultipleEdges;
    }

    public GraphMetadata(JsonNode json) {
        graphName = json.get("graphName").asText();
        isDirected = json.get("isDirected").asBoolean();
        isWeighted = json.get("isWeighted").asBoolean();
        hasSelfLoops = json.get("hasSelfLoops").asBoolean();
        hasMultipleEdges = json.get("hasMultipleEdges").asBoolean();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GraphMetadata metadata)) {
            return false;
        }
        return this.hasMultipleEdges == metadata.hasMultipleEdges &&
                this.hasSelfLoops == metadata.hasSelfLoops &&
                this.isDirected == metadata.isDirected &&
                this.isWeighted == metadata.isWeighted;
    }

    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        json.put("graphID", graphID);
        json.put("graphName", graphName);
        json.put("isDirected", isDirected);
        json.put("isWeighted", isWeighted);
        json.put("hasSelfLoops", hasSelfLoops);
        json.put("hasMultipleEdges", hasMultipleEdges);

        return json;
    }
}
