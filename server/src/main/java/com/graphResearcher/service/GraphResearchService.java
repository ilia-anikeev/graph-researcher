package com.graphResearcher.service;

import com.graphResearcher.model.*;
import com.graphResearcher.util.ParsingUtil;
import org.jgrapht.Graph;
import org.jgrapht.alg.clique.ChordalGraphMaxCliqueFinder;
import org.jgrapht.alg.color.ChordalGraphColoring;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.cycle.ChordalGraphMinimalVertexSeparatorFinder;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.alg.independentset.ChordalGraphIndependentSetFinder;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class GraphResearchService {
    public GraphResearchInfo research(GraphModel graphModel) {
        Graph<Vertex, WeightedEdge> graph = ParsingUtil.graphModelToGraph(graphModel);
        GraphResearchInfo info = new GraphResearchInfo();

        connectivityResearch(info, graph);
        planarityResearch(info, graph);
        chordalityResearch(info, graph);
        return info;
    }

    private void connectivityResearch(GraphResearchInfo info, Graph<Vertex, WeightedEdge> graph) {
        BiconnectivityInspector<Vertex, WeightedEdge> biconnectivityInspector = new BiconnectivityInspector<>(graph);
        ConnectivityInspector<Vertex, WeightedEdge> connectivityInspector = new ConnectivityInspector<>(graph);

        info.isConnected = biconnectivityInspector.isConnected();

        info.isBiconnected = biconnectivityInspector.isBiconnected();

        info.bridges = biconnectivityInspector.getBridges().stream()
                .map(WeightedEdge::toEdge)
                .collect(Collectors.toList());
        info.articulationPoints= new ArrayList<>(biconnectivityInspector.getCutpoints());

        info.connectedComponents= connectivityInspector.connectedSets().stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());

        info.blocks = biconnectivityInspector.getBlocks().stream().toList();
    }

    private void planarityResearch(GraphResearchInfo info, Graph<Vertex, WeightedEdge> graph ) {
        BoyerMyrvoldPlanarityInspector<Vertex, WeightedEdge> planarityInspector = new BoyerMyrvoldPlanarityInspector<>(graph);
        info.isPlanar = planarityInspector.isPlanar();

        if (info.isPlanar) {
            Map<Vertex, List<Edge>> embedding = new HashMap<>();
            for (Vertex v: graph.vertexSet()) {
                embedding.put(v, planarityInspector.getEmbedding().getEdgesAround(v).stream().map(WeightedEdge::toEdge).toList());
            }
            info.embedding = embedding;
        } else {
            info.kuratovskySubgraph = planarityInspector.getKuratowskiSubdivision();
        }
    }

    private void chordalityResearch(GraphResearchInfo info, Graph<Vertex, WeightedEdge> graph) {
        ChordalityInspector<Vertex, WeightedEdge> chordalityInspector = new ChordalityInspector<>(graph);
        info.isChordal = chordalityInspector.isChordal();
        if (info.isChordal) {
            info.perfectEliminationOrder = chordalityInspector.getPerfectEliminationOrder();

            ChordalGraphColoring<Vertex, WeightedEdge> coloringResearcher = new ChordalGraphColoring<>(graph);
            VertexColoringAlgorithm.Coloring<Vertex> coloring = coloringResearcher.getColoring();
            info.chromaticNumber = coloring.getNumberColors();
            info.coloring = coloring.getColors();

            ChordalGraphMaxCliqueFinder<Vertex, WeightedEdge> maxCliqueFinder = new ChordalGraphMaxCliqueFinder<>(graph);
            info.maxClique = maxCliqueFinder.getClique().stream().toList();

            ChordalGraphIndependentSetFinder<Vertex, WeightedEdge> independentSetFinder = new ChordalGraphIndependentSetFinder<>(graph);
            info.independentSet = independentSetFinder.getIndependentSet().stream().toList();

            ChordalGraphMinimalVertexSeparatorFinder<Vertex, WeightedEdge> minimalVertexSeparatorFinder = new ChordalGraphMinimalVertexSeparatorFinder<>(graph);

            info.minimalVertexSeparator = minimalVertexSeparatorFinder.getMinimalSeparators().stream().map(Set::stream).map(Stream::toList).toList();
        }
    }
}
