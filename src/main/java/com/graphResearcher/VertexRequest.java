package com.graphResearcher;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

//import java.util.Objects;

public class VertexRequest {

    public VertexRequest() {
    }

    @JsonProperty("data")
    private String data;


    public String getData(){
        return this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VertexRequest vertexRequest)) {
            return false;
        }
        return Objects.equals(this.data,vertexRequest.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}