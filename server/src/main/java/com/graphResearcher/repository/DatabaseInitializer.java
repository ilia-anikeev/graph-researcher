package com.graphResearcher.repository;

import com.graphResearcher.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInitializer {
    private static final Logger log = LoggerFactory.getLogger(InfoManager.class);
    private final DataSource dataSource;

    public DatabaseInitializer() {
        String name = PropertiesUtil.get("db.username");
        String password = PropertiesUtil.get("db.password");
        String url = PropertiesUtil.get("db.url");
        dataSource = new DriverManagerDataSource(url, name, password);
    }

    public void initDB() {
        try (Connection conn = dataSource.getConnection()) {
            initEdgesTable(conn);
            initVerticesTable(conn);
            initMetadataTable(conn);

            initGraphResearchInfoTable(conn);
            initArticulationPointsTable(conn);
            initBridgesTable(conn);
            initConnectedComponentsTable(conn);
            initBlocksTable(conn);

            initEmbeddingTable(conn);
            initKuratowskiSubgraphTable(conn);

            initPerfectEliminationOrderTable(conn);
            initColoringTable(conn);
            initMaxCliqueTable(conn);
            initIndependentSetTable(conn);
            initMinimalVertexSeparatorTable(conn);
            initPartitionsTable(conn);

            initMinSpanningTreeTable(conn);

            initFlowValueTable(conn);

            log.info("Database initialization was successful");
        } catch (SQLException e) {
            log.error("Database haven't been initialized", e);
            throw new RuntimeException(e);
        }
    }

    private void initVerticesTable(Connection conn) {
        String sql = "CREATE TABLE vertices(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init vertices table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initEdgesTable(Connection conn) {
        String sql = "CREATE TABLE edges(id SERIAL PRIMARY KEY, graph_id INT, source INT, target INT, weight DOUBLE PRECISION, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init edges table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initMetadataTable(Connection conn) {
        String sql = "CREATE TABLE graph_metadata(graph_id SERIAL PRIMARY KEY, user_id INT, graph_name TEXT, is_directed BOOLEAN, is_weighted BOOLEAN, has_self_loops BOOLEAN, has_multiple_edges BOOLEAN)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init metadata table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initGraphResearchInfoTable(Connection conn) {
        String sql = "CREATE TABLE graph_research_info(id SERIAL PRIMARY KEY, " +
                "graph_id INT, user_id INT, is_connected BOOLEAN, is_biconnected BOOLEAN, " +
                "articulation_points INT, bridges INT, connected_components INT, " +
                "blocks INT, is_planar BOOLEAN, is_chordal BOOLEAN, chromatic_number INT, is_bipartite BOOLEAN)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init graphResearchInfo table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initArticulationPointsTable(Connection conn) {
        String sql = "CREATE TABLE articulation_points(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init articulationPoints table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initBridgesTable(Connection conn) {
        String sql = "CREATE TABLE bridges(id SERIAL PRIMARY KEY, graph_id INT, source INT, target INT, weight DOUBLE PRECISION, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init bridges table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initConnectedComponentsTable(Connection conn) {
        String sql = "CREATE TABLE connected_components(id SERIAL PRIMARY KEY, graph_id INT, component_number INT, index INT, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init connected components table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initBlocksTable(Connection conn) {
        String sql = "CREATE TABLE blocks(id SERIAL PRIMARY KEY, graph_id INT, component_number INT, index INT, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init blocks table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initPerfectEliminationOrderTable(Connection conn) {
        String sql = "CREATE TABLE perfect_elimination_order(id SERIAL PRIMARY KEY, graph_id INT, sequence_number INT, index INT, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init perfectEliminationOrder table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initEmbeddingTable(Connection conn) {
        String sql = "CREATE TABLE embedding(id SERIAL PRIMARY KEY, graph_id INT, index INT, sequence_number INT, source INT, target INT, weight DOUBLE PRECISION, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init embedding table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initKuratowskiSubgraphTable(Connection conn) {
        String sql = "CREATE TABLE kuratowski_subgraph(id SERIAL PRIMARY KEY, graph_id INT, subgraph_id INT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init Kuratowski subgraph table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initColoringTable(Connection conn) {
        String sql = "CREATE TABLE coloring(id SERIAL PRIMARY KEY, graph_id INT, component_number INT, index INT, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init coloring table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initMaxCliqueTable(Connection conn) {
        String sql = "CREATE TABLE max_clique(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init max clique table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initIndependentSetTable(Connection conn) {
        String sql = "CREATE TABLE independent_set(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init independent_set table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initMinimalVertexSeparatorTable(Connection conn) {
        String sql = "CREATE TABLE minimal_vertex_separator(id SERIAL PRIMARY KEY, graph_id INT, component_number INT, index INT, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init minimal_vertex_separator table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initPartitionsTable(Connection conn) {
        String sql = "CREATE TABLE partitions(id SERIAL PRIMARY KEY, graph_id INT, component_number INT, index INT, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init connected components table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initMinSpanningTreeTable(Connection conn) {
        String sql = "CREATE TABLE min_spanning_tree(id SERIAL PRIMARY KEY, graph_id INT, source INT, target INT, weight DOUBLE PRECISION, data TEXT)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init spanning_tree table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initFlowValueTable(Connection conn) {
        String sql = "CREATE TABLE flow_value(id SERIAL PRIMARY KEY, graph_id INT, value DOUBLE PRECISION)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init flow value table error", e);
            throw new RuntimeException(e);
        }
    }
}
