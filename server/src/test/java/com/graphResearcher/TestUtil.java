package com.graphResearcher;

import com.graphResearcher.model.Edge;
import com.graphResearcher.model.GraphResearchInfo;
import com.graphResearcher.model.Vertex;

import java.util.List;
import java.util.Map;

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
            assertNotNull(lhs.perfectEliminationOrder);
            assertNotNull(rhs.perfectEliminationOrder);
            assertEquals(lhs.perfectEliminationOrder.size(), rhs.perfectEliminationOrder.size());

            TestUtil.equals(lhs.perfectEliminationOrder, rhs.perfectEliminationOrder);

            assertEquals(lhs.chromaticNumber, rhs.chromaticNumber);

            assertEquals(lhs.coloring.size(), rhs.coloring.size());
            for (Map.Entry<Vertex, Integer> entry : lhs.coloring.entrySet()) {
                assertTrue(rhs.coloring.containsKey(entry.getKey()));
                assertEquals(entry.getValue(), rhs.coloring.get(entry.getKey()));
            }
            assertEquals(lhs.maxClique, rhs.maxClique);
            TestUtil.equals(lhs.independentSet, rhs.independentSet);
            TestUtil.equals(lhs.minimalVertexSeparator, rhs.minimalVertexSeparator);
        }
    }

    public static void equals(List<?> lhs, List<?> rhs) {
        assertEquals(lhs.size(), rhs.size());
        for (int i = 0; i < lhs.size(); ++i) {
            assertEquals(lhs.get(i), rhs.get(i));
        }
    }
}
