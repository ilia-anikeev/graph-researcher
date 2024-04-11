package com.graphResearcher;

import com.graphResearcher.dao.DataBaseManager;
import com.graphResearcher.model.Edge;
import com.graphResearcher.model.GraphMetadata;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.Vertex;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Date;

@SpringBootApplication
public class GraphResearcherApplication {
    public static void main(String[] args) {
        new DataBaseManager().createUser(0);
        SpringApplication.run(GraphResearcherApplication.class, args);
    }
}


//{"vertices":[{"index":1,"data":"hi"},{"index":2,"data":"hello"},{"index":3,"data":"salam"}],"edges":[{"source":{"index":1,"data":"hi"},"target":{"index":2,"data":"hello"},"weight":1.0,"data":"aloha"},{"source":{"index":2,"data":"hello"},"target":{"index":3,"data":"salam"},"weight":1.0,"data":"buenos noches"},{"source":{"index":3,"data":"salam"},"target":{"index":1,"data":"hi"},"weight":1.0,"data":"guten morgen"}],"info":{"isDirected":false,"isWeighted":false,"hasSelfLoops":false,"hasMultipleEdges":false}}
