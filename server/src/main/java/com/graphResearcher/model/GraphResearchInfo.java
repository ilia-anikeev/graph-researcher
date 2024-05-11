package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.util.ParsingUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GraphResearchInfo {
    public Boolean isConnected;
    public Boolean isBiconnected;
    public List<Vertex> articulationPoints;
    public List<Edge> bridges;
    public List<GraphModel> connectedComponents;
    public List<GraphModel> blocks;

    public Boolean isPlanar;
    public Map<Vertex, List<Edge>> embedding; //TODO
    public GraphModel kuratowskiSubgraph;

    public Boolean isChordal;
    public List<Vertex> perfectEliminationOrder;
    public Integer chromaticNumber;
    public Map<Vertex, Integer> coloring;
    public GraphModel maxClique;
    public List<Vertex> independentSet;
    public List<List<Vertex>> minimalVertexSeparator;


    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        json.put("isConnected", isConnected);
        json.put("isBiconnected", isBiconnected);
        json.set("articulationPoints", ParsingUtil.verticesListToJsonArray(articulationPoints));
        json.set("bridges", ParsingUtil.edgesListToJsonArray(bridges));
        json.set("connectedComponents", ParsingUtil.graphsListToJsonArray(connectedComponents));

        json.set("blocks", ParsingUtil.graphsListToJsonArray(blocks));
        if (isPlanar != null) json.put("isPlanar", isPlanar);
        if (isPlanar != null && !isPlanar) {
            json.set("kuratovskySubgraph", kuratowskiSubgraph.toJson());
        }

        if (isChordal != null) json.put("isChordal", isChordal);
        if (isChordal != null && isChordal) {
            json.set("perfectEliminationOrder", ParsingUtil.verticesListToJsonArray(perfectEliminationOrder));
            json.put("chromaticNumber", chromaticNumber);
            // json.set("coloring", /* */); //TODO
            json.set("maxClique", maxClique.toJson());
            json.set("independentSet", ParsingUtil.verticesListToJsonArray(independentSet));
//            json.set("minimal_vertex_separator", ParsingUtil.) //TODO
        }
        return json;
    }

}
