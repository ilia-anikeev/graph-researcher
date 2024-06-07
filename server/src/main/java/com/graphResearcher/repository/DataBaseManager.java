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
        String sql = "INSERT INTO graph_metadata(user_id, graph_name, is_directed, is_weighted, has_self_loops, has_multiple_edges) " +
                "VALUES(?, ?, ?, ?, ?, ?)" +
                "RETURNING graph_id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.setString(2, graph.getMetadata().graphName);
            preparedStatement.setBoolean(3, graph.getMetadata().isDirected);
            preparedStatement.setBoolean(4, graph.getMetadata().isWeighted);
            preparedStatement.setBoolean(5, graph.getMetadata().hasSelfLoops);
            preparedStatement.setBoolean(6, graph.getMetadata().hasMultipleEdges);

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
                preparedStatement.setInt(2, e.source.getIndex());
                preparedStatement.setInt(3, e.target.getIndex());
                preparedStatement.setDouble(4, e.weight);
                preparedStatement.setString(5, e.data);
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
            saveVertices(graphID, info.connectivityInfo.articulationPoints, "articulation_points", conn);
            saveEdges(graphID, info.connectivityInfo.bridges, "bridges",conn);
            saveComponents(graphID, info.connectivityInfo.connectedComponents, "connected_components", conn);
            saveComponents(graphID, info.connectivityInfo.blocks, "blocks", conn);
            if (info.planarityInfo.isPlanar != null && !info.planarityInfo.isPlanar) {
                saveKuratowskiSubgraph(userID, graphID, info.planarityInfo.kuratowskiSubgraph, conn);
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

            researchInfo.connectivityInfo.articulationPoints = getVertices(graphID, "articulation_points", conn);
            researchInfo.connectivityInfo.bridges = getEdges(graphID, "bridges", conn);
            researchInfo.connectivityInfo.connectedComponents = getComponents(graphID, "connected_components", conn);
            researchInfo.connectivityInfo.blocks = getComponents(graphID, "blocks", conn);
            if (researchInfo.planarityInfo.isPlanar != null && !researchInfo.planarityInfo.isPlanar) {
                researchInfo.planarityInfo.kuratowskiSubgraph = getKuratowskiSubgraph(graphID, conn);
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
        String sql = "SELECT graph_id, graph_name, is_directed, is_weighted, has_self_loops, has_multiple_edges FROM graph_metadata WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setInt(1, graphID);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return new GraphMetadata(rs.getInt("graph_id"), rs.getString("graph_name"),
                    rs.getBoolean("is_directed"), rs.getBoolean("is_weighted"),
                    rs.getBoolean("has_self_loops"), rs.getBoolean("has_multiple_edges"));
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

    public GraphModel getGraph(int graphID, Connection conn) {
        return new GraphModel(getVertices(graphID, "vertices", conn), getEdges(graphID, "edges", conn), getGraphMetadata(graphID, conn));
    }

    public List<GraphModel> getAllUserGraphs(int userID) {
        List<GraphModel> graphModelList = new ArrayList<>();
        String sql = "SELECT graph_id FROM graph_metadata WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setInt(1, userID);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    int graphID = rs.getInt("graph_id");
                    GraphModel g = getGraph(graphID, conn);
                    graphModelList.add(g);
                }
        } catch (SQLException e) {
            log.error("userID {}: all user graphs haven't been received", userID);
            throw new RuntimeException(e);
        }
        return graphModelList;
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
                Edge e = new Edge(s, t, rs.getInt("weight"), rs.getString("data"));
                edges.add(e);
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
}
