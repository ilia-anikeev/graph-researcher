package com.graphResearcher;

import com.graphResearcher.model.*;
import com.graphResearcher.repository.DataBaseManager;
import com.graphResearcher.service.GraphResearchService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@SpringBootTest
class GraphResearcherApplicationTests {
    private GraphModel graph1;
    private GraphModel graph2;
    GraphResearcherApplicationTests() {
        buildGraph1();
        buildGraph2();
    }
    @Test
    void saveGraphTest1() {
        int userID = 1;
        DataBaseManager db = new DataBaseManager();
        db.reloadDB();
        db.createUser(userID);

        int graphID = db.saveGraph(userID, graph1);
        GraphModel receivedGraph = db.getGraph(graphID);
        assertEquals(graph1, receivedGraph);

        db.deleteUser(userID);
    }

    @Test
    void saveGraphTest2() {
        int userID = 2;
        DataBaseManager db = new DataBaseManager();
        db.reloadDB();
        db.createUser(userID);

        int graphID = db.saveGraph(userID, graph2);
        GraphModel receivedGraph = db.getGraph(graphID);
        assertEquals(graph2, receivedGraph);

        db.deleteUser(userID);
    }

    @Test
    void researchAndSave1() {
        int userID = 1;
        GraphResearchService service = new GraphResearchService();
        DataBaseManager db = new DataBaseManager();
        db.reloadDB();
        db.createUser(userID);

        int graphID = db.saveGraph(userID, graph1);
        GraphResearchInfo info = service.research(graph1);

        db.saveResearchInfo(userID, graphID, info);

        GraphResearchInfo receivedInfo = db.getResearchInfo(graphID);

        TestUtil.equals(info, receivedInfo);

        db.deleteUser(userID);
    }

    @Test
    void researchAndSave2() {
        int userID = 2;
        GraphResearchService service = new GraphResearchService();
        DataBaseManager db = new DataBaseManager();
        db.reloadDB();
        db.createUser(userID);

        int graphID = db.saveGraph(userID, graph2);
        GraphResearchInfo info = service.research(graph2);

        db.saveResearchInfo(userID, graphID, info);

        GraphResearchInfo receivedInfo = db.getResearchInfo(graphID);

        TestUtil.equals(info, receivedInfo);

        db.deleteUser(userID);
    }

    @Test
    void researchAndSave3() {
        int userID = 3;
        GraphResearchService service = new GraphResearchService();
        DataBaseManager db = new DataBaseManager();
        db.reloadDB();
        db.createUser(userID);

        int graphID1 = db.saveGraph(userID, graph1);
        GraphResearchInfo info1 = service.research(graph1);

        db.saveResearchInfo(userID, graphID1, info1);

        GraphResearchInfo receivedInfo1 = db.getResearchInfo(graphID1);

        TestUtil.equals(info1, receivedInfo1);

        int graphID2 = db.saveGraph(userID, graph2);
        GraphResearchInfo info2 = service.research(graph2);

        db.saveResearchInfo(userID, graphID2, info2);

        GraphResearchInfo receivedInfo2 = db.getResearchInfo(graphID2);

        TestUtil.equals(info2, receivedInfo2);
        db.deleteUser(userID);
    }


    private void buildGraph1() {
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

        GraphMetadata info = new GraphMetadata(false, false, false, false);
        graph1 = new GraphModel(vertices, edges, info);
    }
    private void buildGraph2() {
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

        GraphMetadata metadata = new GraphMetadata(true, true, true, false);
        graph2 = new GraphModel(vertices, edges, metadata);
    }
}
