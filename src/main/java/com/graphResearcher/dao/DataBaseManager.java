package com.graphResearcher.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.TreeMap;


import com.graphResearcher.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    public void saveGraph(int userID, GraphModel graph) {
        String tableName = "user" + userID + "_graph_archive";
        String sql = "INSERT INTO " + tableName + "(is_directed, is_weighted, graph_type) " +
                "VALUES(" + graph.info.isDirected + "," + graph.info.isWeighted + ", '" + graph.info.type + "') RETURNING graph_id";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            int graphID = rs.getInt("graph_id");
            saveVertices(userID, graphID, graph.vertices);
            saveEdges(userID, graphID, graph.edges);
        } catch (SQLException e) {
            log.error("Graph hasn't been saved");
            throw new RuntimeException(e);
        }

        log.info("Graph has been saved");
    }

    private void saveVertices(int userID, int graphID, ArrayList<Vertex> vertices) {
        String tableName = "user" + userID + "_graph" + graphID + "_vertices";
        String createTableSql = "CREATE TABLE " + tableName + "(vertex_id SERIAL PRIMARY KEY, index INTEGER, data TEXT)";
        String insertVertexSql = "INSERT INTO " + tableName + "(index, data) VALUES(?, ?)";

        try (PreparedStatement createTableStatement = connection.prepareStatement(createTableSql);
             PreparedStatement insertVertexStatement = connection.prepareStatement(insertVertexSql)) {

            createTableStatement.execute();
            for (Vertex v: vertices) {
                insertVertexStatement.setInt(1, v.index);
                insertVertexStatement.setString(2, v.data);
                insertVertexStatement.executeUpdate();
            }

        } catch (SQLException e) {
            log.error("Edges haven't been saved");
            throw new RuntimeException(e);
        }
        log.info("Edges have been saved");
    }

    private void saveEdges(int userID, int graphID, ArrayList<Edge> edges) {
        String tableName = "user" + userID + "_graph" + graphID + "_edges";
        String createTableSql = "CREATE TABLE " + tableName + "(edge_id SERIAL PRIMARY KEY, source INTEGER, target INTEGER, data TEXT)";
        String insertEdgeSql = "INSERT INTO " + tableName + "(source, target, data) VALUES(?, ?, ?)";

        try (PreparedStatement createTableStatement = connection.prepareStatement(createTableSql);
             PreparedStatement insertEdgeStatement = connection.prepareStatement(insertEdgeSql)) {

            createTableStatement.execute();
            for (Edge e: edges) {
                insertEdgeStatement.setInt(1, e.source.index);
                insertEdgeStatement.setInt(2, e.target.index);
                insertEdgeStatement.setString(3, e.data);
                insertEdgeStatement.executeUpdate();
            }

        } catch (SQLException e) {
            log.error("Edges haven't been saved");
            throw new RuntimeException(e);
        }
        log.info("Edges have been saved");
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
        String tableName = "user" + userID + "_graph_archive";
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


    public void createUserGraphArchive(int userID) {
        String tableName = "user" + userID + "_graph_archive";
        String sql = "CREATE TABLE " + tableName + "(graph_id SERIAL PRIMARY KEY, is_directed BOOLEAN, is_weighted BOOLEAN, graph_type TEXT)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Archive haven't been created");
            throw new RuntimeException(e);
        }
        log.info("Archive have been created");
    }

    public void deleteUserGraphArchive(int userID) {
        String tableName = "user" + userID + "_graph_archive";
        String sql = "DROP TABLE " + tableName;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Archive haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Archive have been deleted");
    }

    public GraphModel getGraph(int userID, int graphID) {
        var vertices = getVertices(userID, graphID);
        return new GraphModel(vertices, getEdges(userID, graphID, vertices), getGraphInfo(userID, graphID));
    }

    private GraphInfo getGraphInfo(int userID, int graphID) {
        String tableName = "user" + userID + "_graph_archive";
        String sql = "SELECT is_directed, is_weighted, graph_type FROM " + tableName + " WHERE graph_id=" + graphID;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            log.info("GraphInfo have been received");
            return new GraphInfo(rs.getBoolean("is_directed"), rs.getBoolean("is_weighted"), GraphType.fromString(rs.getString("graph_type")));
        } catch (SQLException e) {
            log.error("GraphInfo haven't been received");
            throw new RuntimeException(e);
        }
    }

    private ArrayList<Vertex> getVertices(int userID, int graphID) {
        ArrayList<Vertex> vertices = new ArrayList<>();
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

    private ArrayList<Edge> getEdges(int userID, int graphID, ArrayList<Vertex> vertices) {
        TreeMap<Integer, Vertex> verticesMap = new TreeMap<>();
        for (Vertex v: vertices) {
            verticesMap.put(v.index, v);
        }

        ArrayList<Edge> edges = new ArrayList<>();
        String tableName = "user" + userID + "_graph" + graphID + "_edges";
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT source, target, data FROM " + tableName)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Vertex s = verticesMap.get(rs.getInt("source"));
                Vertex t = verticesMap.get(rs.getInt("target"));
                edges.add(new Edge(s, t, rs.getString("data")));
            }
        } catch (SQLException e) {
            log.error("Edges haven't been received");
            throw new RuntimeException(e);
        }
        log.info("Edges have been received");
        return edges;
    }
}
