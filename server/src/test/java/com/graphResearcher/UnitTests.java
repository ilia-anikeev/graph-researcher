package com.graphResearcher;

import com.graphResearcher.model.*;
import com.graphResearcher.model.graphInfo.GraphResearchInfo;
import com.graphResearcher.repository.*;
import com.graphResearcher.resources.TestGraphs;
import com.graphResearcher.service.GraphResearchService;
import com.graphResearcher.service.UserService;
import com.graphResearcher.util.GraphArchive;
import net.bytebuddy.implementation.bytecode.Throw;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnitTests {
    private final GraphManager graphManager = new GraphManager();
    private final InfoManager infoManager = new InfoManager(graphManager);
    private final UserManager userManager = new UserManager(graphManager, infoManager);
    private final UserService userService= new UserService(userManager);
    private void saveTest(GraphModel graph) {
        User registeredUser = userService.getUser("example");
        if(registeredUser==null){
            registeredUser=userManager.findByUsername("example");
        }
        int userID=registeredUser.getUserId();

        GraphModel receivedGraph;

        int graphID = graphManager.saveGraph(userID, graph);
        receivedGraph = graphManager.getGraph(graphID);

        assertEquals(graph, receivedGraph);

        userManager.deleteUser(userID);
    }

    private void testResearch(GraphModel graph) {
        GraphResearchService service = new GraphResearchService();
        User registeredUser = userService.getUser("example");
        if(registeredUser==null){
            registeredUser=userManager.findByUsername("example");
        }
        int userID=registeredUser.getUserId();

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

    @Test
    void createUserTest(){
        User user= new User();
        user.setEmail("bebra@example.com");
        user.setUsername("bebrinskiy");
        user.setPassword("password");
        if(userService.getUser(user.getUsername())==null){

        }
        userManager.registerUser(user);
    }

    @Test
    void badEmail(){
        User user= new User();
        user.setEmail("bebraexample.com");
        user.setUsername("bebrinskiy");
        user.setPassword("password");
        userManager.registerUser(user);
    }
    @Test
    void createUserTwice(){
        User user= new User();
        user.setEmail("usertwice@example.com");
        user.setUsername("usertwice");
        user.setPassword("password");
        userManager.registerUser(user);
        userManager.registerUser(user);
    }
}
