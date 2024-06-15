package com.graphResearcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphResearcher.model.*;
import com.graphResearcher.model.graphInfo.GraphResearchInfo;
import com.graphResearcher.repository.*;
import com.graphResearcher.service.GraphResearchService;
import com.graphResearcher.util.GraphArchive;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphResearcherApplicationTests {
    private final GraphModel simpleGraph;
    private final GraphModel directedWeighedGraphWithLoops;
    private final GraphModel directedGraph;

    private final GraphManager graphManager = new GraphManager();
    private final InfoManager infoManager = new InfoManager(graphManager);
    private final UserManager userManager = new UserManager(graphManager, infoManager);

    GraphResearcherApplicationTests() {
        simpleGraph = buildSimpleGraph();
        directedWeighedGraphWithLoops = buildDirectedWeighedGraphWithLoops();
        directedGraph = buildDirectedGraph();
    }

    private void saveTest(GraphModel graph) {
        int userID = 1;
        userManager.createUser(userID);

        int graphID = graphManager.saveGraph(userID, graph);
        GraphModel receivedGraph = graphManager.getGraph(graphID);
        assertEquals(graph, receivedGraph);

        userManager.deleteUser(userID);
    }

    private void testResearch(GraphModel graph) {
        int userID = 1;
        GraphResearchService service = new GraphResearchService();
        userManager.createUser(userID);

        int graphID = graphManager.saveGraph(userID, graph);
        GraphResearchInfo info = service.research(graph);

        infoManager.saveResearchInfo(userID, graphID, info);

        GraphResearchInfo receivedInfo = infoManager.getResearchInfo(graphID);

        assertEquals(info, receivedInfo);

        assertEquals(new GraphResearchInfo(receivedInfo.toJson(), graph), info);

        userManager.deleteUser(userID);
    }

    @Test
    void saveGraphTest1() {
        saveTest(simpleGraph);
    }

    @Test
    void saveGraphTest2() {
        saveTest(directedGraph);
    }

    @Test
    void research1() {
        testResearch(simpleGraph);
    }

    @Test
    void research2() {
        testResearch(directedGraph);
    }

    @Test
    void research3() {
        testResearch(directedWeighedGraphWithLoops);
    }

    @Test
    void research4() {
        testResearch(GraphArchive.getApollonianGraph());
    }

    @Test
    void research5() {
        testResearch(GraphArchive.getPetersenGraph());
    }

    @Test
    void research6() {
        testResearch(GraphArchive.getChvatalGraph());
    }

    @Test
    void research7() {
        testResearch(GraphArchive.getHerschelGraph());
    }

    @Test
    void research8() {
        testResearch(GraphArchive.getGrotzschGraph());
    }

    @Test
    void testConverter() throws JsonProcessingException {

    }


    private GraphModel buildSimpleGraph() {
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

        GraphMetadata info = new GraphMetadata("simpleGraph", false, false, false, false);
        return new GraphModel(vertices, edges, info);
    }

    private GraphModel buildDirectedGraph() {
        ArrayList<Vertex> vertices = new ArrayList<>();
        Vertex v1 = new Vertex(1, "");
        Vertex v2 = new Vertex(2, "");
        Vertex v3 = new Vertex(3, "");
        Vertex v4 = new Vertex(4, "");
        Vertex v5 = new Vertex(5, "");
        Vertex v6 = new Vertex(6, "");
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
        vertices.add(v4);
        vertices.add(v5);
        vertices.add(v6);

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(new Edge(v1, v2, 1.0, ""));
        edges.add(new Edge(v2, v3, 1.0, ""));
        edges.add(new Edge(v3, v1, 1.0, ""));
        edges.add(new Edge(v3, v4, 1.0, ""));

        edges.add(new Edge(v4, v5, 1.0, ""));
        edges.add(new Edge(v5, v6, 1.0, ""));
        edges.add(new Edge(v6, v4, 1.0, ""));

        GraphMetadata metadata = new GraphMetadata("directedGraph", true, false, false, false);
        return new GraphModel(vertices, edges, metadata);
    }

    private GraphModel buildDirectedWeighedGraphWithLoops() {
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

        GraphMetadata metadata = new GraphMetadata("directedWeighedGraphWithLoops", true, true, true, false);
        return new GraphModel(vertices, edges, metadata);
    }
}
