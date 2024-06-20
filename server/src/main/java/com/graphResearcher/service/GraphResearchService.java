package com.graphResearcher.service;

import com.graphResearcher.controller.GraphResearchController;
import com.graphResearcher.model.*;
import com.graphResearcher.model.graphInfo.*;
import com.graphResearcher.util.Converter;
import org.jgrapht.Graph;
import org.jgrapht.alg.clique.ChordalGraphMaxCliqueFinder;
import org.jgrapht.alg.color.ChordalGraphColoring;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.cycle.ChordalGraphMinimalVertexSeparatorFinder;
import org.jgrapht.alg.cycle.ChordalityInspector;
import org.jgrapht.alg.flow.DinicMFImpl;
import org.jgrapht.alg.independentset.ChordalGraphIndependentSetFinder;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.alg.partition.BipartitePartitioning;
import org.jgrapht.alg.planar.BoyerMyrvoldPlanarityInspector;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GraphResearchService {
    private static final Logger log = LoggerFactory.getLogger(GraphResearchController.class);

    @Async
    public Future<GraphResearchInfo> research(GraphModel graphModel) {
        Graph<Vertex, WeightedEdge> graph = Converter.graphModelToGraph(graphModel);
        GraphResearchInfo info = new GraphResearchInfo();


        Future<ConnectivityInfo> connectivityInfoFuture = connectivityResearch(graph);
        Future<PlanarityInfo> planarityInfoFuture = null;
        Future<ChordalityInfo> chordalityInfoFuture = null;
        Future<BipartitePartitioningInfo> bipartitePartitioningInfoFuture = null;
        if (graph.getType().isUndirected()) {
            planarityInfoFuture = planarityResearch(graph, graphModel.getMetadata());
            chordalityInfoFuture = chordalityResearch(graph);
            bipartitePartitioningInfoFuture = bipartitePartitioningResearch(graph);
        }

        try {
            info.connectivityInfo = connectivityInfoFuture.get();
            if (planarityInfoFuture != null) {
                info.planarityInfo = planarityInfoFuture.get();
            }
            if (chordalityInfoFuture != null) {
                info.chordalityInfo = chordalityInfoFuture.get();
            }
            if (bipartitePartitioningInfoFuture != null) {
                info.bipartitePartitioningInfo = bipartitePartitioningInfoFuture.get();
            }
            info.minSpanningTree = spanningResearch(graph).get();
        } catch (ExecutionException | InterruptedException e) {
            log.error("research error");
        }
        return CompletableFuture.completedFuture(info);
    }

    @Async
    public Future<FlowResearchInfo> flowResearch(GraphModel graphModel, int source, int sink) {
        FlowResearchInfo flowResearchInfo = new FlowResearchInfo();

        Vertex sourceVertex = null;
        Vertex sinkVertex = null;
        for (Vertex v: graphModel.getVertices()) {
            if (v.getIndex() == source) {
                sourceVertex = v;
            } else if (v.getIndex() == sink) {
                sinkVertex = v;
            }
        }
        Graph<Vertex, WeightedEdge> graph = Converter.graphModelToGraph(graphModel);

        DinicMFImpl<Vertex, WeightedEdge> dinicMF = new DinicMFImpl<>(graph);
        MaximumFlowAlgorithm.MaximumFlow<WeightedEdge> maximumFlow = dinicMF.getMaximumFlow(sourceVertex, sinkVertex);
        flowResearchInfo.maxFlow = maximumFlow.getValue();
        Map<Edge, Double> flow = new HashMap<>();
        for (Edge e: graphModel.getEdges()) {
            WeightedEdge weightedEdge = graph.getEdge(e.source, e.target);
            double f = maximumFlow.getFlow(weightedEdge);
            flow.put(e, f);
        }
        flowResearchInfo.flow = flow;
        return CompletableFuture.completedFuture(flowResearchInfo);
    }

    @Async
    Future<ConnectivityInfo> connectivityResearch(Graph<Vertex, WeightedEdge> graph) {
        ConnectivityInfo connectivityInfo = new ConnectivityInfo();

        BiconnectivityInspector<Vertex, WeightedEdge> biconnectivityInspector = new BiconnectivityInspector<>(graph);
        ConnectivityInspector<Vertex, WeightedEdge> connectivityInspector = new ConnectivityInspector<>(graph);

        connectivityInfo.isConnected = biconnectivityInspector.isConnected();

        connectivityInfo.isBiconnected = biconnectivityInspector.isBiconnected();

        connectivityInfo.articulationPoints = new ArrayList<>(biconnectivityInspector.getCutpoints());

        connectivityInfo.bridges = biconnectivityInspector.getBridges().stream()
                .map(WeightedEdge::toEdge)
                .collect(Collectors.toList());

        connectivityInfo.connectedComponents = connectivityInspector.connectedSets().stream().map(v -> (List<Vertex>) new ArrayList<>(v)).toList();

        connectivityInfo.blocks = biconnectivityInspector.getBlocks().stream()
                .map(block -> (List<Vertex>) new ArrayList<>(block.vertexSet())).toList();
        return CompletableFuture.completedFuture(connectivityInfo);
    }

    @Async
    Future<PlanarityInfo> planarityResearch(Graph<Vertex, WeightedEdge> graph, GraphMetadata metadata) {
        PlanarityInfo planarityInfo = new PlanarityInfo();
        BoyerMyrvoldPlanarityInspector<Vertex, WeightedEdge> planarityInspector = new BoyerMyrvoldPlanarityInspector<>(graph);
        planarityInfo.isPlanar = planarityInspector.isPlanar();

        if (planarityInfo.isPlanar) {
            Map<Vertex, List<Edge>> embedding = new HashMap<>();
            for (Vertex v : graph.vertexSet()) {
                embedding.put(v, planarityInspector.getEmbedding().getEdgesAround(v).stream().map(WeightedEdge::toEdge).toList());
            }
            planarityInfo.embedding = embedding;
        } else {
            planarityInfo.kuratowskiSubgraph = Converter.graphToGraphModel(planarityInspector.getKuratowskiSubdivision(), metadata);
        }

        return CompletableFuture.completedFuture(planarityInfo);
    }

    @Async
    Future<ChordalityInfo> chordalityResearch(Graph<Vertex, WeightedEdge> graph) {
        ChordalityInfo chordalityInfo = new ChordalityInfo();
        ChordalityInspector<Vertex, WeightedEdge> chordalityInspector = new ChordalityInspector<>(graph);
        chordalityInfo.isChordal = chordalityInspector.isChordal();
        if (chordalityInfo.isChordal) {
            chordalityInfo.perfectEliminationOrder = chordalityInspector.getPerfectEliminationOrder();

            ChordalGraphMaxCliqueFinder<Vertex, WeightedEdge> maxCliqueFinder = new ChordalGraphMaxCliqueFinder<>(graph);
            chordalityInfo.maxClique = maxCliqueFinder.getClique().stream().toList();
            ChordalGraphIndependentSetFinder<Vertex, WeightedEdge> independentSetFinder = new ChordalGraphIndependentSetFinder<>(graph);
            chordalityInfo.independentSet = independentSetFinder.getIndependentSet().stream().toList();

            ChordalGraphColoring<Vertex, WeightedEdge> coloringResearcher = new ChordalGraphColoring<>(graph);
            VertexColoringAlgorithm.Coloring<Vertex> coloring = coloringResearcher.getColoring();
            chordalityInfo.chromaticNumber = coloring.getNumberColors();
            chordalityInfo.coloring = coloring.getColorClasses().stream().map(st -> (List<Vertex>) new ArrayList<>(st)).toList();

            ChordalGraphMinimalVertexSeparatorFinder<Vertex, WeightedEdge> minimalVertexSeparatorFinder = new ChordalGraphMinimalVertexSeparatorFinder<>(graph);
            chordalityInfo.minimalVertexSeparator = minimalVertexSeparatorFinder.getMinimalSeparators().stream().map(Set::stream).map(Stream::toList).toList();
        }
        return CompletableFuture.completedFuture(chordalityInfo);
    }

    @Async
    Future<BipartitePartitioningInfo> bipartitePartitioningResearch(Graph<Vertex, WeightedEdge> graph) {
        BipartitePartitioningInfo bipartitePartitioningInfo = new BipartitePartitioningInfo();

        BipartitePartitioning<Vertex, WeightedEdge> bipartitePartitioningInspector = new BipartitePartitioning<>(graph);
        bipartitePartitioningInfo.isBipartite = bipartitePartitioningInspector.isBipartite();
        if (bipartitePartitioningInfo.isBipartite) {
            bipartitePartitioningInfo.partitions = bipartitePartitioningInspector.getPartitioning().getPartitions().stream().map(s -> (List<Vertex>) new ArrayList<>(s)).toList();

            bipartitePartitioningInfo.chromaticNumber = 2;
            if (bipartitePartitioningInfo.partitions.get(0).isEmpty()) {
                bipartitePartitioningInfo.chromaticNumber--;
            }
            if (bipartitePartitioningInfo.partitions.get(1).isEmpty()) {
                bipartitePartitioningInfo.chromaticNumber--;
            }
            bipartitePartitioningInfo.coloring = bipartitePartitioningInfo.partitions;

            if (bipartitePartitioningInfo.partitions.get(0).size() < bipartitePartitioningInfo.partitions.get(1).size()) {
                bipartitePartitioningInfo.independentSet = bipartitePartitioningInfo.partitions.get(1);
            } else {
                bipartitePartitioningInfo.independentSet = bipartitePartitioningInfo.partitions.get(0);
            }
        }
        return CompletableFuture.completedFuture(bipartitePartitioningInfo);
    }

    @Async
    Future<List<Edge>> spanningResearch(Graph<Vertex, WeightedEdge> graph) {
        KruskalMinimumSpanningTree<Vertex,WeightedEdge> KruskalMinimumSpanningTreeAlgo = new KruskalMinimumSpanningTree<>(graph);
        List<Edge> spanningTree = KruskalMinimumSpanningTreeAlgo.getSpanningTree().getEdges().stream().map(WeightedEdge::toEdge).toList();
        return CompletableFuture.completedFuture(spanningTree);
    }
}
