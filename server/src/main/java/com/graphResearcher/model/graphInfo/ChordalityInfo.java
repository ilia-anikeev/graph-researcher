package com.graphResearcher.model.graphInfo;

import com.graphResearcher.model.Vertex;

import java.util.List;

public class ChordalityInfo {
    public Boolean isChordal;
    public List<Vertex> perfectEliminationOrder;
    public Integer chromaticNumber;
    public List<List<Vertex>> coloring;
    public List<Vertex> maxClique;
    public List<Vertex> independentSet;
    public List<List<Vertex>> minimalVertexSeparator;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChordalityInfo other)) {
            return false;
        }
        if (isChordal == other.isChordal) {
            return true;
        }
        if (isChordal == null || other.isChordal == null) {
            return false;
        }
        boolean equals = isChordal.equals(other.isChordal);

        if (isChordal) {
            equals &= perfectEliminationOrder.equals(other.perfectEliminationOrder);
            equals &= chromaticNumber.equals(other.chromaticNumber);
            equals &= coloring.equals(other.coloring);
            equals &= maxClique.equals(other.maxClique);
            equals &= independentSet.equals(other.independentSet);
            equals &= minimalVertexSeparator.equals(other.minimalVertexSeparator);
        }
        return equals;
    }
}
