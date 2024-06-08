package com.graphResearcher.model.graphInfo;

import com.graphResearcher.model.Vertex;

import java.util.List;

public class BipartitePartitioningInfo {
    public Boolean isBipartite; //TODO
    public List<List<Vertex>> partitions; //TODO

    public Integer chromaticNumber;
    public List<List<Vertex>> coloring;

    public List<Vertex> independentSet;
}
