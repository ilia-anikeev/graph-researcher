package com.graphResearcher.model;

public class Edge {
    public Vertex source;
    public Vertex target;
    public String data = "";

    public Edge(Vertex source, Vertex target, String data) {
        this.source = source;
        this.target = target;
        this.data = data;
    }
}
