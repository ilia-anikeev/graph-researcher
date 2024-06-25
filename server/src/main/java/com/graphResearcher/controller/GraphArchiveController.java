package com.graphResearcher.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.model.graphInfo.FlowResearchInfo;
import com.graphResearcher.model.graphInfo.GraphResearchInfo;
import com.graphResearcher.repository.InfoManager;
import com.graphResearcher.service.GraphArchiveService;
import com.graphResearcher.util.Converter;
import com.graphResearcher.util.GraphArchive;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.graphResearcher.model.GraphModel;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GraphArchiveController {
    private final GraphArchiveService graphArchiveService;
    private static final Logger log = LoggerFactory.getLogger(GraphResearchController.class);

    @CrossOrigin
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestParam int user_id, HttpServletRequest request) {
        try {
            String jsonString = request.getReader().lines().collect(Collectors.joining());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(jsonString);

            GraphModel graph = new GraphModel(json.get("graph"));
            int graphID = graphArchiveService.saveGraph(user_id, graph).get();

            if (json.has("info")) {
                GraphResearchInfo info = new GraphResearchInfo(json.get("info"), graph);
                graphArchiveService.saveResearchResult(user_id, graphID, info);
            }
            if (json.has("flowInfo")) {
                FlowResearchInfo flowResearchInfo = new FlowResearchInfo(json.get("flowInfo"));
                graphArchiveService.saveFlowResult(graphID, flowResearchInfo);
            }
            if (json.has("comment")) {
                String comment = json.get("comment").asText();
                graphArchiveService.saveComment(graphID, comment);
            }
            log.info("Graph has been saved");
            return ResponseEntity.ok(Integer.toString(graphID));
        } catch (JsonProcessingException e) {
            log.error("Json parsing error", e);
            return ResponseEntity.badRequest().body("Wrong json format");
        } catch (Throwable e) {
            log.error("Server error", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @CrossOrigin
    @GetMapping("/get_all_graphs")
    public ResponseEntity<String> getAllUserGraphIDs(@RequestParam int user_id) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<Integer, String> graphIDs = graphArchiveService.getAllUserGraphIDs(user_id).get();
            ObjectNode json = mapper.createObjectNode();

            json.set("ids", Converter.integerListToJsonArray(graphIDs.keySet().stream().toList()));

            for (Map.Entry<Integer, String> entry: graphIDs.entrySet()) {
                json.put(String.valueOf(entry.getKey()), entry.getValue());
            }

            log.info("GraphIDs were sent");
            return ResponseEntity.ok(json.toString());
        } catch (Throwable e) {
            log.error("Server error", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @CrossOrigin
    @GetMapping("/get_graph")
    public ResponseEntity<String> getGraph(@RequestParam int graph_id) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GraphModel graphModel = graphArchiveService.getGraph(graph_id).get();

            ObjectNode jsonResponse = mapper.createObjectNode();
            jsonResponse.set("graph", graphModel.toJson());

            log.info("Graph was received");
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (Throwable e) {
            return ResponseEntity.badRequest().body("graph doesn't exist");
        }
    }

    @CrossOrigin
    @GetMapping("/get_graph_info")
    public ResponseEntity<String> getGraphInfo(@RequestParam int graph_id) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GraphResearchInfo info = graphArchiveService.getGraphInfo(graph_id).get();

            ObjectNode jsonResponse = mapper.createObjectNode();
            jsonResponse.set("info", info.toJson());
            jsonResponse.put("comment", graphArchiveService.getComment(graph_id).get());

            log.info("Graph was received");
            return ResponseEntity.ok(jsonResponse.toString());
        } catch (Throwable e) {
            return ResponseEntity.badRequest().body("graph doesn't exist");
        }
    }

    @CrossOrigin
    @PostMapping("/delete_graph")
    public ResponseEntity<String> deleteGraph(@RequestParam int graph_id) {
        try {
            graphArchiveService.deleteGraph(graph_id);
            log.info("Graph was removed");
            return ResponseEntity.ok("");
        } catch (Throwable e) {
            return ResponseEntity.badRequest().body("graph doesn't exist");
        }
    }

    @CrossOrigin
    @PostMapping("/delete_all_user_graphs")
    public ResponseEntity<String> deleteAllUserGraphs(@RequestParam int user_id) {
        try {
            List<Integer> graphIDs = graphArchiveService.getAllUserGraphIDs(user_id).get().keySet().stream().toList();
            for (int id: graphIDs) {
                graphArchiveService.deleteGraph(id);
            }
            log.info("All user graphs were removed");
            return ResponseEntity.ok("");
        } catch (Throwable e) {
            return ResponseEntity.badRequest().body("graph doesn't exist");
        }
    }

    @CrossOrigin
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