package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;


@Getter
public class GraphModel {

    @JsonProperty("vertices")
    private ArrayList<Vertex> vertices;

    @JsonProperty("edges")
    private ArrayList<Edge> edges;

    @JsonProperty("info")
    public final GraphInfo info;

    public GraphModel(ArrayList<Vertex> vertices, ArrayList<Edge> edges, GraphInfo info) {
        this.vertices = vertices;
        this.edges = edges;
        this.info = info;
    }
}
