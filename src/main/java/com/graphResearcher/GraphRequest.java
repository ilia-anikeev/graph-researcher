package com.graphResearcher;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.ArrayList;
import java.util.List;

public class GraphRequest {

    @JsonProperty("vertexes")
    public List<VertexRequest> vertexes = new ArrayList<>();

    @JsonProperty("edges")
    public List<List<VertexRequest>> edges = new ArrayList<>();

    public GraphRequest() {
    }

    public List<VertexRequest> getVertexes(){
        return this.vertexes;
    }

    public List<List<VertexRequest>> getEdges(){
        return this.edges;
    }
}