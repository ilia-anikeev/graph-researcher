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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GraphResearchInfo other)) {
            return false;
        }
        if (isConnected != other.isConnected) {
            return false;
        }
        if (bridges.size() != other.bridges.size()) {
            return false;
        }
        for (int i = 0; i < bridges.size(); ++i) {
            if (!bridges.get(i).equals(other.bridges.get(i))) {
                return false;
            }
        }

        if (articulationPoints.size() != other.articulationPoints.size()) {
            return false;
        }
        for (int i = 0; i < articulationPoints.size(); ++i) {
            if (!articulationPoints.get(i).equals(other.articulationPoints.get(i))) {
                return false;
            }
        }
        if (connectedComponents.size() != other.connectedComponents.size()) {
            return false;
        }
        for (int i = 0; i < connectedComponents.size(); ++i) {
            if (connectedComponents.get(i).size() != other.connectedComponents.get(i).size()) {
                return false;
            }
            for (int j = 0; j < connectedComponents.get(i).size(); ++j) {
                if (!connectedComponents.get(i).get(j).equals(other.connectedComponents.get(i).get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}