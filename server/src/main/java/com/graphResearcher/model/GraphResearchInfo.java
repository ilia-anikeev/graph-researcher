package com.graphResearcher.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.util.Converter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class GraphResearchInfo {
    public ConnectivityInfo connectivityInfo = new ConnectivityInfo();

    public PlanarityInfo planarityInfo = new PlanarityInfo();

    public Boolean isChordal;
    public List<Vertex> perfectEliminationOrder;
    public Integer chromaticNumber;
    public List<List<Vertex>> coloring;
    public List<Vertex> maxClique;
    public List<Vertex> independentSet;
    public List<List<Vertex>> minimalVertexSeparator;

    public Boolean isBipartite; //TODO
    public List<List<Vertex>> partitions; //TODO
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
        if (planarityInfo.isPlanar != null) json.put("isPlanar", planarityInfo.isPlanar);
        if (planarityInfo.isPlanar != null && !planarityInfo.isPlanar) {
            json.set("kuratovskySubgraph", planarityInfo.kuratowskiSubgraph.toJson());
        }

        if (isChordal != null) json.put("isChordal", isChordal);
        if (isChordal != null && isChordal) {
            json.set("perfectEliminationOrder", Converter.verticesListToJsonArray(perfectEliminationOrder));
            json.put("chromaticNumber", chromaticNumber);
            json.set("coloring", Converter.listListVerticesToJsonArray(coloring));
            json.set("maxClique", Converter.verticesListToJsonArray(maxClique));
            json.set("independentSet", Converter.verticesListToJsonArray(independentSet));
            json.set("minimal_vertex_separator", Converter.listListVerticesToJsonArray(minimalVertexSeparator));
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

        equals &= planarityInfo.isPlanar == other.planarityInfo.isPlanar;
        if (planarityInfo.isPlanar != null && !planarityInfo.isPlanar) {
            equals &= planarityInfo.kuratowskiSubgraph.equals(other.planarityInfo.kuratowskiSubgraph);
        }

        equals &= isChordal == other.isChordal;
        if (isChordal != null && isChordal) {
            equals &= perfectEliminationOrder.equals(other.perfectEliminationOrder);
            equals &= chromaticNumber.equals(other.chromaticNumber);
            equals &= coloring.equals(other.coloring);
            equals &= maxClique.equals(other.maxClique);
            equals &= independentSet.equals(other.independentSet);
            equals &= minimalVertexSeparator.equals(other.minimalVertexSeparator);
        }
        return equals;
    }
}
