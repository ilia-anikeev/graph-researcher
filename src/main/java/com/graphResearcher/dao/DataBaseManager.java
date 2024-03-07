package com.graphResearcher.dao;

import java.sql.*;
import java.util.ArrayList;


import com.graphResearcher.model.Vertex;
import com.graphResearcher.model.Edge;
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

    public ArrayList<Vertex> getVertices(int userID, int graphID) {
        ArrayList<Vertex> vertices = new ArrayList<>();
        String tableName = "id" + userID + "_" + graphID + "_vertices";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT index FROM " + tableName);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                vertices.add(new Vertex(rs.getInt("index")));
            }
        } catch(SQLException e) {
            log.error("Vertices haven't been received");
            throw new RuntimeException(e);
        }
        log.info("Vertices have been received");
        return vertices;
    }

    public ArrayList<Edge> getEdges(int userID, int graphID) {
        ArrayList<Edge> edges = new ArrayList<>();
        String tableName = "id" + userID + "_" + graphID + "_edges";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT source, target FROM " + tableName);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                edges.add(new Edge(rs.getInt("source"), rs.getInt("target")));
            }
        } catch (SQLException e) {
            log.error("Edges haven't been received");
            throw new RuntimeException(e);
        }
        log.info("Edges have been received");
        return edges;
    }

    public void saveGraph(int userID, int graphID, ArrayList<Vertex> vertices, ArrayList<Edge> edges) {
        saveVertices(userID, graphID, vertices);
        saveEdges(userID, graphID, edges);
        log.info("Graph has been saved");
    }

    public void deleteGraph(int userID, int graphID) {
        deleteVertices(userID, graphID);
        deleteEdges(userID, graphID);
        log.info("Graph has been deleted");
    }

    private void saveVertices(int userID, int graphID, ArrayList<Vertex> vertices) {
        String tableName = "id" + userID + "_" + graphID + "_vertices";
        String sql = "CREATE TABLE " + tableName + "(vertex_id SERIAL PRIMARY KEY,  index INTEGER)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            for (Vertex v: vertices) {
                sql = "INSERT INTO " + tableName + "(index) VALUES(" + v.index + ")";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.execute();
            }

        } catch (SQLException e) {
            log.error("Vertices haven't been saved");
            throw new RuntimeException(e);
        }
        log.info("Vertices have been saved");
    }

    private void saveEdges(int userID, int graphID, ArrayList<Edge> edges) {
        String tableName = "id" + userID + "_" + graphID + "_edges";
        String sql = "CREATE TABLE " + tableName + "(edge_id SERIAL PRIMARY KEY, source INTEGER, target INTEGER)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            for (Edge e: edges) {
                sql = "INSERT INTO " + tableName + "(source, target) VALUES(" + e.source + ", " + e.target + ")";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.execute();
            }

        } catch (SQLException e) {
            log.error("Edges haven't been saved");
            throw new RuntimeException(e);
        }
        log.info("Edges have been saved");
    }

    private void deleteVertices(int userID, int graphID) {
        String tableName = "id" + userID + "_" + graphID + "_vertices";
        String sql = "DROP TABLE " + tableName;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

        } catch (SQLException e) {
            log.error("Vertices haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Vertices have been deleted");
    }

    private void deleteEdges(int userID, int graphID) {
        String tableName = "id" + userID + "_" + graphID + "_edges";
        String sql = "DROP TABLE " + tableName;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Edges haven't been deleted");
            throw new RuntimeException(e);
        }
        log.info("Edges have been deleted");
    }
}
