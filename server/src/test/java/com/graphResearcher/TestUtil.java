package com.graphResearcher;

import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.GraphResearchInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtil {
    public static void equals(GraphResearchInfo lhs, GraphResearchInfo rhs) {
        assertEquals(lhs.isConnected, rhs.isConnected);
        assertEquals(lhs.isBiconnected, rhs.isBiconnected);
        TestUtil.equals(lhs.bridges, lhs.bridges);
        TestUtil.equals(lhs.articulationPoints, rhs.articulationPoints);
        TestUtil.equals(lhs.connectedComponents, rhs.connectedComponents);
        TestUtil.equals(lhs.blocks, rhs.blocks);

        assertEquals(lhs.isPlanar, rhs.isPlanar);
        if (lhs.isPlanar != null && !lhs.isPlanar) {
            assertEquals(lhs.kuratowskiSubgraph, rhs.kuratowskiSubgraph);
        }

        assertEquals(lhs.isChordal, rhs.isChordal);
        if (lhs.isChordal != null && lhs.isChordal) {
            TestUtil.equals(lhs.perfectEliminationOrder, rhs.perfectEliminationOrder);
            assertEquals(lhs.chromaticNumber, rhs.chromaticNumber);
            TestUtil.equals(lhs.coloring, rhs.coloring);
            TestUtil.equals(lhs.maxClique, rhs.maxClique);
            TestUtil.equals(lhs.independentSet, rhs.independentSet);
            TestUtil.equals(lhs.minimalVertexSeparator, rhs.minimalVertexSeparator);
        }
    }

    public static void equals(List<?> lhs, List<?> rhs) {
        assertEquals(lhs.size(), rhs.size());
        for (int i = 0; i < lhs.size(); ++i) {
            if (lhs.get(i) instanceof GraphModel && rhs.get(i) instanceof GraphModel) {
                TestUtil.equals((GraphModel)lhs.get(i), (GraphModel)rhs.get(i));
            } else if (lhs.get(i) instanceof List<?> && rhs.get(i) instanceof List<?>){
                assertEquals(lhs.get(i), rhs.get(i));
            }
        }
    }

    public static void equals(GraphModel lhs, GraphModel rhs) {
        assertEquals(lhs.getMetadata(), rhs.getMetadata());

        TestUtil.equals(lhs.getEdges(), rhs.getEdges());

        TestUtil.equals(lhs.getVertices(), rhs.getVertices());
    }
}
