package com.graphResearcher.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphResearcher.model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;

import java.util.*;

import com.graphResearcher.util.ParseResearchInfo;
import com.graphResearcher.util.ParsingUtil;
import com.graphResearcher.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


@Repository
public class DataBaseManager {
    private static final Logger log = LoggerFactory.getLogger(DataBaseManager.class);
    private final Connection connection;
    public DataBaseManager() {
        try {
            String NAME = "db.username";
            String PASSWORD = "db.password";
            String URL = "db.url";
            connection = DriverManager.getConnection(PropertiesUtil.get(URL), PropertiesUtil.get(NAME), PropertiesUtil.get(PASSWORD));
        } catch (SQLException e) {
            log.error("DataBaseManager hasn't been created", e);
            throw new RuntimeException(e);
        }
        log.info("DataBaseManager has been created");
    }

    public int saveGraph(int userID, GraphModel graph) {
        String sql = "INSERT INTO graph_metadata(user_id, is_directed, is_weighted, has_self_loops, has_multiple_edges) " +
                "VALUES(" + userID + ", " + graph.getMetadata().isDirected + ", "
                + graph.getMetadata().isWeighted + ", " + graph.getMetadata().hasSelfLoops
                + ", " + graph.getMetadata().hasMultipleEdges + ") " +
                "RETURNING graph_id";
        int graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            graphID = rs.getInt("graph_id");
        } catch (SQLException e) {
            log.error("Graph hasn't been saved", e);
            throw new RuntimeException(e);
        }
        saveVertices(graphID, graph.getVertices());
        saveEdges(graphID, graph.getEdges());
        log.info("ID {}: graph has been saved", graphID);
        return graphID;
    }

    private void saveVertices(int graphID, List<Vertex> vertices) {
        saveVertices(graphID, vertices, "vertices");
    }

    private void saveVertices(int graphID, List<Vertex> vertices, String tableName) {
        String sql = "INSERT INTO " + tableName + "(graph_id, index, data) VALUES(?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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

    private void saveArticulationPoints(int graphID, List<Vertex> vertices) {
        saveVertices(graphID, vertices, "articulation_points");
    }

    private void saveEdges(int graphID, List<Edge> edges) {
        saveEdges(graphID, edges, "edges");
    }

    private void saveEdges(int graphID, List<Edge> edges, String tableName) {
        String sql = "INSERT INTO " + tableName + "(graph_id, source, target, weight, data) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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

    private void saveBridges(int graphID, List<Edge> edges) {
        saveEdges(graphID, edges, "bridges");
    }

    private void saveConnectedComponents(int graphID, List<List<Vertex>> components) {
        saveComponents(graphID, components, "connected_components");
    }

    private void saveBlocks(int graphID, List<List<Vertex>> blocks) {
        saveComponents(graphID, blocks, "blocks");
    }

    private void saveComponents(int graphID, List<List<Vertex>> components, String tableName) {
        String sql = "INSERT INTO " + tableName + "(graph_id, component_number, index, data) VALUES(?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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

    private void saveKuratowskiSubgraph(int userID, int graphID, GraphModel graphModel) {
        int subgraph_id = saveGraph(userID, graphModel);
        String sql = "INSERT INTO kuratowski_subgraph(graph_id, subgraph_id) VALUES(?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.setInt(2, subgraph_id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("ID {}: subgraph haven't been saved", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private void savePerfectEliminationOrder(int graphID, List<Vertex> perfectEliminationOrder) {
        String sql = "INSERT INTO perfect_elimination_order(graph_id, sequence_number, index, data) VALUES(?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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

    private void saveMaxClique(int graphID, List<Vertex> clique) {
        saveVertices(graphID, clique, "max_clique");
    }

    private void saveColoring(int graphID, List<List<Vertex>> coloring) {
        saveComponents(graphID, coloring, "coloring");
    }

    private void saveIndependentSet(int graphID, List<Vertex> independentSet) {
        saveVertices(graphID, independentSet, "independent_set");
    }

    private void saveMinimalVertexSeparator(int graphID, List<List<Vertex>> minimalVertexSeparator) {
        saveComponents(graphID, minimalVertexSeparator, "minimal_vertex_separator");
    }

    public void saveResearchInfo(int userID, int graphID, GraphResearchInfo info) {
        saveArticulationPoints(graphID, info.articulationPoints);
        saveBridges(graphID, info.bridges);
        saveConnectedComponents(graphID, info.connectedComponents);
        saveBlocks(graphID, info.blocks);
        if (info.isPlanar != null && !info.isPlanar) {
            saveKuratowskiSubgraph(userID, graphID, info.kuratowskiSubgraph);
        }
        if (info.isChordal != null && info.isChordal) {
            savePerfectEliminationOrder(graphID, info.perfectEliminationOrder);
            saveColoring(graphID, info.coloring);
            saveMaxClique(graphID, info.maxClique);
            saveIndependentSet(graphID, info.independentSet);
            saveMinimalVertexSeparator(graphID, info.minimalVertexSeparator);
        }

        ParseResearchInfo result = new ParseResearchInfo(info, graphID);

        String sql1 = "DELETE FROM graph_research_info WHERE graph_id = " + graphID;

        String sql2 = "INSERT INTO graph_research_info(" + "user_id, " + result.fieldsName + ") VALUES(" + userID + ", " + result.fields + ")";
        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            preparedStatement1.execute();

            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement2.execute();
        } catch (SQLException e) {
            log.error("ID {}: research info haven't been saved", graphID, e);
            throw new RuntimeException(e);
        }
        log.info("ID {}: research info have been saved", graphID);
    }

    public GraphResearchInfo getResearchInfo(int graphID) {
        String sql = "SELECT * FROM graph_research_info WHERE graph_id = " + graphID;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            GraphResearchInfo researchInfo = ParsingUtil.resultSetToGraphResearchInfo(rs);
            researchInfo.articulationPoints = getArticulationPoints(graphID);
            researchInfo.bridges = getBridges(graphID);
            researchInfo.connectedComponents = getConnectedComponents(graphID);
            researchInfo.blocks = getBlocks(graphID);
            if (researchInfo.isPlanar != null && !researchInfo.isPlanar) {
                researchInfo.kuratowskiSubgraph = getKuratowskiSubgraph(graphID);
            }
            if (researchInfo.isChordal != null && researchInfo.isChordal) {
                researchInfo.perfectEliminationOrder = getPerfectEliminationOrder(graphID);
                researchInfo.coloring = getColoring(graphID);
                researchInfo.maxClique = getMaxClique(graphID);
                researchInfo.independentSet = getIndependentSet(graphID);
                researchInfo.minimalVertexSeparator = getMinimalVertexSeparator(graphID);
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

    private List<List<Vertex>> getConnectedComponents(int graphID) {
        return getComponents(graphID, "connected_components");
    }

    private List<List<Vertex>> getComponents(int graphID, String tableName) {
        Map<Integer, List<Vertex>> componentsMap = new HashMap<>();
        String sql = "SELECT component_number, index, data FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (componentsMap.containsKey(rs.getInt("component_number"))) {
                    componentsMap.get(rs.getInt("component_number")).add(new Vertex(rs.getInt("index"), rs.getString("data")));
                } else {
                    componentsMap.put(rs.getInt("component_number"), new ArrayList<>(List.of(new Vertex(rs.getInt("index"), rs.getString("data")))));
                }
            }
        } catch(SQLException e) {
            log.error("ID {}: components haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
        return componentsMap.values().stream().toList();
    }

    private List<List<Vertex>> getBlocks(int graphID) {
        return getComponents(graphID, "blocks");
    }

    private GraphMetadata getGraphMetadata(int graphID) {
        String sql = "SELECT is_directed, is_weighted, has_self_loops, has_multiple_edges FROM graph_metadata WHERE graph_id = " + graphID;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return new GraphMetadata(rs.getBoolean("is_directed"), rs.getBoolean("is_weighted"), rs.getBoolean("has_self_loops"), rs.getBoolean("has_multiple_edges"));
        } catch (SQLException e) {
            log.error("ID {}: metadata haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    public GraphModel getGraph(int graphID) {
        return new GraphModel(getVertices(graphID), getEdges(graphID), getGraphMetadata(graphID));
    }

    private List<Vertex> getVertices(int graphID) {
        return getVertices(graphID, "vertices");
    }

    private List<Vertex> getArticulationPoints(int graphID) {
        return getVertices(graphID, "articulation_points");
    }

    private List<Vertex> getVertices(int graphID, String tableName) {
        List<Vertex> vertices = new ArrayList<>();
        String sql = "SELECT index, data FROM " + tableName  + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                vertices.add(new Vertex(rs.getInt("index"), rs.getString("data")));
            }
        } catch(SQLException e) {
            log.error("ID {}: vertices haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
        return vertices;
    }

    private List<Edge> getEdges(int graphID) {
        return getEdges(graphID, "edges");
    }

    private List<Edge> getBridges(int graphID) {
        return getEdges(graphID, "bridges");
    }

    private List<Edge> getEdges(int graphID, String tableName) {
        List<Vertex> vertices = getVertices(graphID);
        Map<Integer, Vertex> verticesMap = new HashMap<>();
        for (Vertex v: vertices) {
            verticesMap.put(v.getIndex(), v);
        }
        List<Edge> edges = new ArrayList<>();
        String sql = "SELECT source, target, weight, data FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Vertex s = verticesMap.get(rs.getInt("source"));
                Vertex t = verticesMap.get(rs.getInt("target"));
                edges.add(new Edge(s, t, rs.getInt("weight"), rs.getString("data")));
            }
        } catch (SQLException e) {
            log.error("ID {}: edges haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
        return edges;
    }

    private List<Vertex> getPerfectEliminationOrder(int graphID) {
        List<Vertex> vertices = new ArrayList<>();
        for (int i = 0; i < getVertices(graphID).size(); ++i) {
            vertices.add(new Vertex());
        }
        String sql = "SELECT index, data, sequence_number FROM perfect_elimination_order WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                vertices.set(rs.getInt("sequence_number"), new Vertex(rs.getInt("index"), rs.getString("data")));
            }
        } catch(SQLException e) {
            log.error("ID {}: vertices haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
        return vertices;
    }

    private GraphModel getKuratowskiSubgraph(int graphID) {
        String sql = "SELECT subgraph_id FROM kuratowski_subgraph WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return getGraph(rs.getInt("subgraph_id"));
        } catch(SQLException e) {
            log.error("ID {}: subgraph haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private List<List<Vertex>> getColoring(int graphID) {
        return getComponents(graphID, "coloring");
    }

    private List<Vertex> getMaxClique(int graphID) {
        return getVertices(graphID, "max_clique");
    }

    private List<Vertex> getIndependentSet(int graphID) {
        return getVertices(graphID, "independent_set");
    }

    private List<List<Vertex>> getMinimalVertexSeparator(int graphID) {
        return getComponents(graphID, "minimal_vertex_separator");
    }

    public void createUser(int userID) {
        //TODO
    }

    public void deleteUser(int userID) {
        deleteAllUserGraphs(userID);
    }

    private void deleteAllUserGraphs(int userID) {
        String sql1 = "SELECT graph_id FROM graph_metadata WHERE user_id = " + userID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql1)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int graphID = rs.getInt("graph_id");
                deleteGraph(graphID);
            }
        } catch (SQLException e) {
            log.error("All user{} graphs haven't been deleted", userID, e);
            throw new RuntimeException(e);
        }

        String sql2 = "DELETE FROM graph_metadata WHERE user_id = " + userID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql2)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("All user{} graphs haven't been deleted", userID, e);
            throw new RuntimeException(e);
        }
        
        String sql3 = "DELETE FROM graph_research_info WHERE user_id = " + userID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql3)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("User{} researches haven't been deleted", userID, e);
            throw new RuntimeException(e);
        }
        log.info("All user{} graphs have been deleted", userID);
    }

    public void deleteGraph(int graphID) {
        String sql = "DELETE FROM graph_metadata WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: graph haven't been deleted", graphID, e);
            throw new RuntimeException(e);
        }
        deleteVertices(graphID);
        deleteEdges(graphID);
        deleteArticulationPoints(graphID);
        deleteBridges(graphID);
        deleteConnectedComponents(graphID);
        deleteBlocks(graphID);
        deletePerfectEliminationOrder(graphID);
        deleteKuratowskiSubgraph(graphID);
        deleteColoring(graphID);
        deleteMaxClique(graphID);
        deleteIndependentSet(graphID);
        deleteMinimalVertexSeparator(graphID);
        log.info("ID {}: graph have been deleted", graphID);
    }

    private void deleteVertices(int graphID) {
        deleteVertices(graphID, "vertices");
    }

    private void deleteVertices(int graphID, String tableName) {
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: vertices from {} haven't been deleted", graphID, tableName, e);
            throw new RuntimeException(e);
        }
    }

    private void deleteArticulationPoints(int graphID) {
        deleteVertices(graphID, "articulation_points");
    }

    private void deleteEdges(int graphID) {
        deleteEdges(graphID, "edges");
    }

    private void deleteEdges(int graphID, String tableName) {
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: edges from {} haven't been deleted", graphID, tableName, e);
            throw new RuntimeException(e);
        }
    }

    private void deleteBridges(int graphID) {
        deleteEdges(graphID, "bridges");
    }

    private void deleteConnectedComponents(int graphID) {
        deleteVertices(graphID, "connected_components");
    }

    private void deleteBlocks(int graphID) {
        deleteVertices(graphID, "blocks");
    }

    private void deletePerfectEliminationOrder(int graphID) {
        deleteVertices(graphID, "perfect_elimination_order");
    }

    private void deleteKuratowskiSubgraph(int graphID) {
        String sql = "DELETE FROM kuratowski_subgraph WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: subgraph hasn't been deleted", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private void deleteColoring(int graphID) {
        deleteVertices(graphID, "coloring");
    }

    private void deleteMaxClique(int graphID) {
        deleteVertices(graphID, "max_clique");
    }

    private void deleteIndependentSet(int graphID) {
        deleteVertices(graphID, "independent_set");
    }

    private void deleteMinimalVertexSeparator(int graphID) {
        deleteVertices(graphID, "minimal_vertex_separator");
    }

    private void initVerticesTable() {
        String sql = "CREATE TABLE vertices(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init vertices table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initEdgesTable() {
        String sql = "CREATE TABLE edges(id SERIAL PRIMARY KEY, graph_id INT, source INT, target INT, weight DOUBLE PRECISION, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init edges table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initMetadataTable() {
        String sql = "CREATE TABLE graph_metadata(graph_id SERIAL PRIMARY KEY, user_id INT, is_directed BOOLEAN, is_weighted BOOLEAN, has_self_loops BOOLEAN, has_multiple_edges BOOLEAN)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init metadata table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initGraphResearchInfoTable() {
        String sql = "CREATE TABLE graph_research_info(id SERIAL PRIMARY KEY, " +
                "graph_id INT, user_id INT, is_connected BOOLEAN, is_biconnected BOOLEAN, " +
                "articulation_points INT, bridges INT, connected_components INT, " +
                "blocks INT, is_planar BOOLEAN, kuratovsky_sub_graph_id INT, is_chordal BOOLEAN, chromatic_number INT)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init graphResearchInfo table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initArticulationPointsTable() {
        String sql = "CREATE TABLE articulation_points(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init articulationPoints table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initBridgesTable() {
        String sql = "CREATE TABLE bridges(id SERIAL PRIMARY KEY, graph_id INT, source INT, target INT, weight DOUBLE PRECISION, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init bridges table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initConnectedComponentsTable() {
        String sql = "CREATE TABLE connected_components(id SERIAL PRIMARY KEY, graph_id INT, component_number INT, index INT, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init connected components table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initBlocksTable() {
        String sql = "CREATE TABLE blocks(id SERIAL PRIMARY KEY, graph_id INT, component_number INT, index INT, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init blocks table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initPerfectEliminationOrderTable() {
        String sql = "CREATE TABLE perfect_elimination_order(id SERIAL PRIMARY KEY, graph_id INT, sequence_number INT, index INT, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init perfectEliminationOrder table error", e);
            throw new RuntimeException(e);
        }
    }
    
    private void initKuratowskiSubgraphTable() {
        String sql = "CREATE TABLE kuratowski_subgraph(id SERIAL PRIMARY KEY, graph_id INT, subgraph_id INT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init Kuratowski subgraph table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initColoringTable() {
        String sql = "CREATE TABLE coloring(id SERIAL PRIMARY KEY, graph_id INT, component_number INT, index INT, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init coloring table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initMaxCliqueTable() {
        String sql = "CREATE TABLE max_clique(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init max clique table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initIndependentSetTable() {
        String sql = "CREATE TABLE independent_set(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init independent_set table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initMinimalVertexSeparatorTable() {
        String sql = "CREATE TABLE minimal_vertex_separator(id SERIAL PRIMARY KEY, graph_id INT, component_number INT, index INT, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init minimal_vertex_separator table error", e);
            throw new RuntimeException(e);
        }
    }
    
    public void initDB() {
        initEdgesTable();
        initVerticesTable();
        initMetadataTable();

        initGraphResearchInfoTable();
        initArticulationPointsTable();
        initBridgesTable();
        initConnectedComponentsTable();
        initBlocksTable();
        initPerfectEliminationOrderTable();
        initKuratowskiSubgraphTable();
        initColoringTable();
        initMaxCliqueTable();
        initIndependentSetTable();
        initMinimalVertexSeparatorTable();
        log.info("Initialization was successful");
    }
    
    public void deleteDB() {
        try {
            String sql = "DROP TABLE articulation_points";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE blocks";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE bridges";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE connected_components";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE edges";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE graph_metadata";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE graph_research_info";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE perfect_elimination_order";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE vertices";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE kuratowski_subgraph";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE coloring";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE max_clique";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE independent_set";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            sql = "DROP TABLE minimal_vertex_separator";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Delete db error", e);
            throw new RuntimeException(e);
        }
    }
    
    public void reloadDB() {
        deleteDB();
        initDB();
    }
}
