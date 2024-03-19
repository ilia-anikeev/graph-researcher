package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
