package com.graphResearcher.model.graphInfo;

import com.graphResearcher.model.Vertex;

import java.util.List;

public class BipartitePartitioningInfo {
    public Boolean isBipartite;
    public List<List<Vertex>> partitions;
    public Integer chromaticNumber;
    public List<List<Vertex>> coloring;
    public List<Vertex> independentSet;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BipartitePartitioningInfo other)) {
            return false;
        }

        if (isBipartite == other.isBipartite) {
            return true;
        } else if (isBipartite == null || other.isBipartite == null) {
            return false;
        }

        boolean equals = isBipartite.equals(other.isBipartite);
        if (isBipartite) {
            equals &= partitions.equals(other.partitions);
            equals &= chromaticNumber.equals(other.chromaticNumber);
            equals &= coloring.equals(other.coloring);
            equals &= independentSet.equals(other.independentSet);
        }
        return equals;
    }
}
