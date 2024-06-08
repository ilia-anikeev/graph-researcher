package com.graphResearcher.model.graphInfo;

import com.graphResearcher.model.Edge;
import com.graphResearcher.model.Vertex;

import java.util.List;

public class ConnectivityInfo {
    public Boolean isConnected;
    public Boolean isBiconnected;
    public List<Vertex> articulationPoints;
    public List<Edge> bridges;
    public List<List<Vertex>> connectedComponents;
    public List<List<Vertex>> blocks;

    public boolean equals(Object o) {
        if (!(o instanceof ConnectivityInfo other)) {
            return false;
        }
        boolean equals = true;
        equals &= bridges.equals(other.bridges);

        equals &= isConnected.equals(other.isConnected);
        equals &= isBiconnected.equals(other.isBiconnected);
        equals &= bridges.equals(other.bridges);

        equals &= articulationPoints.equals(other.articulationPoints);
        equals &= connectedComponents.equals(other.connectedComponents);
        equals &= blocks.equals(other.blocks);
        return equals;
    }
}
