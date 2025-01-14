package com.graphResearcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.controller.GraphSearchController;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.resources.TestUtils;
import com.graphResearcher.service.GraphSearchService;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.graphResearcher.model.graphInfo.GraphResearchInfo;
import com.graphResearcher.resources.TestGraphs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


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
    @Test
    public void testUploadFileSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-graph.txt", MediaType.TEXT_PLAIN_VALUE, "0 1\n1 0\n".getBytes());

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getBytes())
                .filename("test-graph.txt")
                .contentType(MediaType.TEXT_PLAIN);
        builder.part("userID", "1");

        webTestClient.post()
                .uri("/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.graphID").isNumber();
    }


    @Test
    public void testUploadFileEmpty() throws Exception {

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty-graph.txt", MediaType.TEXT_PLAIN_VALUE, "".getBytes());

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", emptyFile.getBytes())
                .filename("empty-graph.txt")
                .contentType(MediaType.TEXT_PLAIN);
        builder.part("userID", "1");


        webTestClient.post()
                .uri("/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("File is empty");
    }


    @Test
    public void testSearchGraphs() {
        Map<String, Object> searchCriteria = Map.of(
                "is_connected", true,
                "is_biconnected", false,
                "is_planar", false,
                "is_chordal", false,
                "chromatic_number", "3",
                "is_bipartite", false
        );

        List<Integer> graphIDs = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/search-graphs")
                        .queryParam("page", 1)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(searchCriteria)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .<List<Integer>>handle((result, sink) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode json;
                    try {

                        json = objectMapper.readTree(result);
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException(e));
                        return;
                    }

                    JsonNode node = json.get("graphs");
                    List<Integer> ids = new ArrayList<>();
                    for (var idJson : node) {
                        ids.add(idJson.asInt());
                    }
                    sink.next(ids);
                }).blockLast();

        assertNotNull(graphIDs);


        for (int id : graphIDs) {
            getGraph(id);
            deleteGraph(id);
        }
    }



    @Test
    public void testSaveSimpleGraph() {
        testSave(TestGraphs.simpleGraph, TestGraphs.simpleGraphInfo);
    }
    @Test
    public void testSaveDirectedGraph() {
        testSave(TestGraphs.directedGraph, TestGraphs.directedGraphInfo);
    }
    @Test
    public void testSaveDirectedWeightedGraph() {
        testSave(TestGraphs.directedWeighedGraph, TestGraphs.directedWeighedGraphInfo);
    }
    @Test
    public void testSaveOneEdgeGraph() {
        testSave(TestGraphs.oneEdgeGraph, TestGraphs.oneEdgeGraphInfo);
    }

    @Test
    public void testGetAllUserGraphs() {
        List<Integer> graphIDs = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/get_all_graphs")
                        .queryParam("user_id", 1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .<List<Integer>>handle((result, sink) -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode json;
                    try {
                        json = objectMapper.readTree(result);
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException(e));
                        return;
                    }
                    JsonNode node = json.get("ids");
                    List<Integer> ids = new ArrayList<>();
                    for (var idJson : node) {
                        ids.add(idJson.asInt());
                    }
                    sink.next(ids);
                }).blockLast();
        assert graphIDs != null;
        for (int id: graphIDs) {
            getGraph(id);
            deleteGraph(id);
        }
    }

    public void deleteGraph(int id) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/delete_graph")
                        .queryParam("graph_id", id)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testGetFamousGraphs() {
        testGetFamousGraph("Petersen");
        testGetFamousGraph("Chvatal");
        testGetFamousGraph("Grotzsch");
        testGetFamousGraph("Apollonian");
        testGetFamousGraph("Herschel");
    }

    @Test
    public void testFlowResearch() {
        testFlowResearch(TestGraphs.directedWeighedGraph, 1, 6);
    }

    @Test
    public void testSaveFlowResearch() {
        GraphModel graph = TestGraphs.directedWeighedGraph;
        GraphResearchInfo info = TestGraphs.directedWeighedGraphInfo;

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/save")
                        .queryParam("user_id", 1)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createSaveRequest(graph, info))
                .exchange()
                .expectStatus().isOk();
    }

    public void testGetFamousGraph(String graphName) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/get_famous_graph")
                        .queryParam("graph_name", graphName)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    public void getGraph(int id) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/get_graph")
                        .queryParam("graph_id", id)
                        .build())
                .exchange()
                .expectStatus().isOk();
    }

    private void testFlowResearch(GraphModel graph, int source, int sink) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/flow_research")
                        .queryParam("source", source)
                        .queryParam("sink", sink)
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
                        assertEquals(5.0, json.get("maxFlow").asDouble());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
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

    private void testSave(GraphModel graph, GraphResearchInfo info) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/save")
                        .queryParam("user_id", 1)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createSaveRequest(graph, info))
                .exchange()
                .expectStatus().isOk();
    }

    private ObjectNode createResearchRequest(GraphModel graph) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.set("graph", graph.toJson());
        return json;
    }

    private ObjectNode createSaveRequest(GraphModel graph, GraphResearchInfo info) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.set("graph", graph.toJson());
        json.set("info", info.toJson());
        return json;
    }
}
