package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.util.ParsingUtil;
import org.jgrapht.Graph;

import java.util.List;
import java.util.Map;

public class GraphResearchInfo {
    public Boolean isConnected;
    public Boolean isBiconnected;
    public List<Vertex> articulationPoints;
    public List<Edge> bridges;
    public List<List<Vertex>> connectedComponents;
    public List<Graph<Vertex, WeightedEdge>> blocks;

    public Boolean isPlanar;
    public Map<Vertex, List<Edge>> embedding;
    public Graph<Vertex, WeightedEdge> kuratovskySubgraph;

    public Boolean isChordal;
    public List<Vertex> perfectEliminationOrder;
    public Integer chromaticNumber;
    public Map<Vertex, Integer> coloring;
    public List<Vertex> maxClique;
    public List<Vertex> independentSet;
    public List<List<Vertex>> minimalVertexSeparator;


    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.put("isConnected", isConnected);
        json.put("isBiconnected", isBiconnected);
        json.set("articulationPoints", ParsingUtil.verticesListToJson(articulationPoints));
        json.set("bridges", ParsingUtil.edgesListToJson(bridges));
        json.set("connectedComponents", ParsingUtil.vertices2DListToJson(connectedComponents));
        json.put("isPlanar", isPlanar);
        return json;
    }
}