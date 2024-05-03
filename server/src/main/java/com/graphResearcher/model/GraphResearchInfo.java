package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.util.ParsingUtil;

import java.util.List;

public class GraphResearchInfo {
    public Boolean connectivity;
    public List<Edge> bridges;
    public List<Vertex> articulationPoints;
    public List<List<Vertex>> connectedComponents;

    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.put("connectivity", connectivity);
        json.set("bridges", ParsingUtil.edgesListToJson(bridges));
        json.set("articulationPoints", ParsingUtil.verticesListToJson(articulationPoints));
        json.set("connectedComponents", ParsingUtil.vertices2DListToJson(connectedComponents));

        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GraphResearchInfo other)) {
            return false;
        }
        if (connectivity != other.connectivity) {
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