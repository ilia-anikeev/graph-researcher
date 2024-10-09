package com.graphResearcher.controller;

import com.graphResearcher.service.GraphSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class GraphSearchController {

    private final GraphSearchService graphSearchService;

    public GraphSearchController(GraphSearchService graphSearchService) {
        this.graphSearchService = graphSearchService;
    }

    @PostMapping("/search-graphs")
    public ResponseEntity<Map<String, Object>> searchGraphs(
            @RequestBody Map<String, Object> searchCriteria,
            @RequestParam(defaultValue = "1") int page) {
        System.out.println(searchCriteria);
        List<Integer> graphIds = graphSearchService.searchGraphs(searchCriteria, page);

        Map<String, Object> response = Map.of(
                "graphs", graphIds,
                "page", page
        );

        return ResponseEntity.ok(response);
    }
}
