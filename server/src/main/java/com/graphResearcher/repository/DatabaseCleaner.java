package com.graphResearcher.repository;

import com.graphResearcher.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseCleaner {
    private static final Logger log = LoggerFactory.getLogger(InfoManager.class);
    private final DataSource dataSource;

    public DatabaseCleaner() {
        String name = PropertiesUtil.get("db.username");
        String password = PropertiesUtil.get("db.password");
        String url = PropertiesUtil.get("db.url");
        dataSource = new DriverManagerDataSource(url, name, password);
    }

    public void deleteDB() {
        try (Connection conn = dataSource.getConnection()){
            List<String> tables = List.of("articulation_points", "blocks", "bridges",
                    "connected_components", "edges", "graph_metadata", "graph_research_info",
                    "perfect_elimination_order", "vertices", "embedding", "kuratowski_subgraph",
                    "coloring", "max_clique", "independent_set", "minimal_vertex_separator",
                    "partitions", "min_spanning_tree");
            for (String tableName: tables) {
                String sql = "DROP TABLE " + tableName;
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.execute();
            }

        } catch (SQLException e) {
            log.error("Delete database error", e);
            throw new RuntimeException(e);
        }
    }
}
