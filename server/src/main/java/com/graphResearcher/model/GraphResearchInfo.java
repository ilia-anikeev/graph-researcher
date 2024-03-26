package com.graphResearcher.model;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GraphResearchInfo {
    public Boolean connectivity;
    public List<Edge> bridges;
    public List<Vertex> articulationPoints;
    public List<List<Vertex>> connectedComponents;
    public List<GraphModel> blocks;
}