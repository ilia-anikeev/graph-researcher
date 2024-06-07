package com.graphResearcher.repository;

import com.graphResearcher.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseCleaner {
    private static final Logger log = LoggerFactory.getLogger(DataBaseManager.class);
    private final DataSource dataSource;

    public DataBaseCleaner() {
        String name = PropertiesUtil.get("db.username");
        String password = PropertiesUtil.get("db.password");
        String url = PropertiesUtil.get("db.url");
        dataSource = new DriverManagerDataSource(url, name, password);
    }

    public void createUser(int userID) {
        //TODO
    }

    public void deleteUser(int userID) {
        try (Connection conn = dataSource.getConnection()) {
            deleteAllUserGraphs(userID, conn);
        } catch (SQLException e) {
            log.error("User{} have been deleted", userID, e);
            throw new RuntimeException(e);
        }
    }

    private void deleteAllUserGraphs(int userID, Connection conn) {
        String sql1 = "SELECT graph_id FROM graph_metadata WHERE user_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql1)) {
            preparedStatement.setInt(1, userID);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int graphID = rs.getInt("graph_id");
                deleteGraph(graphID);
            }
        } catch (SQLException e) {
            log.error("All user{} graphs haven't been deleted", userID, e);
            throw new RuntimeException(e);
        }

        String sql2 = "DELETE FROM graph_metadata WHERE user_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql2)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("All user{} graphs haven't been deleted", userID, e);
            throw new RuntimeException(e);
        }

        String sql3 = "DELETE FROM graph_research_info WHERE user_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql3)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("User{} researches haven't been deleted", userID, e);
            throw new RuntimeException(e);
        }
        log.info("All user{} graphs have been deleted", userID);
    }

    public void deleteGraph(int graphID) {
        String sql = "DELETE FROM graph_metadata WHERE graph_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();
            deleteVertices(graphID, "vertices", conn);
            deleteEdges(graphID, "edges", conn);
            deleteVertices(graphID, "articulation_points", conn);
            deleteEdges(graphID, "bridges", conn);
            deleteVertices(graphID, "connected_components", conn);
            deleteVertices(graphID, "blocks", conn);
            deletePerfectEliminationOrder(graphID, conn);
            deleteKuratowskiSubgraph(graphID, conn);
            deleteVertices(graphID, "coloring", conn);
            deleteVertices(graphID, "max_clique", conn);
            deleteVertices(graphID, "independent_set", conn);
            deleteVertices(graphID, "minimal_vertex_separator", conn);
            log.info("ID {}: graph have been deleted", graphID);
        } catch (SQLException e) {
            log.error("ID {}: graph haven't been deleted", graphID, e);
            throw new RuntimeException(e);
        }
    }


    private void deleteVertices(int graphID, String tableName, Connection conn) {
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: vertices from {} haven't been deleted", graphID, tableName, e);
            throw new RuntimeException(e);
        }
    }

    private void deleteEdges(int graphID, String tableName, Connection conn) {
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: edges from {} haven't been deleted", graphID, tableName, e);
            throw new RuntimeException(e);
        }
    }

    private void deletePerfectEliminationOrder(int graphID, Connection conn) {
        deleteVertices(graphID, "perfect_elimination_order", conn);
    }

    private void deleteKuratowskiSubgraph(int graphID, Connection conn) {
        String sql = "DELETE FROM kuratowski_subgraph WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: subgraph hasn't been deleted", graphID, e);
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
                "blocks INT, is_planar BOOLEAN, is_chordal BOOLEAN, chromatic_number INT)";
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

    private void initDB() {
        try (Connection conn = dataSource.getConnection()) {
            initEdgesTable(conn);
            initVerticesTable(conn);
            initMetadataTable(conn);

            initGraphResearchInfoTable(conn);
            initArticulationPointsTable(conn);
            initBridgesTable(conn);
            initConnectedComponentsTable(conn);
            initBlocksTable(conn);
            initPerfectEliminationOrderTable(conn);
            initKuratowskiSubgraphTable(conn);
            initColoringTable(conn);
            initMaxCliqueTable(conn);
            initIndependentSetTable(conn);
            initMinimalVertexSeparatorTable(conn);
            log.info("Database initialization was successful");
        } catch (SQLException e) {
            log.error("Database haven't been initialized", e);
            throw new RuntimeException(e);
        }
    }

    private void deleteDB() {
        try (Connection conn = dataSource.getConnection()){
            String sql = "DROP TABLE articulation_points";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE blocks";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE bridges";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE connected_components";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE edges";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE graph_metadata";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE graph_research_info";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE perfect_elimination_order";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE vertices";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE kuratowski_subgraph";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE coloring";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE max_clique";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE independent_set";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE minimal_vertex_separator";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Delete database error", e);
            throw new RuntimeException(e);
        }
    }

    public void reloadDB() {
        deleteDB();
        initDB();
    }
}
