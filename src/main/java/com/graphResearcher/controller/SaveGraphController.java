package com.graphResearcher.controller;

import com.graphResearcher.GraphRequest;
import com.graphResearcher.VertexRequest;
import com.graphResearcher.service.SaveGraphService;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.Graph;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaveGraphController {

    private final SaveGraphService saveGraphService;

    public SaveGraphController(){
        saveGraphService = new SaveGraphService();
    }

    @GetMapping("/build")
    public Graph<VertexRequest, DefaultEdge> buildGraph(@RequestBody GraphRequest graph) {
        return saveGraphService.buildGraph(graph);
    }
}
