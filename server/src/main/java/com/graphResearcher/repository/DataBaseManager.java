package com.graphResearcher.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphResearcher.model.*;

import java.sql.*;

import java.util.*;

import com.graphResearcher.util.Converter;
import com.graphResearcher.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@Repository
public class DataBaseManager {
    private static final Logger log = LoggerFactory.getLogger(DataBaseManager.class);
    DataSource dataSource;
    public DataBaseManager() {
        String name = PropertiesUtil.get("db.username");
        String password = PropertiesUtil.get("db.password");
        String url = PropertiesUtil.get("db.url");
        dataSource = new DriverManagerDataSource(url, name, password);
    }

    public int saveGraph(int userID, GraphModel graph) {
        String sql = "INSERT INTO graph_metadata(user_id, is_directed, is_weighted, has_self_loops, has_multiple_edges) " +
                "VALUES(?, ?, ?, ?, ?)" +
                "RETURNING graph_id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.setBoolean(2, graph.getMetadata().isDirected);
            preparedStatement.setBoolean(3, graph.getMetadata().isWeighted);
            preparedStatement.setBoolean(4, graph.getMetadata().hasSelfLoops);
            preparedStatement.setBoolean(5, graph.getMetadata().hasMultipleEdges);

            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int graphID = rs.getInt("graph_id");
            saveVertices(graphID, graph.getVertices(), "vertices", conn);
            saveEdges(graphID, graph.getEdges(), "edges", conn);
            log.info("ID {}: graph has been saved", graphID);
            return graphID;
        } catch (SQLException e) {
            log.error("Graph hasn't been saved", e);
            throw new RuntimeException(e);
        }
    }

    private void saveVertices(int graphID, List<Vertex> vertices, String tableName, Connection conn) {
        String sql = "INSERT INTO " + tableName + "(graph_id, index, data) VALUES(?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (Vertex v: vertices) {
                preparedStatement.setInt(1, graphID);
                preparedStatement.setInt(2, v.getIndex());
                preparedStatement.setString(3, v.getData());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("ID {}: vertices haven't been saved", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private void saveEdges(int graphID, List<Edge> edges, String tableName, Connection conn) {
        String sql = "INSERT INTO " + tableName + "(graph_id, source, target, weight, data) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (Edge e: edges) {
                preparedStatement.setInt(1, graphID);
                preparedStatement.setInt(2, e.getSource().getIndex());
                preparedStatement.setInt(3, e.getTarget().getIndex());
                preparedStatement.setDouble(4, e.getWeight());
                preparedStatement.setString(5, e.getData());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("ID {}: edges haven't been saved", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private void saveComponents(int graphID, List<List<Vertex>> components, String tableName, Connection conn) {
        String sql = "INSERT INTO " + tableName + "(graph_id, component_number, index, data) VALUES(?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (int i = 0; i < components.size(); ++i) {
                for (Vertex v: components.get(i)) {
                    preparedStatement.setInt(1, graphID);
                    preparedStatement.setInt(2, i);
                    preparedStatement.setInt(3, v.getIndex());
                    preparedStatement.setString(4, v.getData());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            log.error("ID {}: components haven't been saved", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private void saveKuratowskiSubgraph(int userID, int graphID, GraphModel graphModel, Connection conn) {
        int subgraph_id = saveGraph(userID, graphModel);
        String sql = "INSERT INTO kuratowski_subgraph(graph_id, subgraph_id) VALUES(?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.setInt(2, subgraph_id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("ID {}: subgraph haven't been saved", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private void savePerfectEliminationOrder(int graphID, List<Vertex> perfectEliminationOrder, Connection conn) {
        String sql = "INSERT INTO perfect_elimination_order(graph_id, sequence_number, index, data) VALUES(?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (int i = 0; i < perfectEliminationOrder.size(); ++i) {
                preparedStatement.setInt(1, graphID);
                preparedStatement.setInt(2, i);
                preparedStatement.setInt(3, perfectEliminationOrder.get(i).getIndex());
                preparedStatement.setString(4, perfectEliminationOrder.get(i).getData());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("ID {}: vertices haven't been saved", graphID, e);
            throw new RuntimeException(e);
        }
    }

    public void saveResearchInfo(int userID, int graphID, GraphResearchInfo info) {
        try (Connection conn = dataSource.getConnection()) {
            saveVertices(graphID, info.articulationPoints, "articulation_points", conn);
            saveEdges(graphID, info.bridges, "bridges",conn);
            saveComponents(graphID, info.connectedComponents, "connected_components", conn);
            saveComponents(graphID, info.blocks, "blocks", conn);
            if (info.isPlanar != null && !info.isPlanar) {
                saveKuratowskiSubgraph(userID, graphID, info.kuratowskiSubgraph, conn);
            }
            if (info.isChordal != null && info.isChordal) {
                savePerfectEliminationOrder(graphID, info.perfectEliminationOrder, conn);
                saveComponents(graphID, info.coloring, "coloring", conn);
                saveVertices(graphID, info.maxClique, "max_clique", conn);
                saveVertices(graphID, info.independentSet, "independent_set", conn);
                saveComponents(graphID, info.minimalVertexSeparator, "minimal_vertex_separator", conn);
            }

            String sql1 = "DELETE FROM graph_research_info WHERE graph_id = ?";

            String sql2 = "INSERT INTO graph_research_info(user_id, " + Converter.getFieldsNames(info) + ") VALUES(?, " + Converter.getFields(info, graphID) + ")";

            PreparedStatement preparedStatement1 = conn.prepareStatement(sql1);
            preparedStatement1.setInt(1, graphID);

            PreparedStatement preparedStatement2 = conn.prepareStatement(sql2);
            preparedStatement2.setInt(1, userID);
            preparedStatement1.execute();

            preparedStatement2.execute();
            log.info("ID {}: research info have been saved", graphID);
        } catch (SQLException e) {
            log.error("ID {}: research info haven't been saved", graphID, e);
            throw new RuntimeException(e);
        }
    }

    public GraphResearchInfo getResearchInfo(int graphID) {
        String sql = "SELECT * FROM graph_research_info WHERE graph_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setInt(1, graphID);

            ResultSet rs = preparedStatement.executeQuery();
            GraphResearchInfo researchInfo = Converter.resultSetToGraphResearchInfo(rs);

            researchInfo.articulationPoints = getVertices(graphID, "articulation_points", conn);
            researchInfo.bridges = getEdges(graphID, "bridges", conn);
            researchInfo.connectedComponents = getComponents(graphID, "connected_components", conn);
            researchInfo.blocks = getComponents(graphID, "blocks", conn);
            if (researchInfo.isPlanar != null && !researchInfo.isPlanar) {
                researchInfo.kuratowskiSubgraph = getKuratowskiSubgraph(graphID, conn);
            }
            if (researchInfo.isChordal != null && researchInfo.isChordal) {
                researchInfo.perfectEliminationOrder = getPerfectEliminationOrder(graphID, conn);
                researchInfo.coloring = getComponents(graphID, "coloring", conn);
                researchInfo.maxClique = getVertices(graphID, "max_clique", conn);
                researchInfo.independentSet = getVertices(graphID, "independent_set", conn);
                researchInfo.minimalVertexSeparator = getComponents(graphID, "minimal_vertex_separator", conn);
            }
            log.info("ID {}: research info have been received", graphID);
            return researchInfo;
        } catch (SQLException e) {
            log.error("ID {}: research info haven't been received", graphID, e);
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("ID {}: json parse error", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private List<List<Vertex>> getComponents(int graphID, String tableName, Connection conn) {
        Map<Integer, List<Vertex>> componentsMap = new HashMap<>();
        String sql = "SELECT component_number, index, data FROM " + tableName + " WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Vertex v = new Vertex(rs.getInt("index"), rs.getString("data"));
                if (componentsMap.containsKey(rs.getInt("component_number"))) {
                    componentsMap.get(rs.getInt("component_number")).add(v);
                } else {
                    componentsMap.put(rs.getInt("component_number"), new ArrayList<>(List.of(v)));
                }
            }
            return componentsMap.values().stream().toList();
        } catch(SQLException e) {
            log.error("ID {}: components haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private GraphMetadata getGraphMetadata(int graphID, Connection conn) {
        String sql = "SELECT is_directed, is_weighted, has_self_loops, has_multiple_edges FROM graph_metadata WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setInt(1, graphID);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return new GraphMetadata(rs.getBoolean("is_directed"), rs.getBoolean("is_weighted"), rs.getBoolean("has_self_loops"), rs.getBoolean("has_multiple_edges"));
        } catch (SQLException e) {
            log.error("ID {}: metadata haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    public GraphModel getGraph(int graphID) {
        try (Connection conn = dataSource.getConnection()) {
            return new GraphModel(getVertices(graphID, "vertices", conn), getEdges(graphID, "edges", conn), getGraphMetadata(graphID, conn));
        } catch (SQLException e) {
            log.error("ID {}: graph haven't been received", graphID);
            throw new RuntimeException(e);
        }
    }

    private List<Vertex> getVertices(int graphID, String tableName, Connection conn) {
        String sql = "SELECT index, data FROM " + tableName  + " WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            ResultSet rs = preparedStatement.executeQuery();
            List<Vertex> vertices = new ArrayList<>();
            while (rs.next()) {
                vertices.add(new Vertex(rs.getInt("index"), rs.getString("data")));
            }
            return vertices;
        } catch(SQLException e) {
            log.error("ID {}: vertices haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private List<Edge> getEdges(int graphID, String tableName, Connection conn) {
        List<Vertex> vertices = getVertices(graphID, "vertices", conn);
        Map<Integer, Vertex> verticesMap = new HashMap<>();
        for (Vertex v: vertices) {
            verticesMap.put(v.getIndex(), v);
        }
        List<Edge> edges = new ArrayList<>();
        String sql = "SELECT source, target, weight, data FROM " + tableName + " WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Vertex s = verticesMap.get(rs.getInt("source"));
                Vertex t = verticesMap.get(rs.getInt("target"));
                edges.add(new Edge(s, t, rs.getInt("weight"), rs.getString("data")));
            }
            return edges;
        } catch (SQLException e) {
            log.error("ID {}: edges haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private List<Vertex> getPerfectEliminationOrder(int graphID, Connection conn) {
        List<Vertex> vertices = new ArrayList<>();
        for (int i = 0; i < getVertices(graphID, "vertices", conn).size(); ++i) {
            vertices.add(new Vertex());
        }
        String sql = "SELECT index, data, sequence_number FROM perfect_elimination_order WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                vertices.set(rs.getInt("sequence_number"), new Vertex(rs.getInt("index"), rs.getString("data")));
            }
            return vertices;
        } catch(SQLException e) {
            log.error("ID {}: vertices haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private GraphModel getKuratowskiSubgraph(int graphID, Connection conn) {
        String sql = "SELECT subgraph_id FROM kuratowski_subgraph WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return getGraph(rs.getInt("subgraph_id"));
        } catch(SQLException e) {
            log.error("ID {}: subgraph haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
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
        String sql = "CREATE TABLE graph_metadata(graph_id SERIAL PRIMARY KEY, user_id INT, is_directed BOOLEAN, is_weighted BOOLEAN, has_self_loops BOOLEAN, has_multiple_edges BOOLEAN)";
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
