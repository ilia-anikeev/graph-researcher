package com.graphResearcher;

import com.graphResearcher.dao.DataBaseManager;
import com.graphResearcher.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
class GraphResearcherApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testDAO1() {
        DataBaseManager db = new DataBaseManager();

        db.createUserGraphArchive(1);

        ArrayList<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex(1, "hi");
        Vertex v2 = new Vertex(2, "hello");
        Vertex v3 = new Vertex(3, "salam");
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge(v1, v2, "aloha"));
        edges.add(new Edge(v2, v3, "buenos noches"));
        edges.add(new Edge(v3, v1, "guten morgen"));

        GraphInfo info = new GraphInfo(false, false, GraphType.SIMPLE_GRAPH);

        db.saveGraph(1, new GraphModel(vertices, edges, info));

        GraphModel g = db.getGraph(1, 1);

        for (int i = 0; i < g.vertices.size(); ++i) {
            assertEquals(vertices.get(i).index, g.vertices.get(i).index);
            assertEquals(vertices.get(i).data, g.vertices.get(i).data);
        }

        for (int i = 0; i < g.edges.size(); ++i) {
            assertEquals(edges.get(i).source.index, g.edges.get(i).source.index);
            assertEquals(edges.get(i).target.index, g.edges.get(i).target.index);
            assertEquals(edges.get(i).data, g.edges.get(i).data);
        }

        assertEquals(info.isDirected, g.info.isDirected);
        assertEquals(info.isWeighted, g.info.isWeighted);
        assertEquals(info.type, g.info.type);

        db.deleteGraph(1, 1);
        db.deleteUserGraphArchive(1);
    }

    @Test
    void testDAO2() {
        DataBaseManager db = new DataBaseManager();

        db.createUserGraphArchive(333);

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
        edges.add(new Edge(v1, v1, "petlya"));
        edges.add(new Edge(v1, v2, "pupupu"));
        edges.add(new Edge(v2, v3, "pupupu1"));
        edges.add(new Edge(v4, v3, "pupupu2"));
        edges.add(new Edge(v4, v3, "multipupupu2"));

        GraphInfo info = new GraphInfo(true, true, GraphType.PSEUDO_GRAPH);

        db.saveGraph(333, new GraphModel(vertices, edges, info));

        GraphModel g = db.getGraph(333, 1);

        for (int i = 0; i < g.vertices.size(); ++i) {
            assertEquals(vertices.get(i).index, g.vertices.get(i).index);
            assertEquals(vertices.get(i).data, g.vertices.get(i).data);
        }

        for (int i = 0; i < g.edges.size(); ++i) {
            assertEquals(edges.get(i).source.index, g.edges.get(i).source.index);
            assertEquals(edges.get(i).target.index, g.edges.get(i).target.index);
            assertEquals(edges.get(i).data, g.edges.get(i).data);
        }

        assertEquals(info.isDirected, g.info.isDirected);
        assertEquals(info.isWeighted, g.info.isWeighted);
        assertEquals(info.type, g.info.type);

        db.deleteGraph(333, 1);
        db.deleteUserGraphArchive(333);
    }

}