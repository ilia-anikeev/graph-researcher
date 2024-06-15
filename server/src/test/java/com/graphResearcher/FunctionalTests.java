package com.graphResearcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.graphInfo.GraphResearchInfo;
import com.graphResearcher.resources.TestGraphs;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class FunctionalTests {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testResearchSimpleGraph() {
        testResearch(TestGraphs.simpleGraph, TestGraphs.simpleGraphInfo);
    }

    @Test
    public void testResearchDirectedGraph() {
        testResearch(TestGraphs.directedGraph, TestGraphs.directedGraphInfo);
    }

    @Test
    public void testResearchOneEdgeGraph() {
        testResearch(TestGraphs.oneEdgeGraph, TestGraphs.oneEdgeGraphInfo);
    }

    @Test
    public void testResearchDirectedWeightedGraph() {
        testResearch(TestGraphs.directedWeighedGraph, TestGraphs.directedWeighedGraphInfo);
    }

    private void testResearch(GraphModel graph, GraphResearchInfo expectedInfo) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/research")
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createResearchRequest(graph))
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .subscribe(result -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode json;
                    try {
                        json = objectMapper.readTree(result);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    GraphResearchInfo actualInfo = new GraphResearchInfo(json, TestGraphs.simpleGraph);
                    assertEquals(actualInfo, expectedInfo);
                });
    }

    private ObjectNode createResearchRequest(GraphModel graph) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.set("graph", graph.toJson());
        return json;
    }


}
