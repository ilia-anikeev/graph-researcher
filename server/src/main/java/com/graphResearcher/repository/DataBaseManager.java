package com.graphResearcher.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphResearcher.model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

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
        String tableName = "graph_metadata";
        String sql = "INSERT INTO " + tableName + "(user_id, is_directed, is_weighted, has_self_loops, has_multiple_edges)" +
                "VALUES(" + userID + ", " + graph.getMetadata().isDirected + ", " + graph.getMetadata().isWeighted + ", "
                + graph.getMetadata().hasSelfLoops + ", " + graph.getMetadata().hasMultipleEdges + ") " +
                "RETURNING graph_id";
        int graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            graphID = rs.getInt("graph_id");
            saveVertices(graphID, graph.getVertices());
            saveEdges(graphID, graph.getEdges());
        } catch (SQLException e) {
            log.error("Graph hasn't been saved", e);
            throw new RuntimeException(e);
        }
        log.info("Graph " + graphID + " has been saved");
        return graphID;
    }

    private void saveVertices(int graphID, List<Vertex> vertices) {
        saveVertices(graphID, vertices, "vertices");
    }

    private void saveVertices(int graphID, List<Vertex> vertices, String tableName) {
        String sql = "INSERT INTO " + tableName + "(graph_id, index, data) VALUES(?, ?, ?)";
        try (PreparedStatement insertVertexStatement = connection.prepareStatement(sql)) {
            for (Vertex v: vertices) {
                insertVertexStatement.setInt(1, graphID);
                insertVertexStatement.setInt(2, v.getIndex());
                insertVertexStatement.setString(3, v.getData());
                insertVertexStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Vertices haven't been saved", e);
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
        String insertEdgeSql = "INSERT INTO " + tableName + "(graph_id, source, target, weight, data) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement insertEdgeStatement = connection.prepareStatement(insertEdgeSql)) {
            for (Edge e: edges) {
                insertEdgeStatement.setInt(1, graphID);
                insertEdgeStatement.setInt(2, e.getSource().getIndex());
                insertEdgeStatement.setInt(3, e.getTarget().getIndex());
                insertEdgeStatement.setDouble(4, e.getWeight());
                insertEdgeStatement.setString(5, e.getData());
                insertEdgeStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Edges haven't been saved", e);
            throw new RuntimeException(e);
        }
    }

    private void saveBridges(int graphID, List<Edge> edges) {
        saveEdges(graphID, edges, "bridges");
    }

    private void saveConnectedComponents(int userID, int graphID, List<GraphModel> listGraphModels) {
        saveSubgraphs(userID, graphID, listGraphModels, "connected_components");
    }

    private void saveSubgraphs(int userID, int graphID, List<GraphModel> listGraphModels, String tableName) {
        List<Integer> subgraphIDs = new ArrayList<>();
        for (GraphModel subgraph: listGraphModels) {
            subgraphIDs.add(saveGraph(userID, subgraph));
        }

        String sql = "INSERT INTO " + tableName + "(graph_id, subgraph_id) VALUES(?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (Integer subgraphID: subgraphIDs) {
                preparedStatement.setInt(1, graphID);
                preparedStatement.setInt(2, subgraphID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Subgraphs haven't been saved", e);
            throw new RuntimeException(e);
        }
    }
    private void saveBlocks(int userID, int graphID, List<GraphModel> listGraphModels) {
        saveSubgraphs(userID, graphID, listGraphModels, "blocks");
    }
    private void saveKuratowskiSubgraph(int userID, int graphID, GraphModel graphModel) {
        int subgraph_id = saveGraph(userID, graphModel);
        String tableName = "kuratowski_subgraph";
        String sql = "INSERT INTO " + tableName + "(graph_id, subgraph_id) VALUES(?, ?)";
        try (PreparedStatement insertVertexStatement = connection.prepareStatement(sql)) {
                insertVertexStatement.setInt(1, graphID);
                insertVertexStatement.setInt(2, subgraph_id);
                insertVertexStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("Kuratowski subgraph haven't been saved", e);
            throw new RuntimeException(e);
        }
    }

    private void savePerfectEliminationOrder(int graphID, List<Vertex> perfectEliminationOrder) {
        String tableName = "perfect_elimination_order";
        String sql = "INSERT INTO " + tableName + "(graph_id, sequence_number, index, data) VALUES(?, ?, ?, ?)";
        try (PreparedStatement insertVertexStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < perfectEliminationOrder.size(); ++i) {
                insertVertexStatement.setInt(1, graphID);
                insertVertexStatement.setInt(2, i);
                insertVertexStatement.setInt(3, perfectEliminationOrder.get(i).getIndex());
                insertVertexStatement.setString(4, perfectEliminationOrder.get(i).getData());
                insertVertexStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Vertices haven't been saved", e);
            throw new RuntimeException(e);
        }
    }

    public void saveResearchInfo(int userID, int graphID, GraphResearchInfo info) {
        String tableName = "graph_research_info";

        saveArticulationPoints(graphID, info.articulationPoints);
        saveBridges(graphID, info.bridges);
        saveConnectedComponents(userID, graphID, info.connectedComponents);
        saveBlocks(userID, graphID, info.blocks);
        if (info.isPlanar != null && !info.isPlanar)
            saveKuratowskiSubgraph(userID, graphID, info.kuratowskiSubgraph);

        if (info.isChordal != null && info.isChordal)
            savePerfectEliminationOrder(graphID, info.perfectEliminationOrder);


        ParseResearchInfo result = new ParseResearchInfo(info, graphID);

        String sql1 = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;

        String sql2 = "INSERT INTO " + tableName + "(" + "user_id, " + result.fieldsName + ") VALUES(" + userID + ", " + result.fields + ")";

        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            preparedStatement1.execute();

            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement2.execute();
        } catch (SQLException e) {
            log.error("Graph research info haven't been saved", e);
            throw new RuntimeException(e);
        }
        log.info("Graph" + graphID + " research info have been saved");
    }

    public GraphResearchInfo getResearchInfo(int graphID) {
        String tableName = "graph_research_info";
        String sql = "SELECT * FROM " + tableName + " WHERE graph_id = " + graphID;
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
            }


            log.info("Graph" + graphID + " research info have been received");
            return researchInfo;
        } catch (SQLException e) {
            log.error("Graph" + graphID + " research info haven't been received", e);
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("Json parse error", e);
            throw new RuntimeException(e);
        }
    }

    private List<GraphModel> getConnectedComponents(int graphID) {
        return getSubgraphs(graphID, "connected_components");
    }

    private List<GraphModel> getBlocks(int graphID) {
        return getSubgraphs(graphID, "blocks");
    }

    private List<GraphModel> getSubgraphs(int graphID, String tableName) {
        List<GraphModel> subgraphs = new ArrayList<>();
        String sql = "SELECT subgraph_id FROM " + tableName  + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                subgraphs.add(getGraph(rs.getInt("subgraph_id")));
            }
        } catch(SQLException e) {
            log.error("Subgraphs haven't been received", e);
            throw new RuntimeException(e);
        }
        return subgraphs;
    }


    private GraphMetadata getGraphMetadata(int graphID) {
        String tableName = "graph_metadata";
        String sql = "SELECT is_directed, is_weighted, has_self_loops, has_multiple_edges FROM " + tableName + " WHERE graph_id = " + graphID;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return new GraphMetadata(rs.getBoolean("is_directed"), rs.getBoolean("is_weighted"), rs.getBoolean("has_self_loops"), rs.getBoolean("has_multiple_edges"));
        } catch (SQLException e) {
            log.error("Graph" + graphID + " metadata haven't been received", e);
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
            log.error("Vertices haven't been received", e);
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
        TreeMap<Integer, Vertex> verticesMap = new TreeMap<>();
        for (Vertex v: vertices) {
            verticesMap.put(v.getIndex(), v);
        }
        ArrayList<Edge> edges = new ArrayList<>();
        String sql = "SELECT source, target, weight, data FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Vertex s = verticesMap.get(rs.getInt("source"));
                Vertex t = verticesMap.get(rs.getInt("target"));
                edges.add(new Edge(s, t, rs.getInt("weight"), rs.getString("data")));
            }
        } catch (SQLException e) {
            log.error("Edges haven't been received", e);
            throw new RuntimeException(e);
        }
        return edges;
    }

    private List<Vertex> getPerfectEliminationOrder(int graphID) {
        List<Vertex> vertices = new ArrayList<>();
        for (int i = 0; i < getVertices(graphID).size(); ++i) {
            vertices.add(new Vertex());
        }
        String tableName = "perfect_elimination_order";
        String sql = "SELECT index, data, sequence_number FROM " + tableName  + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                vertices.set(rs.getInt("sequence_number"), new Vertex(rs.getInt("index"), rs.getString("data")));
            }
        } catch(SQLException e) {
            log.error("Vertices haven't been received", e);
            throw new RuntimeException(e);
        }
        return vertices;
    }

    private GraphModel getKuratowskiSubgraph(int graphID) {
        String tableName = "kuratowski_subgraph";
        String sql = "SELECT subgraph_id FROM " + tableName  + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return getGraph(rs.getInt("subgraph_id"));
        } catch(SQLException e) {
            log.error("Vertices haven't been received", e);
            throw new RuntimeException(e);
        }
    }

    public void createUser(int userID) {
        //TODO
    }

    public void deleteUser(int userID) {
        deleteAllUserGraphs(userID);
    }

    private void deleteAllUserGraphs(int userID) {
        String tableName = "graph_metadata";
        String sql1 = "SELECT graph_id FROM " + tableName + " WHERE user_id = " + userID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql1)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int graphID = rs.getInt("graph_id");
                deleteGraph(graphID);
            }
        } catch (SQLException e) {
            log.error("All user" + userID + " graphs haven't been deleted", e);
            throw new RuntimeException(e);
        }

        String sql2 = "DELETE FROM " + tableName + " WHERE user_id = " + userID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql2)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("All user" + userID + " graphs haven't been deleted", e);
            throw new RuntimeException(e);
        }
        tableName = "graph_research_info";
        String sql3 = "DELETE FROM " + tableName + " WHERE user_id = " + userID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql3)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("User researches haven't been deleted", e);
            throw new RuntimeException(e);
        }
        log.info("All user" + userID + " graphs have been deleted");
    }

    public void deleteGraph(int graphID) {
        String tableName = "graph_metadata";
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Graph " + graphID + " haven't been deleted", e);
            throw new RuntimeException(e);
        }
        log.info("Graph " + graphID + " have been deleted");
        deleteVertices(graphID);
        deleteEdges(graphID);
        deleteArticulationPoints(graphID);
        deleteBridges(graphID);
        deleteConnectedComponents(graphID);
        deleteBlocks(graphID);
        deletePerfectEliminationOrder(graphID);
        deleteKuratowskiSubgraph(graphID);
    }

    private void deleteVertices(int graphID) {
        deleteVertices(graphID, "vertices");
    }

    private void deleteVertices(int graphID, String tableName) {
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Vertices haven't been deleted", e);
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
            log.error("Edges haven't been deleted", e);
            throw new RuntimeException(e);
        }
    }

    private void deleteBridges(int graphID) {
        deleteEdges(graphID, "bridges");
    }

    private void deleteConnectedComponents(int graphID) {
        deleteSubgraphs(graphID, "connected_components");
    }
    private void deleteSubgraphs(int graphID, String tableName) {
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Subgraphs haven't been deleted", e);
            throw new RuntimeException(e);
        }
    }

    private void deleteBlocks(int graphID) {
        deleteSubgraphs(graphID, "blocks");
    }

    private void deletePerfectEliminationOrder(int graphID) {
        deleteVertices(graphID, "perfect_elimination_order");
    }

    private void deleteKuratowskiSubgraph(int graphID) {
        String tableName = "kuratowski_subgraph";
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Kuratowski subgraph hasn't been deleted", e);
            throw new RuntimeException(e);
        }
    }

    private void initVerticesTable() {
        String tableName = "vertices";

        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException e) {
            log.error("Init vertices table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initEdgesTable() {
        String tableName = "edges";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, graph_id INT, source INT, target INT, weight DOUBLE PRECISION, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init edges table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initMetadataTable() {
        String tableName = "graph_metadata";
        String sql = "CREATE TABLE " + tableName + "(graph_id SERIAL PRIMARY KEY, user_id INT, is_directed BOOLEAN, is_weighted BOOLEAN, has_self_loops BOOLEAN, has_multiple_edges BOOLEAN)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init metadata table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initGraphResearchInfoTable() {
        String tableName = "graph_research_info";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, " +
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
        String tableName = "articulation_points";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException e) {
            log.error("Init articulationPoints table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initBridgesTable() {
        String tableName = "bridges";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, graph_id INT, source INT, target INT, weight DOUBLE PRECISION, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init bridges table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initConnectedComponentsTable() {
        String tableName = "connected_components";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, graph_id INT, subgraph_id INT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init connected components table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initBlocksTable() {
        String tableName = "blocks";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, graph_id INT, subgraph_id INT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Init blocks table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initPerfectEliminationOrderTable() {
        String tableName = "perfect_elimination_order";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, graph_id INT, sequence_number INT, index INT, data TEXT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException e) {
            log.error("Init perfectEliminationOrder table error", e);
            throw new RuntimeException(e);
        }
    }

    private void initKuratowskiSubgraphTable() {
        String tableName = "kuratowski_subgraph";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, graph_id INT, subgraph_id INT)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException e) {
            log.error("Init Kuratowski subgraph table error", e);
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

        log.info("Initialization was successful");
    }
    public void deleteDB() {
        try {
            String tableName = "articulation_points";
            String sql = "DROP TABLE " + tableName;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            tableName = "blocks";
            sql = "DROP TABLE " + tableName;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            tableName = "bridges";
            sql = "DROP TABLE " + tableName;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            tableName = "connected_components";
            sql = "DROP TABLE " + tableName;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            tableName = "edges";
            sql = "DROP TABLE " + tableName;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            tableName = "graph_metadata";
            sql = "DROP TABLE " + tableName;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            tableName = "graph_research_info";
            sql = "DROP TABLE " + tableName;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            tableName = "perfect_elimination_order";
            sql = "DROP TABLE " + tableName;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            tableName = "vertices";
            sql = "DROP TABLE " + tableName;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            tableName = "kuratowski_subgraph";
            sql = "DROP TABLE " + tableName;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException e) {
            log.error("Init perfectEliminationOrder table error", e);
            throw new RuntimeException(e);
        }
    }
    public void reloadDB() {
        deleteDB();
        initDB();
    }
}
