package com.graphResearcher.controller;

import com.graphResearcher.model.GraphModel;
//import com.graphResearcher.model.GraphRequest;
import com.graphResearcher.service.SaveGraphService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaveGraphController {

    private final SaveGraphService saveGraphService;

    public SaveGraphController() {
        saveGraphService = new SaveGraphService();
    }

    @GetMapping("/build")
    public ResponseEntity<String> buildGraph(@RequestBody GraphModel graph) {
        if (SaveGraphService.buildGraph(graph) != null) {
            return new ResponseEntity<>("Graph was successfully created!", HttpStatus.OK);
        }
        return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
