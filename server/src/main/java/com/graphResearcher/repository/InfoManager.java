package com.graphResearcher.repository;

import com.fasterxml.jackson.core.JsonProcessingException;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public void saveConnectivityInfo(int graphID, ConnectivityInfo connectivityInfo, Connection conn) {
        if (connectivityInfo == null) {
            return;
        }
        graphManager.saveVertices(graphID, connectivityInfo.articulationPoints, "articulation_points", conn);
        graphManager.saveEdges(graphID, connectivityInfo.bridges, "bridges", conn);
        graphManager.saveComponents(graphID, connectivityInfo.connectedComponents, "connected_components", conn);
        graphManager.saveComponents(graphID, connectivityInfo.blocks, "blocks", conn);
    }

    public void saveChordalityInfo(int graphID, ChordalityInfo chordalityInfo, Connection conn) {
        if (chordalityInfo.isChordal == null) {
            return;
        }
        if (chordalityInfo.isChordal) {
            graphManager.savePerfectEliminationOrder(graphID, chordalityInfo.perfectEliminationOrder, conn);
            graphManager.saveComponents(graphID, chordalityInfo.coloring, "coloring", conn);
            graphManager.saveVertices(graphID, chordalityInfo.maxClique, "max_clique", conn);
            graphManager.saveVertices(graphID, chordalityInfo.independentSet, "independent_set", conn);
            graphManager.saveComponents(graphID, chordalityInfo.minimalVertexSeparator, "minimal_vertex_separator", conn);
        }
    }

    public void savePlanarityInfo(int userID, int graphID, PlanarityInfo planarityInfo, Connection conn) {
        if (planarityInfo.isPlanar == null) {
            return;
        }
        if (planarityInfo.isPlanar) {
            graphManager.saveEmbedding(graphID, planarityInfo.embedding, conn);
        } else {
            graphManager.saveKuratowskiSubgraph(userID, graphID, planarityInfo.kuratowskiSubgraph, conn);
        }
    }

    public void saveBipartitePartitioningInfo(int graphID, BipartitePartitioningInfo
            bipartitePartitioningInfo, Connection conn) {
        if (bipartitePartitioningInfo.isBipartite == null) {
            return;
        }
        if (bipartitePartitioningInfo.isBipartite) {
            graphManager.saveComponents(graphID, bipartitePartitioningInfo.coloring, "coloring", conn);
            graphManager.saveComponents(graphID, bipartitePartitioningInfo.partitions, "partitions", conn);
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
        connectivityInfo.connectedComponents = graphManager.getComponents(graphID, "connected_components", conn);
        connectivityInfo.blocks = graphManager.getComponents(graphID, "blocks", conn);
        return connectivityInfo;
    }

    public PlanarityInfo getPlanarityInfo(int graphID, PlanarityInfo planarityInfo, Connection conn) {
        if (planarityInfo.isPlanar == null){
            return planarityInfo;
        }
        if (planarityInfo.isPlanar) {
            planarityInfo.embedding = graphManager.getEmbedding(graphID, conn);
        } else {
            planarityInfo.kuratowskiSubgraph = graphManager.getKuratowskiSubgraph(graphID, conn);
        }
        return planarityInfo;
    }

    public ChordalityInfo getChordalityInfo(int graphID, ChordalityInfo chordalityInfo, Connection conn) {
        if (chordalityInfo.isChordal != null && chordalityInfo.isChordal) {
            chordalityInfo.perfectEliminationOrder = graphManager.getPerfectEliminationOrder(graphID, conn);
            chordalityInfo.coloring = graphManager.getComponents(graphID, "coloring", conn);
            chordalityInfo.maxClique = graphManager.getVertices(graphID, "max_clique", conn);
            chordalityInfo.independentSet = graphManager.getVertices(graphID, "independent_set", conn);
            chordalityInfo.minimalVertexSeparator = graphManager.getComponents(graphID, "minimal_vertex_separator", conn);
        }
        return chordalityInfo;
    }

    public BipartitePartitioningInfo getBipartitePartitioningInfo(int graphID, BipartitePartitioningInfo bipartitePartitioningInfo, ChordalityInfo chordalityInfo, Connection conn) {
        if (bipartitePartitioningInfo.isBipartite != null && bipartitePartitioningInfo.isBipartite) {
            bipartitePartitioningInfo.chromaticNumber = chordalityInfo.chromaticNumber;
            bipartitePartitioningInfo.partitions = graphManager.getComponents(graphID, "partitions", conn);
            bipartitePartitioningInfo.independentSet = graphManager.getVertices(graphID, "independent_set", conn);
            bipartitePartitioningInfo.coloring = graphManager.getComponents(graphID, "coloring", conn);
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

    public void deleteResearchInfo(int graphID) {
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
            graphManager.deleteEmbedding(graphID, conn);
            graphManager.deleteVertices(graphID, "independent_set", conn);
            graphManager.deleteKuratowskiSubgraph(graphID, conn);
            graphManager.deleteVertices(graphID, "max_clique", conn);
            graphManager.deleteEdges(graphID, "min_spanning_tree", conn);
            graphManager.deleteVertices(graphID, "minimal_vertex_separator", conn);
            graphManager.deleteVertices(graphID, "partitions", conn);
            graphManager.deletePerfectEliminationOrder(graphID, conn);

            log.info("ID {}: graph info have been deleted", graphID);
        } catch (SQLException e) {
            log.error("ID {}: graph info haven't been deleted", graphID, e);
            throw new RuntimeException(e);
        }
    }
}
