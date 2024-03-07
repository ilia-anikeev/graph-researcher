package com.graphResearcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.graphResearcher.dao.DataBaseManager;

@SpringBootApplication
public class GraphResearcherApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphResearcherApplication.class, args);
    }
}
