package com.graphResearcher;

import com.graphResearcher.dao.DataBaseManager;
import com.graphResearcher.model.*;
import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
class GraphResearcherApplicationTests {

    @Test
    void saveGraphInfoTest1() {
        Vertex v1 = new Vertex(1, "a");
        Vertex v2 = new Vertex(2, "b");
        Vertex v3 = new Vertex(3, "c");
        Vertex v4 = new Vertex(4, "d");
        List<Vertex> vertices = List.of(v1, v2, v3, v4);

        Edge e1 = new Edge(v1, v2, 1.0, "aba");
        Edge e2 = new Edge(v2, v3, 1.0, "abaaa");
        Edge e3 = new Edge(v3, v1, 1.0, "abasdf");
        Edge e4 = new Edge(v1, v4, 1.0, "abou2");
        List<Edge> edges = List.of(e1, e2, e3, e4);

        GraphInfo info = new GraphInfo(false, false, false, false);
        GraphModel model = new GraphModel(vertices, edges, info);



        GraphResearchInfo researchInfo = new GraphResearchInfo();
        researchInfo.connectivity = true;
        researchInfo.bridges = List.of(e4);
        researchInfo.articulationPoints = List.of(v1);
        researchInfo.connectedComponents = List.of(vertices);
        researchInfo.blocks = List.of(model);

        DataBaseManager db = new DataBaseManager();
        db.createUser(1);
        db.saveResearchInfo(1, 1, researchInfo);
        GraphResearchInfo researchInfoDB = db.getResearchInfo(1, 1);

        assertEquals(researchInfo.connectivity, researchInfoDB.connectivity);

        for (int i = 0; i < researchInfoDB.bridges.size(); ++i) {
            assertEquals(researchInfo.bridges.get(i), researchInfoDB.bridges.get(i));
        }

        for (int i = 0; i < researchInfoDB.articulationPoints.size(); ++i) {
            assertEquals(researchInfo.articulationPoints.get(i), researchInfoDB.articulationPoints.get(i));
        }

        for (int i = 0; i < researchInfoDB.connectedComponents.size(); ++i) {
            for (int j = 0; j < researchInfoDB.connectedComponents.get(i).size(); ++j) {
                assertEquals(researchInfo.connectedComponents.get(i).get(j), researchInfoDB.connectedComponents.get(i).get(j));
            }
        }
        assertEquals(researchInfo.blocks.getFirst().getEdges().size(), researchInfoDB.blocks.getFirst().getEdges().size());
        for (int i = 0; i < researchInfoDB.blocks.size(); ++i) {
            GraphModel g1 = researchInfo.blocks.get(i);
            GraphModel g2 = researchInfoDB.blocks.get(i);

            for (int j = 0; j < g1.getVertices().size(); ++j) {
                assertEquals(g1.getVertices().get(j), g2.getVertices().get(j));
            }

            for (int j = 0; j < g1.getEdges().size(); ++j) {
                assertEquals(g1.getEdges().get(j), g2.getEdges().get(j));
            }

            assertEquals(g1.getInfo().isDirected, g2.getInfo().isDirected);
            assertEquals(g1.getInfo().isWeighted, g2.getInfo().isWeighted);
            assertEquals(g1.getInfo().hasSelfLoops, g2.getInfo().hasSelfLoops);
            assertEquals(g1.getInfo().hasMultipleEdges, g2.getInfo().hasMultipleEdges);
        }
        db.deleteUser(1);
    }

    @Test
    void saveGraphInfoTest2() {
        GraphResearchInfo researchInfo = new GraphResearchInfo();
        researchInfo.connectivity = null;
        researchInfo.bridges = null;
        researchInfo.articulationPoints = null;
        researchInfo.connectedComponents = null;
        researchInfo.blocks = null;

        DataBaseManager db = new DataBaseManager();
        db.createUser(1);
        db.saveResearchInfo(1, 1, researchInfo);
        GraphResearchInfo researchInfoDB = db.getResearchInfo(1, 1);

        assertEquals(researchInfo.connectivity, researchInfoDB.connectivity);
        assertEquals(researchInfo.bridges, researchInfoDB.bridges);
        assertEquals(researchInfo.articulationPoints, researchInfoDB.articulationPoints);
        assertEquals(researchInfo.connectedComponents, researchInfoDB.connectedComponents);
        assertEquals(researchInfo.blocks, researchInfoDB.blocks);
        db.deleteUser(1);
    }


    @Test
    void saveGraphTest1() {
        DataBaseManager db = new DataBaseManager();

        db.createUser(1);


        ArrayList<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex(1, "hi");
        Vertex v2 = new Vertex(2, "hello");
        Vertex v3 = new Vertex(3, "salam");
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge(v1, v2, 1.0, "aloha"));
        edges.add(new Edge(v2, v3, 1.0, "buenos noches"));
        edges.add(new Edge(v3, v1, 1.0, "guten morgen"));

        GraphInfo info = new GraphInfo(false, false, false, false);

        db.saveGraph(1, new GraphModel(vertices, edges, info));

        GraphModel g = db.getGraph(1, 1);

        for (int i = 0; i < g.getVertices().size(); ++i) {
            assertEquals(vertices.get(i).getIndex(), g.getVertices().get(i).getIndex());
            assertEquals(vertices.get(i).getData(), g.getVertices().get(i).getData());
        }

        for (int i = 0; i < g.getEdges().size(); ++i) {
            assertEquals(edges.get(i).getSource().getIndex(), g.getEdges().get(i).getSource().getIndex());
            assertEquals(edges.get(i).getTarget().getIndex(), g.getEdges().get(i).getTarget().getIndex());
            assertEquals(edges.get(i).getData(), g.getEdges().get(i).getData());
        }

        assertEquals(info.isDirected, g.info.isDirected);
        assertEquals(info.isWeighted, g.info.isWeighted);
        assertEquals(info.hasSelfLoops, g.info.hasSelfLoops);
        assertEquals(info.hasMultipleEdges, g.info.hasMultipleEdges);

        db.deleteUser(1);
    }

    @Test
    void saveGraphTest2() {
        DataBaseManager db = new DataBaseManager();

        db.createUser(333);

        ArrayList<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex(1, "pu1");
        Vertex v2 = new Vertex(2, "pu2");
        Vertex v3 = new Vertex(3, "pu3");
        Vertex v4 = new Vertex(4, "pu3");
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
        vertices.add(v4);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge(v1, v1, 1.0, "petlya"));
        edges.add(new Edge(v1, v2, 1.0, "pupupu"));
        edges.add(new Edge(v2, v3, 1.0, "pupupu1"));
        edges.add(new Edge(v4, v3, 1.0, "pupupu2"));
        edges.add(new Edge(v4, v3, 1.0, "multipupupu2"));

        GraphInfo info = new GraphInfo(true, true, true, false);

        db.saveGraph(333, new GraphModel(vertices, edges, info));

        GraphModel g = db.getGraph(333, 1);

        for (int i = 0; i < g.getVertices().size(); ++i) {
            assertEquals(vertices.get(i).getIndex(), g.getVertices().get(i).getIndex());
            assertEquals(vertices.get(i).getData(), g.getVertices().get(i).getData());
        }

        for (int i = 0; i < g.getEdges().size(); ++i) {
            assertEquals(edges.get(i).getSource().getIndex(), g.getEdges().get(i).getSource().getIndex());
            assertEquals(edges.get(i).getTarget().getIndex(), g.getEdges().get(i).getTarget().getIndex());
            assertEquals(edges.get(i).getData(), g.getEdges().get(i).getData());
        }

        assertEquals(info.isDirected, g.info.isDirected);
        assertEquals(info.isWeighted, g.info.isWeighted);
        assertEquals(info.hasSelfLoops, g.info.hasSelfLoops);
        assertEquals(info.hasMultipleEdges, g.info.hasMultipleEdges);

        db.deleteUser(333);
    }
}
