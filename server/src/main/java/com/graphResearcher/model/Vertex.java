package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String data = "";

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

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
