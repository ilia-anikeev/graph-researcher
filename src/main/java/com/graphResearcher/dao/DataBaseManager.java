package com.graphResearcher.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import com.graphResearcher.model.Vertex;

public class DataBaseManager {

    private String NAME;
    private String PASSWORD;

    private final String URL = "jdbc:postgresql://localhost:5432/graphResearcherDB";

    public DataBaseManager() {
        try (Scanner scan = new Scanner(new File("src/main/resources/application.properties"))) {
            NAME = scan.nextLine();
            PASSWORD = scan.nextLine();
        } catch(FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public ArrayList<Vertex> getVertices(String graphName) {
        ArrayList<Vertex> vertices = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(URL, NAME, PASSWORD)) {
            String query = """
                    SELECT number FROM test_graph_vertices
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                vertices.add(new Vertex(rs.getInt(1)));
            }
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        return vertices;
    }

}
