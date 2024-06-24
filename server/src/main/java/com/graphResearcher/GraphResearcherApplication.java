package com.graphResearcher;

import com.graphResearcher.repository.DatabaseCleaner;
import com.graphResearcher.repository.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GraphResearcherApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphResearcherApplication.class, args);
    }
}
