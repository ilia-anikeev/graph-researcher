package com.graphResearcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.dao.DataBaseManager;
import com.graphResearcher.dao.ParsingUtil;
import com.graphResearcher.model.*;
import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
class GraphResearcherApplicationTests {

    @Test
    void contextLoads() {
        var v1 = new Vertex(1, "");
        var e = new Edge(v1, v1, 1.0, "a");

        ArrayList<Vertex> st = new ArrayList<>();
        st.add(v1);
        st.add(v1);
        var x = ParsingUtil.verticesCollectionToJson(st);

        var s = ParsingUtil.jsonToListVertices(x);
        for (int i = 0; i < s.size(); ++i) {
            assertEquals(s.get(i), st.get(i));
        }
        System.err.println(s);
    }

    @Test
    void testDAO1() {
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
    void testDAO2() {
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
