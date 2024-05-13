package com.graphResearcher.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.util.ParsingUtil;

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
        json.set("articulationPoints", ParsingUtil.verticesListToJsonArray(articulationPoints));
        json.set("bridges", ParsingUtil.edgesListToJsonArray(bridges));
        json.set("connectedComponents", ParsingUtil.listListVerticesToJsonArray(connectedComponents));
        json.set("blocks", ParsingUtil.listListVerticesToJsonArray(blocks));
        if (isPlanar != null) json.put("isPlanar", isPlanar);
        if (isPlanar != null && !isPlanar) {
            json.set("kuratovskySubgraph", kuratowskiSubgraph.toJson());
        }

        if (isChordal != null) json.put("isChordal", isChordal);
        if (isChordal != null && isChordal) {
            json.set("perfectEliminationOrder", ParsingUtil.verticesListToJsonArray(perfectEliminationOrder));
            json.put("chromaticNumber", chromaticNumber);
            json.set("coloring", ParsingUtil.listListVerticesToJsonArray(coloring));
            json.set("maxClique", ParsingUtil.verticesListToJsonArray(maxClique));
            json.set("independentSet", ParsingUtil.verticesListToJsonArray(independentSet));
            json.set("minimal_vertex_separator", ParsingUtil.listListVerticesToJsonArray(minimalVertexSeparator));
        }
        return json;
    }
}
