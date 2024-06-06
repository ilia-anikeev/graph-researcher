package com.graphResearcher.controller;



import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.service.GraphArchiveService;
import com.graphResearcher.util.Converter;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.graphResearcher.model.GraphModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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


    @GetMapping("/get_all_user_graphs")
    public ResponseEntity<String> getAllUserGraphs(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = mapper.readTree(jsonString);

            int userID = json.get("userID").asInt();
            List<GraphModel> graphModelList = graphArchiveService.getAllUserGraphs(userID);
            ArrayNode arr = Converter.GraphModelListToJsonArray(graphModelList);

            ObjectNode jsonResponse = mapper.createObjectNode();
            jsonResponse.set("graphs", arr);

            log.info("Graphs was received");
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (JsonProcessingException e) {
            log.error("Json parsing error", e);
            return ResponseEntity.badRequest().body("Wrong json format");
        } catch (Throwable e) {
            log.error("Server error", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }



    GraphArchiveController(GraphArchiveService graphArchiveService) {
        this.graphArchiveService = graphArchiveService;
    }
}