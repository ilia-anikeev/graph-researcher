package com.graphResearcher.model.graphInfo;

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

    public List<Edge> minSpanningTree;


    public JsonNode toJson() {
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
        json.set("min_spanning_tree", Converter.edgesListToJsonArray(minSpanningTree));
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
        equals &= minSpanningTree.equals(other.minSpanningTree);
        return equals;
    }

    public GraphResearchInfo() {

    }

    public GraphResearchInfo(JsonNode json, GraphModel graphModel) {
        connectivityInfo.isConnected = json.get("isConnected").asBoolean();
        connectivityInfo.isBiconnected = json.get("isBiconnected").asBoolean();

        connectivityInfo.articulationPoints = Converter.jsonArrayToVerticesList(json.get("articulationPoints"));
        connectivityInfo.bridges = Converter.jsonArrayToEdgesList(json.get("bridges"));

        connectivityInfo.connectedComponents = Converter.jsonArrayToListListVertices(json.get("connectedComponents"));
        connectivityInfo.blocks = Converter.jsonArrayToListListVertices(json.get("blocks"));

        if (json.has("isPlanar")) {
            planarityInfo.isPlanar = json.get("isPlanar").asBoolean();
            if (planarityInfo.isPlanar) {
                planarityInfo.embedding = Converter.jsonArrayToMapOfVertexAndListEdges(json.get("embedding"), graphModel.getVertices());
            } else {
                planarityInfo.kuratowskiSubgraph = new GraphModel(json.get("kuratovskySubgraph"));
            }
        }

        if (json.has("isChordal")) {
            chordalityInfo.isChordal = json.get("isChordal").asBoolean();
            if (chordalityInfo.isChordal) {
                chordalityInfo.perfectEliminationOrder = Converter.jsonArrayToVerticesList(json.get("perfectEliminationOrder"));
                chordalityInfo.chromaticNumber = json.get("chromaticNumber").asInt();
                chordalityInfo.coloring = Converter.jsonArrayToListListVertices(json.get("coloring"));
                chordalityInfo.maxClique = Converter.jsonArrayToVerticesList(json.get("maxClique"));
                chordalityInfo.independentSet = Converter.jsonArrayToVerticesList(json.get("independentSet"));
                chordalityInfo.minimalVertexSeparator = Converter.jsonArrayToListListVertices(json.get("minimal_vertex_separator"));
            }
        }

        if (json.has("isBipartite")) {
            bipartitePartitioningInfo.isBipartite = json.get("isBipartite").asBoolean();
            if (bipartitePartitioningInfo.isBipartite) {
                bipartitePartitioningInfo.partitions = Converter.jsonArrayToListListVertices(json.get("partitions"));
                bipartitePartitioningInfo.chromaticNumber = json.get("chromaticNumber").asInt();
                bipartitePartitioningInfo.coloring = Converter.jsonArrayToListListVertices(json.get("coloring"));
                bipartitePartitioningInfo.independentSet = Converter.jsonArrayToVerticesList(json.get("independentSet"));
            }
        }
        minSpanningTree = Converter.jsonArrayToEdgesList(json.get("min_spanning_tree"));
    }
}
