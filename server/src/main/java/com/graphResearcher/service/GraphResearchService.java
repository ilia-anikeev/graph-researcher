package com.graphResearcher.service;

import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.GraphResearchInfo;
import com.graphResearcher.model.Vertex;
import com.graphResearcher.model.WeightedEdge;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.graphResearcher.service.SaveGraphService.buildGraph;


@Service
public class GraphResearchService {


    public GraphResearchInfo softResearch (GraphModel graphModel){

        GraphResearchInfo result = new GraphResearchInfo();

        Graph<Vertex, WeightedEdge> graph = buildGraph(graphModel);
        BiconnectivityInspector<Vertex, WeightedEdge> biconnectivityInspector = new BiconnectivityInspector<>(graph);

        result.connectedComponents= new ConnectivityInspector<>(graph).connectedSets().stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
        result.bridges = biconnectivityInspector.getBridges().stream()
                .map(WeightedEdge::toEdge)
                .collect(Collectors.toList());
        result.articulationPoints= new ArrayList<>(biconnectivityInspector.getCutpoints());
        result.connectivity=biconnectivityInspector.isConnected();

        return result;
    }
}