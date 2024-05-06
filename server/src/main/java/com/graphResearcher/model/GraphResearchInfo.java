package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.util.ParsingUtil;

import java.util.List;
import java.util.Map;

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
    public Map<Vertex, Integer> coloring; //TODO
    public GraphModel maxClique; //TODO
    public List<Vertex> independentSet; //TODO
    public List<List<Vertex>> minimalVertexSeparator; //TODO


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
            json.set("kuratovskySubGraph", kuratowskiSubgraph.toJson());
        }

        if (isChordal != null) json.put("isChordal", isChordal);
        if (isChordal != null && isChordal) {
            json.set("perfectEliminationOrder", ParsingUtil.verticesListToJsonArray(perfectEliminationOrder));
            json.put("chromaticNumber", chromaticNumber);
        }
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
        if (isBiconnected != other.isBiconnected) {
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
            if (!connectedComponents.get(i).equals(other.connectedComponents.get(i))) {
                return false;
            }
        }
        if (blocks.size() != other.blocks.size()) {
            return false;
        }
        for (int i = 0; i < blocks.size(); ++i) {
            if (!blocks.get(i).equals(other.blocks.get(i))) {
                return false;
            }
        }
        if (isPlanar != other.isPlanar) {
            return false;
        }
        if (kuratowskiSubgraph != null && !kuratowskiSubgraph.equals(other.kuratowskiSubgraph)) {
            return false;
        }
        if (isChordal != other.isChordal) {
            return false;
        }
        if (perfectEliminationOrder != null && other.perfectEliminationOrder == null ||
            perfectEliminationOrder == null && other.perfectEliminationOrder != null) {
            return false;
        }
        if (perfectEliminationOrder != null) {
            if (perfectEliminationOrder.size() != other.perfectEliminationOrder.size()) {
                return false;
            }
            for (int i = 0; i < perfectEliminationOrder.size(); ++i) {
                if (!perfectEliminationOrder.get(i).equals(other.perfectEliminationOrder.get(i))) {
                    return false;
                }
            }
        }
        if (chromaticNumber != other.chromaticNumber) {
            return false;
        }
        return true;
    }
}
