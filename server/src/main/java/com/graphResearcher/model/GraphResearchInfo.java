package com.graphResearcher.model;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GraphResearchInfo {
    public boolean connectivity;
    public Set<Edge> bridges;
    public Set<Vertex> articulationPoints;

    public List<Set<Vertex>> connectedComponents;
    public Set<Graph<Vertex, Edge>> blocks;

}