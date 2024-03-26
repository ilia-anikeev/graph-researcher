package com.graphResearcher.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphResearcher.model.*;
import com.graphResearcher.service.GraphResearchService;
import com.graphResearcher.service.SaveService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
public class GraphResearchController {
    private final GraphResearchService researchService;
    private final SaveService saveService;
    private static final Logger log = LoggerFactory.getLogger(GraphResearchController.class);

    @PostMapping("/research")
    public ResponseEntity<String> research(HttpServletRequest request) {
        try {
            String jsonString = request.getReader().lines().collect(Collectors.joining());

            ObjectMapper mapper = new ObjectMapper();

            JsonNode json = mapper.readTree(jsonString);

            GraphModel graphModel = new GraphModel(json.get("graph"));

            int userID = json.get("userID").asInt();
            int graphID = saveService.saveGraph(userID, graphModel);

            GraphResearchInfo researchResult = researchService.softResearch(graphModel);

            saveService.saveResearchResult(userID, graphID, researchResult);
            log.info("Research was successfully completed");
            return ResponseEntity.ok("Граф жёска исследован!\n" + researchResult.toJson());
        } catch (JsonProcessingException e) {
            log.error("Json parsing error in saveGraph");
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Request parsing error in saveGraph");
            throw new RuntimeException(e);
        }

    }

    GraphResearchController(GraphResearchService researchService, SaveService saveService) {
        this.researchService = researchService;
        this.saveService = saveService;
    }
}