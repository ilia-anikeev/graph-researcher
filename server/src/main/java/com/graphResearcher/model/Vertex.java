package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Vertex {

    @JsonProperty("index")
    private int index;

    @JsonProperty("data")
    private String data;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vertex vertexRequest)) {
            return false;
        }
        return Objects.equals(this.data,vertexRequest.getData());
    }

    public Vertex(JsonNode json) {
        index = json.get("index").asInt();
        data = json.get("data").asText();
    }

    public JsonNode toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();

        json.put("index", index);
        json.put("data", data);

        return json;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
