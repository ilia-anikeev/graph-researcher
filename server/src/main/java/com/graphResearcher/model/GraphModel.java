package com.graphResearcher.model;

import java.util.ArrayList;

public class GraphModel {
    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    public GraphInfo info;

    public GraphModel(ArrayList<Vertex> vertices, ArrayList<Edge> edges, GraphInfo info) {
        this.vertices = vertices;
        this.edges = edges;
        this.info = info;
    }
}
