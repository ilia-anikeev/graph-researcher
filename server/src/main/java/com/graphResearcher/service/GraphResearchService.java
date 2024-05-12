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

import static com.graphResearcher.util.ParsingUtil.graphToGraphModel;


@Service
public class GraphResearchService {
    public GraphResearchInfo research(GraphModel graphModel) {
        Graph<Vertex, WeightedEdge> graph = ParsingUtil.graphModelToGraph(graphModel);
        GraphResearchInfo info = new GraphResearchInfo();

        connectivityResearch(info, graph, graphModel);

        if (graph.getType().isUndirected()) {
            planarityResearch(info, graph, graphModel);
            chordalityResearch(info, graph, graphModel);
        } else {
            // flowResearch
        }

        //bipartitionResearch

        //cycleResearch

        //spanningResearch

        //VertexCover

        return info;
    }

    private void connectivityResearch(GraphResearchInfo info, Graph<Vertex, WeightedEdge> graph, GraphModel graphModel) {
        BiconnectivityInspector<Vertex, WeightedEdge> biconnectivityInspector = new BiconnectivityInspector<>(graph);
        ConnectivityInspector<Vertex, WeightedEdge> connectivityInspector = new ConnectivityInspector<>(graph);

        info.isConnected = biconnectivityInspector.isConnected();

        info.isBiconnected = biconnectivityInspector.isBiconnected();

        info.articulationPoints= new ArrayList<>(biconnectivityInspector.getCutpoints());

        info.bridges = biconnectivityInspector.getBridges().stream()
                .map(WeightedEdge::toEdge)
                .collect(Collectors.toList());

        info.connectedComponents = connectivityInspector.connectedSets().stream().map(v -> (List<Vertex>)new ArrayList<>(v)).toList();

        info.blocks = biconnectivityInspector.getBlocks().stream()
                .map(block -> (List<Vertex>)new ArrayList<>(block.vertexSet())).toList();

    }

    private void planarityResearch(GraphResearchInfo info, Graph<Vertex, WeightedEdge> graph, GraphModel graphModel) {
        BoyerMyrvoldPlanarityInspector<Vertex, WeightedEdge> planarityInspector = new BoyerMyrvoldPlanarityInspector<>(graph);
        info.isPlanar = planarityInspector.isPlanar();

        if (info.isPlanar) {
            Map<Vertex, List<Edge>> embedding = new HashMap<>();
            for (Vertex v: graph.vertexSet()) {
                embedding.put(v, planarityInspector.getEmbedding().getEdgesAround(v).stream().map(WeightedEdge::toEdge).toList());
            }
            info.embedding = embedding;
        } else {
            info.kuratowskiSubgraph = ParsingUtil.graphToGraphModel(planarityInspector.getKuratowskiSubdivision(), graphModel.getMetadata());
        }
    }

    private void chordalityResearch(GraphResearchInfo info, Graph<Vertex, WeightedEdge> graph, GraphModel graphModel) {
        ChordalityInspector<Vertex, WeightedEdge> chordalityInspector = new ChordalityInspector<>(graph);
        info.isChordal = chordalityInspector.isChordal();
        if (info.isChordal) {
            info.perfectEliminationOrder = chordalityInspector.getPerfectEliminationOrder();

            ChordalGraphColoring<Vertex, WeightedEdge> coloringResearcher = new ChordalGraphColoring<>(graph);
            VertexColoringAlgorithm.Coloring<Vertex> coloring = coloringResearcher.getColoring();
            info.chromaticNumber = coloring.getNumberColors();
            info.coloring = coloring.getColorClasses().stream().map(st -> (List<Vertex>)new ArrayList<>(st)).toList();


            ChordalGraphMaxCliqueFinder<Vertex, WeightedEdge> maxCliqueFinder = new ChordalGraphMaxCliqueFinder<>(graph);
            info.maxClique = ParsingUtil.listVertexToSubgraph(maxCliqueFinder.getClique().stream().toList(), graphModel);
            ChordalGraphIndependentSetFinder<Vertex, WeightedEdge> independentSetFinder = new ChordalGraphIndependentSetFinder<>(graph);
            info.independentSet = independentSetFinder.getIndependentSet().stream().toList();

            ChordalGraphMinimalVertexSeparatorFinder<Vertex, WeightedEdge> minimalVertexSeparatorFinder = new ChordalGraphMinimalVertexSeparatorFinder<>(graph);

            info.minimalVertexSeparator = minimalVertexSeparatorFinder.getMinimalSeparators().stream().map(Set::stream).map(Stream::toList).toList();
        }
    }
}
