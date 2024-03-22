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

        String tableName = "user" + userID + "_graph_research_info";
        ParseResearchInfo result = new ParseResearchInfo(info, graphID);

        String sql1 = "DELETE FROM " + tableName + " WHERE graph_id = " + graphID;

        String sql2 = "INSERT INTO " + tableName + "(" + result.fieldsName + ") VALUES(" + result.fields + ")";

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
        String tableName = "user" + userID + "_graph_metadata";
        String sql = "INSERT INTO " + tableName + "(is_directed, is_weighted, has_self_loops, has_multiple_edges)" +
                "VALUES(" + graph.metadata.isDirected + ", " + graph.metadata.isWeighted + ", "
                + graph.metadata.hasSelfLoops + ", " + graph.metadata.hasMultipleEdges + ") " +
                "RETURNING graph_id";
        int graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            graphID = rs.getInt("graph_id");
            saveVertices(userID, graphID, graph.getVertices());
            saveEdges(userID, graphID, graph.getEdges());
        } catch (SQLException e) {
            log.error("Graph hasn't been saved");
            throw new RuntimeException(e);
        }
        log.info("Graph has been saved");
        return graphID;
    }

    private void saveVertices(int userID, int graphID, List<Vertex> vertices) {
        String tableName = "user" + userID + "_graph" + graphID + "_vertices";
        String createTableSql = "CREATE TABLE " + tableName + "(vertex_id SERIAL PRIMARY KEY, index INTEGER, data TEXT)";
        String insertVertexSql = "INSERT INTO " + tableName + "(index, data) VALUES(?, ?)";

        try (PreparedStatement createTableStatement = connection.prepareStatement(createTableSql);
             PreparedStatement insertVertexStatement = connection.prepareStatement(insertVertexSql)) {

            createTableStatement.execute();
            for (Vertex v: vertices) {
                insertVertexStatement.setInt(1, v.getIndex());
                insertVertexStatement.setString(2, v.getData());
                insertVertexStatement.executeUpdate();
            }

        } catch (SQLException e) {
            log.error("Edges haven't been saved");
            throw new RuntimeException(e);
        }
        log.info("Edges have been saved");
    }

    private void saveEdges(int userID, int graphID, List<Edge> edges) {
        String tableName = "user" + userID + "_graph" + graphID + "_edges";
        String createTableSql = "CREATE TABLE " + tableName + "(edge_id SERIAL PRIMARY KEY, source INTEGER, target INTEGER, weight DOUBLE PRECISION, data TEXT)";
        String insertEdgeSql = "INSERT INTO " + tableName + "(source, target, weight, data) VALUES(?, ?, ?, ?)";

        try (PreparedStatement createTableStatement = connection.prepareStatement(createTableSql);
             PreparedStatement insertEdgeStatement = connection.prepareStatement(insertEdgeSql)) {

            createTableStatement.execute();
            for (Edge e: edges) {
                insertEdgeStatement.setInt(1, e.getSource().getIndex());
                insertEdgeStatement.setInt(2, e.getTarget().getIndex());
                insertEdgeStatement.setDouble(3, e.getWeight());
                insertEdgeStatement.setString(4, e.getData());
                insertEdgeStatement.executeUpdate();
            }

        } catch (SQLException e) {
            log.error("Edges haven't been saved");
            throw new RuntimeException(e);
        }
        log.info("Edges have been saved");
    }

    public GraphResearchInfo getResearchInfo(int userID, int graphID) {
        String tableName = "user" + userID + "_graph_research_info";
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

    private GraphMetadata getGraphMetadata(int userID, int graphID) {
        String tableName = "user" + userID + "_graph_metadata";
        String sql = "SELECT is_directed, is_weighted, has_self_loops, has_multiple_edges FROM " + tableName + " WHERE graph_id=" + graphID;
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

    public GraphModel getGraph(int userID, int graphID) {
        List<Vertex> vertices = getVertices(userID, graphID);
        List<Edge> edges = getEdges(userID, graphID, vertices);
        return new GraphModel(vertices, edges, getGraphMetadata(userID, graphID));
    }

    private List<Vertex> getVertices(int userID, int graphID) {
        List<Vertex> vertices = new ArrayList<>();
        String tableName = "user" + userID + "_graph" + graphID + "_vertices";

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT index, data FROM " + tableName)) {
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

    private List<Edge> getEdges(int userID, int graphID, List<Vertex> vertices) {
        TreeMap<Integer, Vertex> verticesMap = new TreeMap<>();
        for (Vertex v: vertices) {
            verticesMap.put(v.getIndex(), v);
        }

        ArrayList<Edge> edges = new ArrayList<>();
        String tableName = "user" + userID + "_graph" + graphID + "_edges";
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT source, target, weight, data FROM " + tableName)) {
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
        createUserGraphMetadataTable(userID);
        createUserGraphResearchInfoTable(userID);
    }

    private void createUserGraphMetadataTable(int userID) {
        String tableName = "user" + userID + "_graph_metadata";
        String sql = "CREATE TABLE " + tableName + "(graph_id SERIAL PRIMARY KEY, is_directed BOOLEAN, is_weighted BOOLEAN, has_self_loops BOOLEAN, has_multiple_edges BOOLEAN)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Metadata table haven't been created");
            throw new RuntimeException(e);
        }
        log.info("Metadata table have been created");
    }

    private void createUserGraphResearchInfoTable(int userID) {
        String tableName = "user" + userID + "_graph_research_info";
        String sql = "CREATE TABLE " + tableName + "(id SERIAL PRIMARY KEY, " +
                "graph_id INTEGER, connectivity BOOLEAN, bridges TEXT, " +
                "articulation_points TEXT, connected_components TEXT)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Metadata table haven't been created");
            throw new RuntimeException(e);
        }
        log.info("Metadata table have been created");
    }

    public void deleteUser(int userID) {
        deleteUserGraphMetadataTable(userID);
        deleteUserGraphResearchInfoTable(userID);
    }

    private void deleteUserGraphResearchInfoTable(int userID) {
        String tableName = "user" + userID + "_graph_research_info";
        String sql = "DROP TABLE " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Research info table haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Research info table have been deleted");
    }

    private void deleteUserGraphMetadataTable(int userID) {
        deleteAllUserGraphs(userID);
        String tableName = "user" + userID + "_graph_metadata";
        String sql = "DROP TABLE " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Metadata table haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Metadata table have been deleted");
    }

    private void deleteAllUserGraphs(int userID) {
        String tableName = "user" + userID + "_graph_metadata";
        String sql = "SELECT graph_id FROM " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int graphID = rs.getInt("graph_id");
                deleteGraph(userID, graphID);
            }
        } catch (SQLException e) {
            log.error("Research info table haven't been deleted");
            throw new RuntimeException(e);
        }
    }

    private void deleteVertices(int userID, int graphID) {
        String tableName = "user" + userID + "_graph" + graphID + "_vertices";
        String sql = "DROP TABLE " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Vertices haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Vertices have been deleted");
    }

    private void deleteEdges(int userID, int graphID) {
        String tableName = "user" + userID + "_graph" + graphID + "_edges";
        String sql = "DROP TABLE " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Edges haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Edges have been deleted");
    }

    public void deleteGraph(int userID, int graphID) {
        String tableName = "user" + userID + "_graph_metadata";
        String sql = "DELETE FROM " + tableName + " WHERE graph_id=" + graphID;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Graph haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Graph have been deleted");
        deleteVertices(userID, graphID);
        deleteEdges(userID, graphID);
    }
}
