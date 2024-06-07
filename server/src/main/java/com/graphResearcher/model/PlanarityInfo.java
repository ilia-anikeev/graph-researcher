package com.graphResearcher.model;

import java.util.List;
import java.util.Map;

public class PlanarityInfo {
    public Boolean isPlanar;
    public Map<Vertex, List<Edge>> embedding; //TODO
    public GraphModel kuratowskiSubgraph;
}
