package com.graphResearcher.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Vertex {

    private int index;

    private String data;

    public Vertex(JsonNode json) {
        index = json.get("index").asInt();
        data = json.get("data").asText();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vertex other)) {
            return false;
        }
        return index == other.index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.put("index", index);
        json.put("data", data);

        return json;
    }
}
