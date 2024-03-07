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
            log.error("The vertices haven't been received");
            throw new RuntimeException(e);
        }
        log.info("The vertices have been received");
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
            log.error("The edges haven't been received");
            throw new RuntimeException(e);
        }
        log.info("The edges have been received");
        return edges;
    }

//    public Graph<Vertex, Edge> getGraph(int userID, int graphID) {
        //TODO
//    }
}
