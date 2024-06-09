package com.graphResearcher.model.graphInfo;

import com.graphResearcher.model.Edge;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.Vertex;

import java.util.List;
import java.util.Map;

public class PlanarityInfo {
    public Boolean isPlanar;
    public Map<Vertex, List<Edge>> embedding; //TODO
    public GraphModel kuratowskiSubgraph;

    public boolean equals(Object o) {
        if (!(o instanceof PlanarityInfo other)) {
            return false;
        }
        boolean equals = true;
        if (isPlanar == other.isPlanar) {
            return true;
        }
        if (isPlanar == null || other.isPlanar == null) {
            return false;
        }
        if (!isPlanar) {
            equals &= kuratowskiSubgraph.equals(other.kuratowskiSubgraph);
        }
        return equals;
    }
}