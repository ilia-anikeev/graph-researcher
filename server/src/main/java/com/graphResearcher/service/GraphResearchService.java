package com.graphResearcher.service;

import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.GraphResearchInfo;
import com.graphResearcher.model.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

import static com.graphResearcher.service.SaveGraphService.buildGraph;
    

@Service
public class GraphResearchService {


    public GraphResearchInfo softResearch (GraphModel graphModel){

        GraphResearchInfo result= new GraphResearchInfo();

        Graph<Vertex, DefaultWeightedEdge > graph = buildGraph(graphModel);
        BiconnectivityInspector biconnectivityInspector = new BiconnectivityInspector<>(graph);
        result.connectedComponents= new ConnectivityInspector<Vertex,DefaultWeightedEdge>(graph).connectedSets();
        result.bridges=biconnectivityInspector.getBridges();
        result.articulationPoints=biconnectivityInspector.getCutpoints();
        result.connectivity=biconnectivityInspector.isConnected();
        result.blocks=biconnectivityInspector.getBlocks();
        return result;
    }
}