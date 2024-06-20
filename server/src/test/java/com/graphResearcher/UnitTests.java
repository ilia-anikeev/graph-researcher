package com.graphResearcher;

import com.graphResearcher.model.*;
import com.graphResearcher.model.graphInfo.GraphResearchInfo;
import com.graphResearcher.repository.*;
import com.graphResearcher.resources.TestGraphs;
import com.graphResearcher.service.GraphResearchService;
import com.graphResearcher.util.GraphArchive;
import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnitTests {
    private final GraphManager graphManager = new GraphManager();
    private final InfoManager infoManager = new InfoManager(graphManager);
    private final UserManager userManager = new UserManager(graphManager, infoManager);

    private void saveTest(GraphModel graph) {
        int userID = 1;
        userManager.createUser(userID);

        GraphModel receivedGraph;

        int graphID = graphManager.saveGraph(userID, graph);
        receivedGraph = graphManager.getGraph(graphID);

        assertEquals(graph, receivedGraph);

        userManager.deleteUser(userID);
    }

    private void testResearch(GraphModel graph) {
        int userID = 1;
        GraphResearchService service = new GraphResearchService();
        userManager.createUser(userID);

        int graphID = graphManager.saveGraph(userID, graph);
        GraphResearchInfo info;
        try {
            info = service.research(graph).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        infoManager.saveResearchInfo(userID, graphID, info);

        GraphResearchInfo receivedInfo = infoManager.getResearchInfo(graphID);

        assertEquals(info, receivedInfo);

        assertEquals(new GraphResearchInfo(receivedInfo.toJson(), graph), info);

        userManager.deleteUser(userID);
    }

    @Test
    void saveGraphTest1() {
        saveTest(TestGraphs.simpleGraph);
    }

    @Test
    void saveGraphTest2() {
        saveTest(TestGraphs.directedGraph);
    }

    @Test
    void saveGraphTest3() {
        saveTest(TestGraphs.directedWeighedGraph);
    }

    @Test
    void research1() {
        testResearch(TestGraphs.simpleGraph);
    }

    @Test
    void research2() {
        testResearch(TestGraphs.directedGraph);
    }

    @Test
    void research3() {
        testResearch(TestGraphs.directedWeighedGraph);
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
}
