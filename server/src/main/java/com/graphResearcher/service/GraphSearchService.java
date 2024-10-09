package com.graphResearcher.service;

import com.graphResearcher.repository.GraphManager;
import com.graphResearcher.util.PropertiesUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class GraphSearchService {

    private final GraphManager graphManager;

    public GraphSearchService(GraphManager graphManager) {
        this.graphManager = graphManager;
    }

    public List<Integer> searchGraphs(Map<String, Object> searchCriteria, int page) {
        String sqlQuery = "SELECT graph_id FROM graph_research_info WHERE 1=1";

        if ((boolean) searchCriteria.get("is_connected")) {
            sqlQuery += " AND is_connected = true";
        }
        if ((boolean) searchCriteria.get("is_biconnected")) {
            sqlQuery += " AND is_biconnected = true";
        }
        if ((boolean) searchCriteria.get("is_planar")) {
            sqlQuery += " AND is_planar = true";
        }
        if ((boolean) searchCriteria.get("is_chordal")) {
            sqlQuery += " AND is_chordal = true";
        }
        System.out.println(sqlQuery);
        System.out.println(searchCriteria.get("chromatic_number"));

        if (Integer.parseInt((String)searchCriteria.get("chromatic_number")) > 0) {
            sqlQuery += " AND chromatic_number = " + searchCriteria.get("chromatic_number");
        }
        if ((boolean) searchCriteria.get("is_bipartite")) {
            sqlQuery += " AND is_bipartite = true";
        }
        System.out.println(sqlQuery);

        int offset = (page - 1) * 3;
        sqlQuery += " LIMIT 3 OFFSET " + offset;
        System.out.println(sqlQuery);

        return graphManager.getGraphs(sqlQuery);
    }
}
