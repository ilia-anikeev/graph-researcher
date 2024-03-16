package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Vertex {

    @JsonProperty("index")
    public int index;

    @JsonProperty("data")
    public String data = "";

    public Vertex() {
    }

    public Vertex(int index, String data) {
        this.index = index;
        this.data = data;
    }

    public int getIndex(){
        return this.index;
    }

    public String getData(){
        return this.data;
    }

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
