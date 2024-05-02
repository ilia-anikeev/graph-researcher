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
            log.error("DataBaseManager hasn't been created");
            throw new RuntimeException(e);
        }
        log.info("DataBaseManager has been created");
    }

    public void saveResearchInfo(int userID, int graphID, GraphResearchInfo info) {

        String tableName = "graph_research_info";
        ParseResearchInfo result = new ParseResearchInfo(info, graphID);

        String sql1 = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;

        String sql2 = "INSERT INTO " + tableName + "(" + "user_id, " + result.fieldsName + ") VALUES(" + userID + ", " + result.fields + ")";

        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql1);
            preparedStatement1.execute();

            PreparedStatement preparedStatement2 = connection.prepareStatement(sql2);
            preparedStatement2.execute();

        } catch (SQLException e) {
            log.error("Graph research info haven't been saved");
            throw new RuntimeException(e);
        }
    }

    public int saveGraph(int userID, GraphModel graph) {
        String tableName = "graph_metadata";
        String sql = "INSERT INTO " + tableName + "(user_id, is_directed, is_weighted, has_self_loops, has_multiple_edges)" +
                "VALUES(" + userID + ", " + graph.metadata.isDirected + ", " + graph.metadata.isWeighted + ", "
                + graph.metadata.hasSelfLoops + ", " + graph.metadata.hasMultipleEdges + ") " +
                "RETURNING graph_id";
        int graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            graphID = rs.getInt("graph_id");
            saveVertices(graphID, graph.getVertices());
            saveEdges(graphID, graph.getEdges());
        } catch (SQLException e) {
            log.error("Graph hasn't been saved");
            throw new RuntimeException(e);
        }
        log.info("Graph has been saved");
        return graphID;
    }

    private void saveVertices(int graphID, List<Vertex> vertices) {
        String tableName = "vertices";
        String sql = "INSERT INTO " + tableName + "(graph_id, index, data) VALUES(?, ?, ?)";
        try (PreparedStatement insertVertexStatement = connection.prepareStatement(sql)) {
            for (Vertex v: vertices) {
                insertVertexStatement.setInt(1, graphID);
                insertVertexStatement.setInt(2, v.getIndex());
                insertVertexStatement.setString(3, v.getData());
                insertVertexStatement.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Vertices haven't been saved");
            throw new RuntimeException(e);
        }
        log.info("Vertices have been saved");
    }

    private void saveEdges(int graphID, List<Edge> edges) {
        String tableName = "edges";
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
            log.error("Edges haven't been saved");
            throw new RuntimeException(e);
        }
        log.info("Edges have been saved");
    }

    public GraphResearchInfo getResearchInfo(int graphID) {
        String tableName = "graph_research_info";
        String sql = "SELECT * FROM " + tableName + " WHERE graph_id = " + graphID;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            GraphResearchInfo researchInfo = ParsingUtil.resultSetToGraphResearchInfo(rs);

            log.info("Graph research info have been received");
            return researchInfo;
        } catch (SQLException e) {
            log.error("Graph research info haven't been received");
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("Json parse error");
            throw new RuntimeException(e);
        }
    }

    private GraphMetadata getGraphMetadata(int graphID) {
        String tableName = "graph_metadata";
        String sql = "SELECT is_directed, is_weighted, has_self_loops, has_multiple_edges FROM " + tableName + " WHERE graph_id = " + graphID;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            log.info("Graph metadata have been received");
            return new GraphMetadata(rs.getBoolean("is_directed"), rs.getBoolean("is_weighted"), rs.getBoolean("has_self_loops"), rs.getBoolean("has_multiple_edges"));
        } catch (SQLException e) {
            log.error("Graph metadata haven't been received");
            throw new RuntimeException(e);
        }
    }

    public GraphModel getGraph(int graphID) {
        List<Vertex> vertices = getVertices(graphID);
        List<Edge> edges = getEdges(graphID, vertices);
        return new GraphModel(vertices, edges, getGraphMetadata(graphID));
    }

    private List<Vertex> getVertices(int graphID) {
        List<Vertex> vertices = new ArrayList<>();
        String tableName = "vertices";
        String sql = "SELECT index, data FROM " + tableName  + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                vertices.add(new Vertex(rs.getInt("index"), rs.getString("data")));
            }
        } catch(SQLException e) {
            log.error("Vertices haven't been received");
            throw new RuntimeException(e);
        }
        log.info("Vertices have been received");
        return vertices;
    }

    private List<Edge> getEdges(int graphID, List<Vertex> vertices) {
        TreeMap<Integer, Vertex> verticesMap = new TreeMap<>();
        for (Vertex v: vertices) {
            verticesMap.put(v.getIndex(), v);
        }

        ArrayList<Edge> edges = new ArrayList<>();
        String tableName = "edges";
        String sql = "SELECT source, target, weight, data FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Vertex s = verticesMap.get(rs.getInt("source"));
                Vertex t = verticesMap.get(rs.getInt("target"));
                edges.add(new Edge(s, t, rs.getInt("weight"), rs.getString("data")));
            }
        } catch (SQLException e) {
            log.error("Edges haven't been received");
            throw new RuntimeException(e);
        }
        log.info("Edges have been received");
        return edges;
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
            log.error("All user graphs haven't been deleted", e);
            throw new RuntimeException(e);
        }

        String sql2 = "DELETE FROM " + tableName + " WHERE user_id = " + userID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql2)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("All user graphs haven't been deleted", e);
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
        log.info("All user graphs have been deleted");
    }

    public void deleteGraph(int graphID) {
        String tableName = "graph_metadata";
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Graph haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Graph have been deleted");
        deleteVertices(graphID);
        deleteEdges(graphID);
    }

    private void deleteVertices(int graphID) {
        String tableName = "vertices";
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Vertices haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Vertices have been deleted");
    }

    private void deleteEdges(int graphID) {
        String tableName = "edges";
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Edges haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Edges have been deleted");
    }

    private void initVerticesTable() {
        String tableName = "vertices";

        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, graph_id INT, index INT, data TEXT)";

        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql);
            preparedStatement1.execute();

        } catch (SQLException e) {
            log.error("init error");
            throw new RuntimeException(e);
        }
    }

    private void initEdgesTable() {
        String tableName = "edges";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, graph_id INT, source INT, target INT, weight DOUBLE PRECISION, data TEXT)";
        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(sql);
            preparedStatement1.execute();
        } catch (SQLException e) {
            log.error("init error");
            throw new RuntimeException(e);
        }
    }

    private void initMetadataTable() {
        String tableName = "graph_metadata";
        String sql = "CREATE TABLE " + tableName + "(graph_id SERIAL PRIMARY KEY, user_id INT, is_directed BOOLEAN, is_weighted BOOLEAN, has_self_loops BOOLEAN, has_multiple_edges BOOLEAN)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Metadata table haven't been created");
            throw new RuntimeException(e);
        }
        log.info("Metadata table have been created");
    }

    private void initGraphResearchInfoTable() {
        String tableName = "graph_research_info";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, " +
                "graph_id INT, user_id INT, connectivity BOOLEAN, bridges TEXT, " +
                "articulation_points TEXT, connected_components TEXT)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("GraphResearchInfo table haven't been created");
            throw new RuntimeException(e);
        }
        log.info("GraphResearchInfo table have been created");
    }
    public void initDB() {
        initEdgesTable();
        initVerticesTable();
        initMetadataTable();
        initGraphResearchInfoTable();
    }
}
