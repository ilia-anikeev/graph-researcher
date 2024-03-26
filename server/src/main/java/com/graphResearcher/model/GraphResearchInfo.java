package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.util.ParsingUtil;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
}