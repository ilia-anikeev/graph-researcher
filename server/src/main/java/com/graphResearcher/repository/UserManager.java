package com.graphResearcher.repository;

import com.graphResearcher.util.PropertiesUtil;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserManager {
    private static final Logger log = LoggerFactory.getLogger(InfoManager.class);
    private final GraphManager graphManager;
    private final InfoManager infoManager;
    private final DataSource dataSource;


    public UserManager(GraphManager graphManager, InfoManager infoManager) {
        this.graphManager = graphManager;
        this.infoManager = infoManager;
        String name = PropertiesUtil.get("db.username");
        String password = PropertiesUtil.get("db.password");
        String url = PropertiesUtil.get("db.url");
        dataSource = new DriverManagerDataSource(url, name, password);
    }

    void deleteAllUserGraphs(int userID, Connection conn) {
        String sql1 = "SELECT graph_id FROM graph_metadata WHERE user_id = ?";
        String sql2 = "DELETE FROM graph_metadata WHERE user_id = ?";
        String sql3 = "DELETE FROM graph_research_info WHERE user_id = ?";

        try (PreparedStatement preparedStatement1 = conn.prepareStatement(sql1);
             PreparedStatement preparedStatement2 = conn.prepareStatement(sql2);
             PreparedStatement preparedStatement3 = conn.prepareStatement(sql3)) {
            preparedStatement1.setInt(1, userID);
            ResultSet rs = preparedStatement1.executeQuery();
            while (rs.next()) {
                int graphID = rs.getInt("graph_id");
                graphManager.deleteGraph(graphID);
                infoManager.deleteResearchInfo(graphID);
            }
            preparedStatement2.setInt(1, userID);
            preparedStatement2.execute();

            preparedStatement3.setInt(1, userID);
            preparedStatement3.execute();
        } catch (SQLException e) {
            log.error("All user{} graphs haven't been deleted", userID, e);
            throw new RuntimeException(e);
        }

        log.info("All user{} graphs have been deleted", userID);
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
}
