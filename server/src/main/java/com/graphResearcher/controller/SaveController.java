package com.graphResearcher.controller;



import com.graphResearcher.service.SaveService;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.graphResearcher.model.GraphModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;


@RestController

public class SaveController {
    private final SaveService saveService;
    private static final Logger log = LoggerFactory.getLogger(GraphResearchController.class);

    @PostMapping("/save")
    public ResponseEntity<String> saveGraph(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = mapper.readTree(jsonString);

            int userID = json.get("userID").asInt();

            GraphModel graph = new GraphModel(json.get("graph"));

            int graphID = saveService.saveGraph(userID, graph);

            log.info("Graph has been saved");
            return new ResponseEntity<>("Graph has been saved with ID" + graphID, HttpStatus.OK);
        } catch (JsonProcessingException e) {
            log.error("Json parsing error", e);
            throw new RuntimeException(e);
        }
    }

    SaveController(SaveService saveService) {
        this.saveService = saveService;
    }
}