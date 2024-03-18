package com.graphResearcher.model;

public class GraphInfo {
    public final boolean isDirected;
    public final boolean isWeighted;
    public final GraphType type;
    public GraphInfo(boolean isDirected, boolean isWeighted, GraphType type) {
        this.isDirected = isDirected;
        this.isWeighted = isWeighted;
        this.type = type;
    }
}
