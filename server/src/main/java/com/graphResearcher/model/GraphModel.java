package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


public class GraphModel {

    @JsonProperty("vertices")
    public ArrayList<Vertex> vertices;

    @JsonProperty("edges")
    public ArrayList<Edge> edges;

    @JsonProperty("info")
    public GraphInfo info;

    public GraphModel(ArrayList<Vertex> vertices, ArrayList<Edge> edges, GraphInfo info) {
        this.vertices = vertices;
        this.edges = edges;
        this.info = info;
    }

    public ArrayList<Vertex> getVertices(){
        return this.vertices;
    }

    public ArrayList<Edge> getEdges(){
        return this.edges;
    }
}
