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
    public Boolean isConnected;
    public Boolean isBiconnected;
    public List<Vertex> articulationPoints;
    public List<Edge> bridges;
    public List<List<Vertex>> connectedComponents;
    public List<List<Vertex>> blocks;

    public Boolean isPlanar;
    public Map<Vertex, List<Edge>> embedding; //TODO
    public GraphModel kuratowskiSubgraph;

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
        json.put("isConnected", isConnected);
        json.put("isBiconnected", isBiconnected);
        json.set("articulationPoints", Converter.verticesListToJsonArray(articulationPoints));
        json.set("bridges", Converter.edgesListToJsonArray(bridges));
        json.set("connectedComponents", Converter.listListVerticesToJsonArray(connectedComponents));
        json.set("blocks", Converter.listListVerticesToJsonArray(blocks));
        if (isPlanar != null) json.put("isPlanar", isPlanar);
        if (isPlanar != null && !isPlanar) {
            json.set("kuratovskySubgraph", kuratowskiSubgraph.toJson());
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
        equals &= bridges.equals(other.bridges);
        equals &= isConnected.equals(other.isConnected);
        equals &= isBiconnected.equals(other.isBiconnected);
        equals &= bridges.equals(other.bridges);
        equals &= articulationPoints.equals(other.articulationPoints);
        equals &= connectedComponents.equals(other.connectedComponents);
        equals &= blocks.equals(other.blocks);

        equals &= isPlanar == other.isPlanar;
        if (isPlanar != null && !isPlanar) {
            equals &= kuratowskiSubgraph.equals(other.kuratowskiSubgraph);
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
