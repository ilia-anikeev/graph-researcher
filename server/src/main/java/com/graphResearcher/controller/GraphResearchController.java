package com.graphResearcher.controller;

import com.graphResearcher.dao.DataBaseManager;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.GraphResearchInfo;
import com.graphResearcher.service.GraphResearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphResearchController {
    private final GraphResearchService service;
    @GetMapping("/research")
    public String research(String Json){
        String result="";
        // TODO: Parse Json to GraphModel(?)
        GraphModel graphModel = null;
        GraphResearchInfo researchResult= service.softResearch(graphModel);
        // TODO: Convert researchResult to Json(?)
        return result;
    }

    GraphResearchController(GraphResearchService service) {
        this.service = service;
    }
}