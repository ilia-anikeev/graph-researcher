package com.graphResearcher.model.graphInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.model.*;
import com.graphResearcher.util.Converter;

import java.util.List;

public class GraphResearchInfo {
    public ConnectivityInfo connectivityInfo = new ConnectivityInfo();

    public PlanarityInfo planarityInfo = new PlanarityInfo();

    public ChordalityInfo chordalityInfo = new ChordalityInfo();

    public BipartitePartitioningInfo bipartitePartitioningInfo = new BipartitePartitioningInfo();

    public List<Edge> minSpanningTree; //TODO


    public JsonNode toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        json.put("isConnected", connectivityInfo.isConnected);
        json.put("isBiconnected", connectivityInfo.isBiconnected);
        json.set("articulationPoints", Converter.verticesListToJsonArray(connectivityInfo.articulationPoints));
        json.set("bridges", Converter.edgesListToJsonArray(connectivityInfo.bridges));
        json.set("connectedComponents", Converter.listListVerticesToJsonArray(connectivityInfo.connectedComponents));
        json.set("blocks", Converter.listListVerticesToJsonArray(connectivityInfo.blocks));
        if (planarityInfo.isPlanar != null) {
            json.put("isPlanar", planarityInfo.isPlanar);
            if (planarityInfo.isPlanar) {
                json.set("embedding", Converter.mapOfVertexAndListEdgesToJsonArray(planarityInfo.embedding));
            } else {
                json.set("kuratovskySubgraph", planarityInfo.kuratowskiSubgraph.toJson());
            }
        }

        if (chordalityInfo.isChordal != null) {
            json.put("isChordal", chordalityInfo.isChordal);
            if (chordalityInfo.isChordal) {
                json.set("perfectEliminationOrder", Converter.verticesListToJsonArray(chordalityInfo.perfectEliminationOrder));
                json.put("chromaticNumber", chordalityInfo.chromaticNumber);
                json.set("coloring", Converter.listListVerticesToJsonArray(chordalityInfo.coloring));
                json.set("maxClique", Converter.verticesListToJsonArray(chordalityInfo.maxClique));
                json.set("independentSet", Converter.verticesListToJsonArray(chordalityInfo.independentSet));
                json.set("minimal_vertex_separator", Converter.listListVerticesToJsonArray(chordalityInfo.minimalVertexSeparator));
            }
        }
        if (bipartitePartitioningInfo.isBipartite != null) {
            json.put("isBipartite", bipartitePartitioningInfo.isBipartite);
            if (bipartitePartitioningInfo.isBipartite) {
                json.set("partitions", Converter.listListVerticesToJsonArray(bipartitePartitioningInfo.partitions));
                json.put("chromaticNumber", bipartitePartitioningInfo.chromaticNumber);
                json.set("coloring", Converter.listListVerticesToJsonArray(bipartitePartitioningInfo.coloring));
                json.set("independentSet", Converter.verticesListToJsonArray(bipartitePartitioningInfo.independentSet));
            }
        }
        return json;
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GraphResearchInfo other)) {
            return false;
        }
        boolean equals = true;
        equals &= connectivityInfo.equals(other.connectivityInfo);
        if (planarityInfo != null) {
            equals &= planarityInfo.equals(other.planarityInfo);
        }
        if (chordalityInfo != null) {
            equals &= chordalityInfo.equals(other.chordalityInfo);
        }
        equals &= bipartitePartitioningInfo.equals(other.bipartitePartitioningInfo);
        return equals;
    }
}
