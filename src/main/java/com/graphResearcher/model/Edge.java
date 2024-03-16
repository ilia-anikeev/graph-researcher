package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Edge {

    @JsonProperty("source")
    public Vertex source;

    @JsonProperty("target")
    public Vertex target;

    @JsonProperty("data")
    public String data;

    @JsonProperty("weight")
    public int weight;

    public Edge() {
    }

    public Edge(Vertex source, Vertex target, String data, int weight) {
        this.source = source;
        this.target = target;
        this.data = data;
        this.weight = weight;
    }

    public Vertex getSourceVertex() {
        return this.source;
    }

    public Vertex getTargetVertex() {
        return this.target;
    }
}