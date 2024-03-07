package com.graphResearcher;

import com.graphResearcher.dao.DataBaseManager;
import com.graphResearcher.model.Edge;
import com.graphResearcher.model.Vertex;
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
    void testDAO1(){
        DataBaseManager db = new DataBaseManager();

        ArrayList<Vertex> vertices = new ArrayList<>(Arrays.asList(new Vertex(1), new Vertex(2)));
        ArrayList<Edge> edges = new ArrayList<>(Arrays.asList(new Edge(1, 2), new Edge(2, 1)));

        db.saveGraph(0, 1, vertices, edges);

        ArrayList<Vertex> outputVertices = db.getVertices(0, 1);

        for (int i = 0; i < vertices.size(); ++i) {
            assertEquals(vertices.get(i).index, outputVertices.get(i).index);
        }

        ArrayList<Edge> outputEdges = db.getEdges(0, 1);

        for (int i = 0; i < edges.size(); ++i) {
            assertEquals(edges.get(i).source, outputEdges.get(i).source);
            assertEquals(edges.get(i).target, outputEdges.get(i).target);
        }

        db.deleteGraph(0, 1);
    }

    @Test
    void testDAO2() {
        DataBaseManager db = new DataBaseManager();

        ArrayList<Vertex> vertices = new ArrayList<>(Arrays.asList(new Vertex(1), new Vertex(2), new Vertex(3)));
        ArrayList<Edge> edges = new ArrayList<>(Arrays.asList(new Edge(1, 2), new Edge(2, 3), new Edge(3, 1)));

        db.saveGraph(0, 1, vertices, edges);

        ArrayList<Vertex> outputVertices = db.getVertices(0, 1);

        for (int i = 0; i < vertices.size(); ++i) {
            assertEquals(vertices.get(i).index, outputVertices.get(i).index);
        }

        ArrayList<Edge> outputEdges = db.getEdges(0, 1);

        for (int i = 0; i < edges.size(); ++i) {
            assertEquals(edges.get(i).source, outputEdges.get(i).source);
            assertEquals(edges.get(i).target, outputEdges.get(i).target);
        }

        db.deleteGraph(0, 1);
    }

}
