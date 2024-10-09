//package com.graphResearcher.service;
//
//import com.graphResearcher.model.GraphModel;
//import com.graphResearcher.model.Vertex;
//import com.graphResearcher.model.Edge;
//import com.graphResearcher.model.GraphMetadata;
//import com.graphResearcher.repository.GraphManager;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
////@Service
//public class FileProcessingService {
//
//    private final GraphManager graphManager;
//    private final int userID;
//    public FileProcessingService(int userID, GraphManager graphManager) {
//        this.userID=userID;
//        this.graphManager = graphManager;
//    }
//
//    public String processFile(MultipartFile file,int userID) throws IOException {
//        List<Vertex> vertices = new ArrayList<>();
//        List<Edge> edges = new ArrayList<>();
//        String graphName = "Uploaded Graph: " + file.getOriginalFilename();
//        boolean isDirected = false;
//        boolean isWeighted = false;
//        boolean hasSelfLoops=false;
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
//            String line;
//            List<List<Double>> adjacencyMatrix = new ArrayList<>();
//            while ((line = reader.readLine()) != null) {
//                String[] values = line.trim().split("\\s+");
//                List<Double> row = new ArrayList<>();
//
//                for (String value : values) {
//                    double weight = Double.parseDouble(value);
//                    row.add(weight);
//
//                    if (weight != 0 && weight != 1) {
//                        isWeighted = true;
//                    }
//                }
//                adjacencyMatrix.add(row);
//            }
//
//            int matrixSize = adjacencyMatrix.size();
//
//            for (int i = 0; i < matrixSize; i++) {
//                Vertex vertex = new Vertex(i, "v" + i);
//                vertices.add(vertex);
//            }
//
//            for (int i = 0; i < matrixSize; i++) {
//                for (int j = 0; j < matrixSize; j++) {
//                    double weight = adjacencyMatrix.get(i).get(j);
//                    if(i==j && weight!=0){
//                        hasSelfLoops=true;
//                    }
//                    if (weight != 0) {
//                        Vertex source = vertices.get(i);
//                        Vertex target = vertices.get(j);
//                        Edge edge = new Edge(source, target, weight, "");
//                        edges.add(edge);
//                        if (!isDirected && i != j) {
//                            Edge reverseEdge = new Edge(target, source, weight, "");
//                            edges.add(reverseEdge);
//                        }
//                    }
//                }
//            }
//        }
//
//        GraphMetadata metadata = new GraphMetadata(graphName, isDirected, isWeighted, hasSelfLoops, false);
//        GraphModel graphModel = new GraphModel(vertices, edges, metadata);
//
//        graphManager.saveGraph(userID, graphModel);
//
//        return "File processed and graph saved to database: " + file.getOriginalFilename();
//    }
//}