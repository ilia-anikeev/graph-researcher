package com.graphResearcher.service;

import com.graphResearcher.GraphRequest;
import com.graphResearcher.VertexRequest;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.*;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.stereotype.Service;



@Service
public class SaveGraphService {

    public Graph<VertexRequest, DefaultEdge> buildGraph(GraphRequest graph) {
        Graph<VertexRequest, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        for (int i = 0; i < graph.getVertexes().size(); i++){
            g.addVertex(graph.getVertexes().get(i));
        }
        for (int i = 0; i < graph.getEdges().size(); i++){
            g.addEdge(graph.getEdges().get(i).get(0), graph.getEdges().get(i).get(1));
        }
        return g;
    }
}
