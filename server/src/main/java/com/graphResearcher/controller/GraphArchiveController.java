package com.graphResearcher.controller;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.service.GraphArchiveService;
import com.graphResearcher.util.Converter;
import com.graphResearcher.util.GraphArchive;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.graphResearcher.model.GraphModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

import java.util.List;


@RestController
public class GraphArchiveController {
    private final GraphArchiveService graphArchiveService;
    private static final Logger log = LoggerFactory.getLogger(GraphResearchController.class);

    @PostMapping("/save")
    public ResponseEntity<String> saveGraph(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = mapper.readTree(jsonString);

            int userID = json.get("userID").asInt();

            GraphModel graph = new GraphModel(json.get("graph"));

            int graphID = graphArchiveService.saveGraph(userID, graph);

            log.info("Graph has been saved");
            return ResponseEntity.ok("Graph has been saved with ID" + graphID);
        } catch (JsonProcessingException e) {
            log.error("Json parsing error", e);
            return ResponseEntity.badRequest().body("Wrong json format");
        } catch (Throwable e) {
            log.error("Server error", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/get_all_graphs")
    public ResponseEntity<String> getAllUserGraphs(@RequestParam int user_id) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<GraphModel> graphModelList = graphArchiveService.getAllUserGraphs(user_id);
            ArrayNode arr = Converter.GraphModelListToJsonArray(graphModelList);

            ObjectNode jsonResponse = mapper.createObjectNode();
            jsonResponse.set("graphs", arr);

            log.info("Graphs was received");
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (Throwable e) {
            log.error("Server error", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/get_graph")
    public ResponseEntity<String> getGraph(@RequestParam int graph_id) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GraphModel graphModel = graphArchiveService.getGraph(graph_id);

            ObjectNode jsonResponse = mapper.createObjectNode();
            jsonResponse.set("graph", graphModel.toJson());

            log.info("Graph was received");
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (Throwable e) {
            return ResponseEntity.badRequest().body("graph doesn't exist");
        }
    }

    @PostMapping("/delete_graph")
    public ResponseEntity<String> deleteGraph(@RequestParam int graph_id) {
        try {
            graphArchiveService.deleteGraph(graph_id);
            log.info("Graph was removed");
            return ResponseEntity.ok("Graph " + graph_id + " was removed");
        } catch (Throwable e) {
            return ResponseEntity.badRequest().body("graph doesn't exist");
        }
    }

    @GetMapping("/get_famous_graph")
    public ResponseEntity<String> getFamousGraph(@RequestParam String graph_name) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode jsonResponse = mapper.createObjectNode();
            switch (graph_name) {
                case ("Petersen"):
                    jsonResponse.set("graph", GraphArchive.getPetersenGraph().toJson());
                    break;
                case ("Chvatal"):
                    jsonResponse.set("graph", GraphArchive.getChvatalGraph().toJson());
                    break;
                case ("Grotzsch"):
                    jsonResponse.set("graph", GraphArchive.getGrotzschGraph().toJson());
                    break;
                case ("Apollonian"):
                    jsonResponse.set("graph", GraphArchive.getApollonianGraph().toJson());
                    break;
                case ("Herschel"):
                    jsonResponse.set("graph", GraphArchive.getHerschelGraph().toJson());
            }

            log.info("Graph {} was received", graph_name);
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (Throwable e) {
            log.error("Server error", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    GraphArchiveController(GraphArchiveService graphArchiveService) {
        this.graphArchiveService = graphArchiveService;
    }
}
