package com.graphResearcher.model;

import org.jgrapht.graph.DefaultWeightedEdge;

public class WeightedEdge extends DefaultWeightedEdge {
    public Edge toEdge() {
        return new Edge((Vertex)getSource(), (Vertex)getTarget(), getWeight(), "");
    }
}
