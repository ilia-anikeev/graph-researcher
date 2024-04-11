package com.graphResearcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Edge {

    @JsonProperty("source")
    private Vertex source;

    @JsonProperty("target")
    private Vertex target;

    @JsonProperty("weight")
    private double weight;

    @JsonProperty("data")
    private String data;
}
