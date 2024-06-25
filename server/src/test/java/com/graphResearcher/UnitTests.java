package com.graphResearcher;

import com.graphResearcher.dto.UserRegistrationDto;
import com.graphResearcher.exceptions.UserAlreadyExist;
import com.graphResearcher.exceptions.InvalidEmail;
import com.graphResearcher.model.*;
import com.graphResearcher.model.graphInfo.GraphResearchInfo;
import com.graphResearcher.repository.*;
import com.graphResearcher.resources.TestGraphs;
import com.graphResearcher.service.GraphResearchService;
import com.graphResearcher.service.UserService;
import com.graphResearcher.util.GraphArchive;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class UnitTests {
    private final GraphManager graphManager = new GraphManager();
    private final InfoManager infoManager = new InfoManager(graphManager);
    private final UserManager userManager = new UserManager(graphManager, infoManager);
    private final UserService userService = new UserService(userManager);

    private void saveTest(GraphModel graph) {
        User user = new User("example@gmail.com", "exampleName", "examplePassword");
        try {
            userManager.registerUser(user);
        } catch (InvalidEmail e) {
            throw new RuntimeException(e);
        }

        int userID = user.getUserID();

        GraphModel receivedGraph;

        int graphID = graphManager.saveGraph(userID, graph);
        receivedGraph = graphManager.getGraph(graphID);

        assertEquals(graph, receivedGraph);

        userManager.deleteUser(userID);
    }

    private void testResearch(GraphModel graph) {
        GraphResearchService service = new GraphResearchService();
        User user = new User("example@gmail.com", "exampleName", "examplePassword");
        try {
            userManager.registerUser(user);
        } catch (InvalidEmail e) {
            throw new RuntimeException(e);
        }
        int userID = user.getUserID();

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
    void createUserTest() {
        UserRegistrationDto userRegistrationDto =
                new UserRegistrationDto("bebrinskiy", "bebra@example.com", "password");
        try {
            userService.registerUser(userRegistrationDto);
            User user = userManager.findUserByEmail("bebra@example.com");
            assertEquals("bebrinskiy", user.getUsername());
            assertEquals("bebra@example.com", user.getEmail());
            userManager.deleteUser(
                    user.getUserID());
        } catch (UserAlreadyExist | InvalidEmail e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void badEmail() {
        UserRegistrationDto userRegistrationDto =
                new UserRegistrationDto("bebrinskiy", "bebraexample.com", "password");
        assertThrows(InvalidEmail.class, () -> userService.registerUser(userRegistrationDto));
    }

    @Test
    void createUserTwice() {
        UserRegistrationDto userRegistrationDto =
                new UserRegistrationDto("usertwice", "usertwice@example.com", "password");
        try {
            User user = userService.registerUser(userRegistrationDto);
            assertThrows(UserAlreadyExist.class, () -> userService.registerUser(userRegistrationDto));
            userManager.deleteUser(
                    user.getUserID());
        } catch (UserAlreadyExist | InvalidEmail e) {
            throw new RuntimeException(e);
        }
    }
}