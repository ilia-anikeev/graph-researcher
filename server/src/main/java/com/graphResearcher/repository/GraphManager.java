package com.graphResearcher.repository;

import com.graphResearcher.model.Edge;
import com.graphResearcher.model.GraphMetadata;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.Vertex;
import com.graphResearcher.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GraphManager {
    private static final Logger log = LoggerFactory.getLogger(InfoManager.class);
    private final DataSource dataSource;

    public GraphManager() {
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

    public GraphModel getGraph(int graphID) {
        try (Connection conn = dataSource.getConnection()) {
            return new GraphModel(getVertices(graphID, "vertices", conn), getEdges(graphID, "edges", conn), getGraphMetadata(graphID, conn));
        } catch (SQLException e) {
            log.error("ID {}: graph haven't been received", graphID);
            throw new RuntimeException(e);
        }
    }

    public void deleteGraph(int graphID) {
        String sql = "DELETE FROM graph_metadata WHERE graph_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();

            deleteVertices(graphID, "vertices", conn);
            deleteEdges(graphID, "edges", conn);
            log.info("ID {}: graph have been deleted", graphID);
        } catch (SQLException e) {
            log.error("ID {}: graph haven't been deleted", graphID, e);
            throw new RuntimeException(e);
        }
    }

    void saveVertices(int graphID, List<Vertex> vertices, String tableName, Connection conn) {
        String sql = "INSERT INTO " + tableName + "(graph_id, index, data) VALUES(?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (Vertex v : vertices) {
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

    void saveEdges(int graphID, List<Edge> edges, String tableName, Connection conn) {
        String sql = "INSERT INTO " + tableName + "(graph_id, source, target, weight, data) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (Edge e : edges) {
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

    private GraphMetadata getGraphMetadata(int graphID, Connection conn) {
        String sql = "SELECT graph_id, graph_name, is_directed, is_weighted, has_self_loops, has_multiple_edges FROM graph_metadata WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
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

    GraphModel getGraph(int graphID, Connection conn) {
        return new GraphModel(getVertices(graphID, "vertices", conn), getEdges(graphID, "edges", conn), getGraphMetadata(graphID, conn));
    }

    List<Vertex> getVertices(int graphID, String tableName, Connection conn) {
        String sql = "SELECT index, data FROM " + tableName + " WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            ResultSet rs = preparedStatement.executeQuery();
            List<Vertex> vertices = new ArrayList<>();
            while (rs.next()) {
                vertices.add(new Vertex(rs.getInt("index"), rs.getString("data")));
            }
            return vertices;
        } catch (SQLException e) {
            log.error("ID {}: vertices haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    List<Edge> getEdges(int graphID, String tableName, Connection conn) {
        List<Vertex> vertices = getVertices(graphID, "vertices", conn);
        Map<Integer, Vertex> verticesMap = new HashMap<>();
        for (Vertex v : vertices) {
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

    void deleteVertices(int graphID, String tableName, Connection conn) {
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: vertices from {} haven't been deleted", graphID, tableName, e);
            throw new RuntimeException(e);
        }
    }

    void deleteEdges(int graphID, String tableName, Connection conn) {
        String sql = "DELETE FROM " + tableName + " WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: edges from {} haven't been deleted", graphID, tableName, e);
            throw new RuntimeException(e);
        }
    }

    void deletePerfectEliminationOrder(int graphID, Connection conn) {
        deleteVertices(graphID, "perfect_elimination_order", conn);
    }

    void deleteKuratowskiSubgraph(int graphID, Connection conn) {
        String sql = "DELETE FROM kuratowski_subgraph WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: subgraph hasn't been deleted", graphID, e);
            throw new RuntimeException(e);
        }
    }
    void deleteEmbedding(int graphID, Connection conn) {
        String sql = "DELETE FROM embedding WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: embedding hasn't been deleted", graphID, e);
            throw new RuntimeException(e);
        }
    }

    List<List<Vertex>> getComponents(int graphID, String tableName, Connection conn) {
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
        } catch (SQLException e) {
            log.error("ID {}: components haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    List<Vertex> getPerfectEliminationOrder(int graphID, Connection conn) {
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
        } catch (SQLException e) {
            log.error("ID {}: vertices haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    Map<Vertex, List<Edge>> getEmbedding(int graphID, Connection conn) {
        Map<Vertex, List<Edge>> embedding = new HashMap<>();
        List<Vertex> vertices = getVertices(graphID, "vertices", conn);
        Map<Integer, Vertex> verticesMap = new HashMap<>();
        for (Vertex v : vertices) {
            verticesMap.put(v.getIndex(), v);
        }

        String sql = "SELECT index, sequence_number, source, target, weight, data FROM embedding WHERE graph_id = ? AND index = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (Vertex v: vertices) {
                preparedStatement.setInt(1, graphID);
                preparedStatement.setInt(2, v.getIndex());
                ResultSet rs = preparedStatement.executeQuery();
                Map<Integer, Edge> sequence_edges = new HashMap<>();
                while (rs.next()) {
                    Vertex source = verticesMap.get(rs.getInt("source"));
                    Vertex target = verticesMap.get(rs.getInt("target"));
                    Edge e = new Edge(source, target, rs.getDouble("weight"), rs.getString("data"));
                    sequence_edges.put(rs.getInt("sequence_number"), e);
                }
                List<Edge> listEdges = new ArrayList<>();
                for (int i = 0; i < sequence_edges.size(); ++i) {
                    listEdges.add(null);
                }
                for (Map.Entry<Integer, Edge> entry: sequence_edges.entrySet()) {
                    listEdges.set(entry.getKey(), entry.getValue());
                }
                embedding.put(v, listEdges);
            }
            return embedding;
        } catch (SQLException e) {
            log.error("ID {}: embedding haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    GraphModel getKuratowskiSubgraph(int graphID, Connection conn) {
        String sql = "SELECT subgraph_id FROM kuratowski_subgraph WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            ResultSet rs = preparedStatement.executeQuery();
            return getGraph(rs.getInt("subgraph_id"));
        } catch (SQLException e) {
            log.error("ID {}: subgraph haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    void saveComponents(int graphID, List<List<Vertex>> components, String tableName, Connection conn) {
        String sql = "INSERT INTO " + tableName + "(graph_id, component_number, index, data) VALUES(?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (int i = 0; i < components.size(); ++i) {
                for (Vertex v : components.get(i)) {
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

    void saveEmbedding(int graphID, Map<Vertex, List<Edge>> embedding, Connection conn) {
        String sql = "INSERT INTO embedding(graph_id, index, sequence_number, source, target, weight, data) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            for (var entry: embedding.entrySet()) {
                for (int i = 0; i < entry.getValue().size(); ++i) {
                    Edge e = entry.getValue().get(i);
                    preparedStatement.setInt(1, graphID);
                    preparedStatement.setInt(2, entry.getKey().getIndex());
                    preparedStatement.setInt(3, i);
                    preparedStatement.setInt(4, e.source.getIndex());
                    preparedStatement.setInt(5, e.target.getIndex());
                    preparedStatement.setDouble(6, e.weight);
                    preparedStatement.setString(7, e.data);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            log.error("ID {}: embedding haven't been saved", graphID, e);
            throw new RuntimeException(e);
        }
    }


    void saveKuratowskiSubgraph(int userID, int graphID, GraphModel graphModel, Connection conn) {
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

    void savePerfectEliminationOrder(int graphID, List<Vertex> perfectEliminationOrder, Connection conn) {
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
}
