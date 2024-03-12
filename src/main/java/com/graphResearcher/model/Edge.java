package com.graphResearcher.model;

public class Edge {
    public int source;
    public int target;
    public String data = "";

    public Edge(int source, int target, String data) {
        this.source = source;
        this.target = target;
        this.data = data;
    }
}
