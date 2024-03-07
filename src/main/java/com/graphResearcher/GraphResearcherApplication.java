package com.graphResearcher;

import com.graphResearcher.model.Edge;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.graphResearcher.dao.DataBaseManager;


import java.util.ArrayList;

@SpringBootApplication
public class GraphResearcherApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphResearcherApplication.class, args);
/*        DataBaseManager db = new DataBaseManager();
        ArrayList<Edge> a = db.getEdges(0, 1);
        for (Edge x: a) {
            System.err.print(x.source);
            System.err.print(" ");
            System.err.println(x.target);
        }*/
    }
}
