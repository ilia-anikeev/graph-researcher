package com.graphResearcher.controller;

import com.graphResearcher.dao.DataBaseManager;
import com.graphResearcher.model.*;
import com.graphResearcher.service.GraphResearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GraphResearchController {
    private final GraphResearchService service;
    private final DataBaseManager db = new DataBaseManager();

    @GetMapping("/research")
    public String research(String Json) {
        // TODO: Parse Json to GraphModel(?)
        Vertex v1 = new Vertex(1, "a");
        Vertex v2 = new Vertex(2, "b");
        Vertex v3 = new Vertex(3, "c");
        Vertex v4 = new Vertex(4, "d");
        List<Vertex> vertices = List.of(v1, v2, v3, v4);

        Edge e1 = new Edge(v1, v2, 1.0, "aba");
        Edge e2 = new Edge(v2, v3, 1.0, "abaaa");
        Edge e3 = new Edge(v3, v1, 1.0, "abasdf");
        Edge e4 = new Edge(v1, v4, 1.0, "abou2");
        List<Edge> edges = List.of(e1, e2, e3, e4);

        GraphMetadata info = new GraphMetadata(false, false, false, false);
        GraphModel graphModel = new GraphModel(vertices, edges, info);


        GraphResearchInfo researchResult = service.softResearch(graphModel);

//        db.deleteUser(1);
//        db.createUser(1);
//        db.saveResearchInfo(1, 1, researchResult);
        // TODO: Convert researchResult to Json(?)


//        return db.getResearchInfo(1, 1).toJson().toString();
        return "";
    }

    GraphResearchController(GraphResearchService service) {
        this.service = service;
    }
}