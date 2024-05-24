package com.graphResearcher.service;

import com.graphResearcher.model.*;
import com.graphResearcher.util.Converter;
import org.jgrapht.Graph;
import org.jgrapht.alg.clique.ChordalGraphMaxCliqueFinder;
import org.jgrapht.alg.color.ChordalGraphColoring;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.cycle.ChordalGraphMinimalVertexSeparatorFinder;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.alg.independentset.ChordalGraphIndependentSetFinder;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.alg.partition.BipartitePartitioning;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GraphResearchService {
    public GraphResearchInfo research(GraphModel graphModel) {
        Graph<Vertex, WeightedEdge> graph = Converter.graphModelToGraph(graphModel);
        GraphResearchInfo info = new GraphResearchInfo();

        connectivityResearch(info, graph);

        if (graph.getType().isUndirected()) {
            planarityResearch(info, graph, graphModel.getMetadata());
            chordalityResearch(info, graph);
        } else {
            // flowResearch
        }

        bipartitePartitioningResearch(info, graph);

        //cycleResearch

        spanningResearch(info, graph);

        //VertexCover

        return info;
    }

    private void connectivityResearch(GraphResearchInfo info, Graph<Vertex, WeightedEdge> graph) {
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

    private void planarityResearch(GraphResearchInfo info, Graph<Vertex, WeightedEdge> graph, GraphMetadata metadata) {
        BoyerMyrvoldPlanarityInspector<Vertex, WeightedEdge> planarityInspector = new BoyerMyrvoldPlanarityInspector<>(graph);
        info.isPlanar = planarityInspector.isPlanar();

        if (info.isPlanar) {
            Map<Vertex, List<Edge>> embedding = new HashMap<>();
            for (Vertex v: graph.vertexSet()) {
                embedding.put(v, planarityInspector.getEmbedding().getEdgesAround(v).stream().map(WeightedEdge::toEdge).toList());
            }
            info.embedding = embedding;
        } else {
            info.kuratowskiSubgraph = Converter.graphToGraphModel(planarityInspector.getKuratowskiSubdivision(), metadata);
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
            info.coloring = coloring.getColorClasses().stream().map(st -> (List<Vertex>)new ArrayList<>(st)).toList();

            ChordalGraphMaxCliqueFinder<Vertex, WeightedEdge> maxCliqueFinder = new ChordalGraphMaxCliqueFinder<>(graph);
            info.maxClique = maxCliqueFinder.getClique().stream().toList();
            ChordalGraphIndependentSetFinder<Vertex, WeightedEdge> independentSetFinder = new ChordalGraphIndependentSetFinder<>(graph);
            info.independentSet = independentSetFinder.getIndependentSet().stream().toList();

            ChordalGraphMinimalVertexSeparatorFinder<Vertex, WeightedEdge> minimalVertexSeparatorFinder = new ChordalGraphMinimalVertexSeparatorFinder<>(graph);
            info.minimalVertexSeparator = minimalVertexSeparatorFinder.getMinimalSeparators().stream().map(Set::stream).map(Stream::toList).toList();
        }
    }

    private void bipartitePartitioningResearch(GraphResearchInfo info, Graph<Vertex, WeightedEdge> graph) {
        BipartitePartitioning<Vertex, WeightedEdge> bipartitePartitioningInspector = new BipartitePartitioning<>(graph);
        info.isBipartite = bipartitePartitioningInspector.isBipartite();
        if (info.isBipartite) {
            info.partitions = bipartitePartitioningInspector.getPartitioning().getPartitions().stream().map(s -> (List<Vertex>)new ArrayList<>(s)).toList();

            info.chromaticNumber = 2; //TODO
            info.coloring = info.partitions; //TODO

            if (info.partitions.get(0).size() < info.partitions.get(1).size()) {
                info.independentSet = info.partitions.get(1);
            } else {
                info.independentSet = info.partitions.get(0);
            }
        }
    }

    private void spanningResearch(GraphResearchInfo info, Graph<Vertex, WeightedEdge> graph) {
        KruskalMinimumSpanningTree<Vertex,WeightedEdge> KruskalMinimumSpanningTreeAlgo = new KruskalMinimumSpanningTree<>(graph);
        info.minSpanningTree = KruskalMinimumSpanningTreeAlgo.getSpanningTree().getEdges().stream().map(WeightedEdge::toEdge).toList();
    }
}
