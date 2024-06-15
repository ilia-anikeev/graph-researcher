package com.graphResearcher.model.graphInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphResearcher.model.Edge;
import com.graphResearcher.util.Converter;

import java.util.Map;

public class FlowResearchInfo {
    public double maxFlow;

    public Map<Edge, Double> flow;
    public FlowResearchInfo(JsonNode json) {
        maxFlow = json.get("maxFlow").asDouble();

//        json.get("flow"); //TODO
    }

    public FlowResearchInfo() {
    }
    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.put("maxFlow", maxFlow);
        json.set("flow", Converter.mapOfEdgesAndDoublesToJsonArray(flow));
        return json;
    }
}
