package com.graphResearcher.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphResearcher.model.*;


import java.sql.*;

import java.util.*;

import com.graphResearcher.model.graphInfo.*;
import com.graphResearcher.util.Converter;
import com.graphResearcher.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@Repository
public class InfoManager {
    private static final Logger log = LoggerFactory.getLogger(InfoManager.class);
    private final DataSource dataSource;
    private final GraphManager graphManager;

    public InfoManager(GraphManager graphManager) {
        this.graphManager = graphManager;
        String name = PropertiesUtil.get("db.username");
        String password = PropertiesUtil.get("db.password");
        String url = PropertiesUtil.get("db.url");
        dataSource = new DriverManagerDataSource(url, name, password);
    }

    private void saveComponents(int graphID, List<List<Vertex>> components, String tableName, Connection conn) {
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

    private void saveEmbedding(int graphID, Map<Vertex, List<Edge>> embedding, Connection conn) {
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


    private void saveKuratowskiSubgraph(int userID, int graphID, GraphModel graphModel, Connection conn) {
        int subgraph_id = graphManager.saveGraph(userID, graphModel);
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

    public void saveConnectivityInfo(int graphID, ConnectivityInfo connectivityInfo, Connection conn) {
        if (connectivityInfo == null) {
            return;
        }
        graphManager.saveVertices(graphID, connectivityInfo.articulationPoints, "articulation_points", conn);
        graphManager.saveEdges(graphID, connectivityInfo.bridges, "bridges", conn);
        saveComponents(graphID, connectivityInfo.connectedComponents, "connected_components", conn);
        saveComponents(graphID, connectivityInfo.blocks, "blocks", conn);
    }

    public void saveChordalityInfo(int graphID, ChordalityInfo chordalityInfo, Connection conn) {
        if (chordalityInfo.isChordal == null) {
            return;
        }
        if (chordalityInfo.isChordal) {
            savePerfectEliminationOrder(graphID, chordalityInfo.perfectEliminationOrder, conn);
            saveComponents(graphID, chordalityInfo.coloring, "coloring", conn);
            graphManager.saveVertices(graphID, chordalityInfo.maxClique, "max_clique", conn);
            graphManager.saveVertices(graphID, chordalityInfo.independentSet, "independent_set", conn);
            saveComponents(graphID, chordalityInfo.minimalVertexSeparator, "minimal_vertex_separator", conn);
        }
    }

    public void savePlanarityInfo(int userID, int graphID, PlanarityInfo planarityInfo, Connection conn) {
        if (planarityInfo.isPlanar == null) {
            return;
        }
        if (planarityInfo.isPlanar) {
            saveEmbedding(graphID, planarityInfo.embedding, conn);
        } else {
            saveKuratowskiSubgraph(userID, graphID, planarityInfo.kuratowskiSubgraph, conn);
        }
    }

    public void saveBipartitePartitioningInfo(int graphID, BipartitePartitioningInfo
            bipartitePartitioningInfo, Connection conn) {
        if (bipartitePartitioningInfo.isBipartite == null) {
            return;
        }
        if (bipartitePartitioningInfo.isBipartite) {
            saveComponents(graphID, bipartitePartitioningInfo.coloring, "coloring", conn);
            saveComponents(graphID, bipartitePartitioningInfo.partitions, "partitions", conn);
            graphManager.saveVertices(graphID, bipartitePartitioningInfo.independentSet, "independent_set", conn);
        }
    }


    public void saveResearchInfo(int userID, int graphID, GraphResearchInfo info) {
        try (Connection conn = dataSource.getConnection()) {
            saveConnectivityInfo(graphID, info.connectivityInfo, conn);
            savePlanarityInfo(userID, graphID, info.planarityInfo, conn);
            saveChordalityInfo(graphID, info.chordalityInfo, conn);
            saveBipartitePartitioningInfo(graphID, info.bipartitePartitioningInfo, conn);
            graphManager.saveEdges(graphID, info.minSpanningTree, "min_spanning_tree", conn);

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

    public ConnectivityInfo getConnectivityInfo(int graphID, ConnectivityInfo connectivityInfo, Connection conn) {
        connectivityInfo.articulationPoints = graphManager.getVertices(graphID, "articulation_points", conn);
        connectivityInfo.bridges = graphManager.getEdges(graphID, "bridges", conn);
        connectivityInfo.connectedComponents = getComponents(graphID, "connected_components", conn);
        connectivityInfo.blocks = getComponents(graphID, "blocks", conn);
        return connectivityInfo;
    }

    public PlanarityInfo getPlanarityInfo(int graphID, PlanarityInfo planarityInfo, Connection conn) {
        if (planarityInfo.isPlanar == null){
            return planarityInfo;
        }
        if (planarityInfo.isPlanar) {
            planarityInfo.embedding = getEmbedding(graphID, conn);
        } else {
            planarityInfo.kuratowskiSubgraph = getKuratowskiSubgraph(graphID, conn);
        }
        return planarityInfo;
    }

    public ChordalityInfo getChordalityInfo(int graphID, ChordalityInfo chordalityInfo, Connection conn) {
        if (chordalityInfo.isChordal != null && chordalityInfo.isChordal) {
            chordalityInfo.perfectEliminationOrder = getPerfectEliminationOrder(graphID, conn);
            chordalityInfo.coloring = getComponents(graphID, "coloring", conn);
            chordalityInfo.maxClique = graphManager.getVertices(graphID, "max_clique", conn);
            chordalityInfo.independentSet = graphManager.getVertices(graphID, "independent_set", conn);
            chordalityInfo.minimalVertexSeparator = getComponents(graphID, "minimal_vertex_separator", conn);
        }
        return chordalityInfo;
    }

    public BipartitePartitioningInfo getBipartitePartitioningInfo(int graphID, BipartitePartitioningInfo bipartitePartitioningInfo, ChordalityInfo chordalityInfo, Connection conn) {
        if (bipartitePartitioningInfo.isBipartite != null && bipartitePartitioningInfo.isBipartite) {
            bipartitePartitioningInfo.chromaticNumber = chordalityInfo.chromaticNumber;
            bipartitePartitioningInfo.partitions = getComponents(graphID, "partitions", conn);
            bipartitePartitioningInfo.independentSet = graphManager.getVertices(graphID, "independent_set", conn);
            bipartitePartitioningInfo.coloring = getComponents(graphID, "coloring", conn);
        }
        return bipartitePartitioningInfo;
    }

    public GraphResearchInfo getResearchInfo(int graphID) {
        String sql = "SELECT * FROM graph_research_info WHERE graph_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);

            ResultSet rs = preparedStatement.executeQuery();
            GraphResearchInfo researchInfo = Converter.resultSetToGraphResearchInfo(rs);

            researchInfo.connectivityInfo = getConnectivityInfo(graphID, researchInfo.connectivityInfo, conn);
            researchInfo.planarityInfo = getPlanarityInfo(graphID, researchInfo.planarityInfo, conn);
            researchInfo.chordalityInfo = getChordalityInfo(graphID, researchInfo.chordalityInfo, conn);
            researchInfo.bipartitePartitioningInfo = getBipartitePartitioningInfo(graphID, researchInfo.bipartitePartitioningInfo, researchInfo.chordalityInfo, conn);
            researchInfo.minSpanningTree = graphManager.getEdges(graphID, "min_spanning_tree", conn);
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
        } catch (SQLException e) {
            log.error("ID {}: components haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    private List<Vertex> getPerfectEliminationOrder(int graphID, Connection conn) {
        List<Vertex> vertices = new ArrayList<>();
        for (int i = 0; i < graphManager.getVertices(graphID, "vertices", conn).size(); ++i) {
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

    private Map<Vertex, List<Edge>> getEmbedding(int graphID, Connection conn) {
        Map<Vertex, List<Edge>> embedding = new HashMap<>();
        List<Vertex> vertices = graphManager.getVertices(graphID, "vertices", conn);
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

    private GraphModel getKuratowskiSubgraph(int graphID, Connection conn) {
        String sql = "SELECT subgraph_id FROM kuratowski_subgraph WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            ResultSet rs = preparedStatement.executeQuery();
            return graphManager.getGraph(rs.getInt("subgraph_id"));
        } catch (SQLException e) {
            log.error("ID {}: subgraph haven't been received", graphID, e);
            throw new RuntimeException(e);
        }
    }

    void deleteResearchInfo(int graphID) {
        String sql = "DELETE FROM graph_research_info WHERE graph_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();

            graphManager.deleteVertices(graphID, "articulation_points", conn);
            graphManager.deleteVertices(graphID, "blocks", conn);
            graphManager.deleteEdges(graphID, "bridges", conn);
            graphManager.deleteVertices(graphID, "coloring", conn);
            graphManager.deleteVertices(graphID, "connected_components", conn);
            deleteEmbedding(graphID, conn);
            graphManager.deleteVertices(graphID, "independent_set", conn);
            deleteKuratowskiSubgraph(graphID, conn);
            graphManager.deleteVertices(graphID, "max_clique", conn);
            graphManager.deleteEdges(graphID, "min_spanning_tree", conn);
            graphManager.deleteVertices(graphID, "minimal_vertex_separator", conn);
            graphManager.deleteVertices(graphID, "partitions", conn);
            deletePerfectEliminationOrder(graphID, conn);

            log.info("ID {}: graph info have been deleted", graphID);
        } catch (SQLException e) {
            log.error("ID {}: graph info haven't been deleted", graphID, e);
            throw new RuntimeException(e);
        }
    }
    private void deletePerfectEliminationOrder(int graphID, Connection conn) {
        graphManager.deleteVertices(graphID, "perfect_elimination_order", conn);
    }

    private void deleteKuratowskiSubgraph(int graphID, Connection conn) {
        String sql = "DELETE FROM kuratowski_subgraph WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)){
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: subgraph hasn't been deleted", graphID, e);
            throw new RuntimeException(e);
        }
    }
    private void deleteEmbedding(int graphID, Connection conn) {
        String sql = "DELETE FROM embedding WHERE graph_id = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, graphID);
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("ID {}: embedding hasn't been deleted", graphID, e);
            throw new RuntimeException(e);
        }
    }
}
