package com.graphResearcher.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphResearcher.model.*;
import com.graphResearcher.model.graphInfo.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.graphResearcher.service.GraphResearchService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class GraphResearchController {
    private static final Logger log = LoggerFactory.getLogger(GraphResearchController.class);
    private final GraphResearchService researchService;

    @PostMapping("/research")
    public ResponseEntity<String> research(HttpServletRequest request) {
        try {
            String jsonString = request.getReader().lines().collect(Collectors.joining());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(jsonString);

            GraphModel graphModel = new GraphModel(json.get("graph"));

            GraphResearchInfo result = researchService.research(graphModel).get();

            log.info("Research was successfully completed");
            return ResponseEntity.ok(result.toJson().toString());
        } catch (JsonProcessingException e) {
            log.error("Json parsing error", e);
            return ResponseEntity.badRequest().body("Wrong json format");
        } catch (IOException e) {
            log.error("Request parsing error", e);
            return ResponseEntity.badRequest().body("Wrong json format");
        } catch (Throwable e) {
            log.error("Server error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/flow_research")
    public ResponseEntity<String> flowResearch(@RequestParam int source, @RequestParam int sink, HttpServletRequest request) {
        try {
            String jsonString = request.getReader().lines().collect(Collectors.joining());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(jsonString);

            GraphModel graphModel = new GraphModel(json.get("graph"));

            FlowResearchInfo result = researchService.flowResearch(graphModel, source, sink).get();

            log.info("Research was successfully completed");
            return ResponseEntity.ok(result.toJson().toString());
        } catch (JsonProcessingException e) {
            log.error("Json parsing error", e);
            return ResponseEntity.badRequest().body("Wrong json format");
        } catch (IOException e) {
            log.error("Request parsing error", e);
            return ResponseEntity.badRequest().body("Wrong json format");
        } catch (Throwable e) {
            log.error("Server error", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    GraphResearchController(GraphResearchService researchService) {
        this.researchService = researchService;
    }
}