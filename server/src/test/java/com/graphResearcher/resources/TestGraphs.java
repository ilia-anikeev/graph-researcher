package com.graphResearcher.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.graphInfo.GraphResearchInfo;


public class TestGraphs {
    public static final GraphModel simpleGraph = buildSimpleGraph();
    public static final GraphResearchInfo simpleGraphInfo = buildSimpleGraphInfo();

    public static final GraphModel directedGraph = buildDirectedGraph();
    public static final GraphResearchInfo directedGraphInfo = buildDirectedGraphInfo();


    public static final GraphModel oneEdgeGraph = buildOneEdgeGraph();
    public static final GraphResearchInfo oneEdgeGraphInfo = buildOneEdgeGraphInfo();

    public static final GraphModel directedWeighedGraph = buildDirectedWeighedGraph();
    public static final GraphResearchInfo directedWeighedGraphInfo = buildDirectedWeighedGraphInfo();


    private static GraphModel buildSimpleGraph() {
        String jsonString = """
                {
                        "vertices":[
                            {"index":1, "data": ""},
                            {"index":2, "data": ""},
                            {"index":3, "data": ""},
                            {"index":4, "data": ""}
                        ],

                        "edges":[
                                {"source":{"index":1, "data": ""},"target":{"index":2, "data": ""},"weight":1.0, "data": ""},
                                {"source":{"index":2, "data": ""},"target":{"index":3, "data": ""},"weight":1.0, "data": ""},
                                {"source":{"index":3, "data": ""},"target":{"index":4, "data": ""},"weight":1.0, "data": ""},
                                {"source":{"index":4, "data": ""},"target":{"index":1, "data": ""},"weight":1.0, "data": ""}
                                ],
                                
                        "info":{
                            "graphName": "square",
                            "isDirected":false,
                            "isWeighted":false,
                            "hasSelfLoops":false,
                            "hasMultipleEdges":false
                            }
                }
                """;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(jsonString);
            return new GraphModel(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static GraphResearchInfo buildSimpleGraphInfo() {
        String jsonString = "{\"isConnected\":true,\"isBiconnected\":true,\"articulationPoints\":[],\"bridges\":[],\"connectedComponents\":[[{\"index\":1,\"data\":\"\"},{\"index\":2,\"data\":\"\"},{\"index\":3,\"data\":\"\"},{\"index\":4,\"data\":\"\"}]],\"blocks\":[[{\"index\":1,\"data\":\"\"},{\"index\":2,\"data\":\"\"},{\"index\":3,\"data\":\"\"},{\"index\":4,\"data\":\"\"}]],\"isPlanar\":true,\"embedding\":{\"1\":[{\"source\":{\"index\":4,\"data\":\"\"},\"target\":{\"index\":1,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"},{\"source\":{\"index\":1,\"data\":\"\"},\"target\":{\"index\":2,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"}],\"2\":[{\"source\":{\"index\":1,\"data\":\"\"},\"target\":{\"index\":2,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"},{\"source\":{\"index\":2,\"data\":\"\"},\"target\":{\"index\":3,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"}],\"3\":[{\"source\":{\"index\":2,\"data\":\"\"},\"target\":{\"index\":3,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"},{\"source\":{\"index\":3,\"data\":\"\"},\"target\":{\"index\":4,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"}],\"4\":[{\"source\":{\"index\":3,\"data\":\"\"},\"target\":{\"index\":4,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"},{\"source\":{\"index\":4,\"data\":\"\"},\"target\":{\"index\":1,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"}]},\"isChordal\":false,\"isBipartite\":true,\"partitions\":[[{\"index\":1,\"data\":\"\"},{\"index\":3,\"data\":\"\"}],[{\"index\":2,\"data\":\"\"},{\"index\":4,\"data\":\"\"}]],\"chromaticNumber\":2,\"coloring\":[[{\"index\":1,\"data\":\"\"},{\"index\":3,\"data\":\"\"}],[{\"index\":2,\"data\":\"\"},{\"index\":4,\"data\":\"\"}]],\"independentSet\":[{\"index\":1,\"data\":\"\"},{\"index\":3,\"data\":\"\"}],\"min_spanning_tree\":[{\"source\":{\"index\":1,\"data\":\"\"},\"target\":{\"index\":2,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"},{\"source\":{\"index\":2,\"data\":\"\"},\"target\":{\"index\":3,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"},{\"source\":{\"index\":3,\"data\":\"\"},\"target\":{\"index\":4,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"}]}";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(jsonString);
            return new GraphResearchInfo(json, simpleGraph);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static GraphModel buildDirectedGraph() {
        String jsonString = """
                {
                        "vertices":[
                            {"index":1, "data": ""},
                            {"index":2, "data": ""},
                            {"index":3, "data": ""},
                            {"index":4, "data": ""},
                            {"index":5, "data": ""},
                            {"index":6, "data": ""}
                        ],
                        
                        "edges":[
                                {"source":{"index":1, "data": ""},"target":{"index":2, "data": ""},"weight":1.0, "data": ""},
                                {"source":{"index":2, "data": ""},"target":{"index":3, "data": ""},"weight":1.0, "data": ""},
                                {"source":{"index":3, "data": ""},"target":{"index":1, "data": ""},"weight":1.0, "data": ""},
                                {"source":{"index":3, "data": ""},"target":{"index":4, "data": ""},"weight":1.0, "data": ""},
                                {"source":{"index":4, "data": ""},"target":{"index":5, "data": ""},"weight":1.0, "data": ""},
                                {"source":{"index":5, "data": ""},"target":{"index":6, "data": ""},"weight":1.0, "data": ""},
                                {"source":{"index":6, "data": ""},"target":{"index":4, "data": ""},"weight":1.0, "data": ""}
                        ],
                                
                        "info":{"graphName": "directed_sandglass","isDirected":true,"isWeighted":false,"hasSelfLoops":false,"hasMultipleEdges":false}
                }
                """;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(jsonString);
            return new GraphModel(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static GraphResearchInfo buildDirectedGraphInfo() {
        String jsonString = "{\"isConnected\":true,\"isBiconnected\":false,\"articulationPoints\":[{\"index\":4,\"data\":\"\"},{\"index\":3,\"data\":\"\"}],\"bridges\":[{\"source\":{\"index\":3,\"data\":\"\"},\"target\":{\"index\":4,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"}],\"connectedComponents\":[[{\"index\":1,\"data\":\"\"},{\"index\":2,\"data\":\"\"},{\"index\":3,\"data\":\"\"},{\"index\":4,\"data\":\"\"},{\"index\":5,\"data\":\"\"},{\"index\":6,\"data\":\"\"}]],\"blocks\":[[{\"index\":4,\"data\":\"\"},{\"index\":5,\"data\":\"\"},{\"index\":6,\"data\":\"\"}],[{\"index\":3,\"data\":\"\"},{\"index\":4,\"data\":\"\"}],[{\"index\":1,\"data\":\"\"},{\"index\":2,\"data\":\"\"},{\"index\":3,\"data\":\"\"}]],\"min_spanning_tree\":[{\"source\":{\"index\":1,\"data\":\"\"},\"target\":{\"index\":2,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"},{\"source\":{\"index\":4,\"data\":\"\"},\"target\":{\"index\":5,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"},{\"source\":{\"index\":2,\"data\":\"\"},\"target\":{\"index\":3,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"},{\"source\":{\"index\":5,\"data\":\"\"},\"target\":{\"index\":6,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"},{\"source\":{\"index\":3,\"data\":\"\"},\"target\":{\"index\":4,\"data\":\"\"},\"weight\":1.0,\"data\":\"\"}]}";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(jsonString);
            return new GraphResearchInfo(json, simpleGraph);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static GraphModel buildDirectedWeighedGraph() {
        String jsonString = """
                {
                    "vertices": [
                        {"index": 1, "data": ""},
                        {"index": 2, "data": ""},
                        {"index": 3, "data": ""},
                        {"index": 4, "data": ""},
                        {"index": 5, "data": ""},
                        {"index": 6, "data": ""}
                    ],
                                
                    "edges": [
                        {"source": {"index": 1, "data": ""}, "target": {"index": 2, "data": ""}, "weight": 3.0, "data": ""},
                        {"source": {"index": 2, "data": ""}, "target": {"index": 3, "data": ""}, "weight": 2.0, "data": ""},
                        {"source": {"index": 1, "data": ""}, "target": {"index": 3, "data": ""}, "weight": 3.0, "data": ""},
                        {"source": {"index": 3, "data": ""}, "target": {"index": 4, "data": ""}, "weight": 2.0, "data": ""},
                        {"source": {"index": 2, "data": ""}, "target": {"index": 5, "data": ""}, "weight": 3.0, "data": ""},
                        {"source": {"index": 5, "data": ""}, "target": {"index": 4, "data": ""}, "weight": 4.0, "data": ""},
                        {"source": {"index": 5, "data": ""}, "target": {"index": 6, "data": ""}, "weight": 2.0, "data": ""},
                        {"source": {"index": 4, "data": ""}, "target": {"index": 6, "data": ""}, "weight": 3.0, "data": ""}
                    ],
                                
                    "info": {
                        "graphName": "directedWeighedGraphWithLoops",
                        "isDirected": true,
                        "isWeighted": true,
                        "hasSelfLoops": false,
                        "hasMultipleEdges": false
                    }
                }
                """;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(jsonString);
            return new GraphModel(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static GraphResearchInfo buildDirectedWeighedGraphInfo() {
        String jsonString = "{\"isConnected\":true,\"isBiconnected\":true,\"articulationPoints\":[],\"bridges\":[],\"connectedComponents\":[[{\"index\":1,\"data\":\"\"},{\"index\":2,\"data\":\"\"},{\"index\":3,\"data\":\"\"},{\"index\":4,\"data\":\"\"},{\"index\":5,\"data\":\"\"},{\"index\":6,\"data\":\"\"}]],\"blocks\":[[{\"index\":1,\"data\":\"\"},{\"index\":2,\"data\":\"\"},{\"index\":3,\"data\":\"\"},{\"index\":4,\"data\":\"\"},{\"index\":5,\"data\":\"\"},{\"index\":6,\"data\":\"\"}]],\"min_spanning_tree\":[{\"source\":{\"index\":1,\"data\":\"\"},\"target\":{\"index\":2,\"data\":\"\"},\"weight\":3.0,\"data\":\"\"},{\"source\":{\"index\":2,\"data\":\"\"},\"target\":{\"index\":5,\"data\":\"\"},\"weight\":3.0,\"data\":\"\"},{\"source\":{\"index\":3,\"data\":\"\"},\"target\":{\"index\":4,\"data\":\"\"},\"weight\":2.0,\"data\":\"\"},{\"source\":{\"index\":5,\"data\":\"\"},\"target\":{\"index\":6,\"data\":\"\"},\"weight\":2.0,\"data\":\"\"},{\"source\":{\"index\":2,\"data\":\"\"},\"target\":{\"index\":3,\"data\":\"\"},\"weight\":2.0,\"data\":\"\"}]}";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(jsonString);
            return new GraphResearchInfo(json, simpleGraph);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static GraphModel buildOneEdgeGraph() {
        String jsonString = """
                {
                  "vertices": [
                    {"index": 1, "data": "hi"},
                    {"index": 2, "data": "hello"}
                  ],
                  "edges": [
                    {
                      "source": {"index": 1, "data": "hi"},
                      "target": {"index": 2, "data": "hello"},
                      "weight": 1.0,
                      "data": "aloha"
                    }
                  ],
                  "info": {
                    "graphName": "one edge",
                    "isDirected": false,
                    "isWeighted": false,
                    "hasSelfLoops": false,
                    "hasMultipleEdges": false
                  }
                }
                """;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(jsonString);
            return new GraphModel(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static GraphResearchInfo buildOneEdgeGraphInfo() {
        String jsonString = "{\"isConnected\":true,\"isBiconnected\":true,\"articulationPoints\":[],\"bridges\":[{\"source\":{\"index\":1,\"data\":\"hi\"},\"target\":{\"index\":2,\"data\":\"hello\"},\"weight\":1.0,\"data\":\"\"}],\"connectedComponents\":[[{\"index\":1,\"data\":\"hi\"},{\"index\":2,\"data\":\"hello\"}]],\"blocks\":[[{\"index\":1,\"data\":\"hi\"},{\"index\":2,\"data\":\"hello\"}]],\"isPlanar\":true,\"embedding\":{\"1\":[{\"source\":{\"index\":1,\"data\":\"hi\"},\"target\":{\"index\":2,\"data\":\"hello\"},\"weight\":1.0,\"data\":\"\"}],\"2\":[{\"source\":{\"index\":1,\"data\":\"hi\"},\"target\":{\"index\":2,\"data\":\"hello\"},\"weight\":1.0,\"data\":\"\"}]},\"isChordal\":true,\"perfectEliminationOrder\":[{\"index\":1,\"data\":\"hi\"},{\"index\":2,\"data\":\"hello\"}],\"chromaticNumber\":2,\"coloring\":[[{\"index\":1,\"data\":\"hi\"}],[{\"index\":2,\"data\":\"hello\"}]],\"maxClique\":[{\"index\":1,\"data\":\"hi\"},{\"index\":2,\"data\":\"hello\"}],\"independentSet\":[{\"index\":1,\"data\":\"hi\"}],\"minimal_vertex_separator\":[],\"isBipartite\":true,\"partitions\":[[{\"index\":1,\"data\":\"hi\"}],[{\"index\":2,\"data\":\"hello\"}]],\"min_spanning_tree\":[{\"source\":{\"index\":1,\"data\":\"hi\"},\"target\":{\"index\":2,\"data\":\"hello\"},\"weight\":1.0,\"data\":\"\"}]}";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(jsonString);
            return new GraphResearchInfo(json, simpleGraph);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
