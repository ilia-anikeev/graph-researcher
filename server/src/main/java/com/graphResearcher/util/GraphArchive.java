package com.graphResearcher.util;

import com.graphResearcher.model.GraphMetadata;
import com.graphResearcher.model.GraphModel;


public class GraphArchive {
    public GraphModel getPetersenGraph() {
        int[][] matrix = {
              // 1  2  3  4  5  6  7  8  9  10
                {0, 0, 1, 1, 0, 1, 0, 0, 0, 0}, // 1
                {0, 0, 0, 1, 1, 0, 1, 0, 0, 0}, // 2
                {1, 0, 0, 0, 1, 0, 0, 1, 0, 0}, // 3
                {1, 1, 0, 0, 0, 0, 0, 0, 1, 0}, // 4
                {0, 1, 1, 0, 0, 0, 0, 0, 0, 1}, // 5
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 1}, // 6
                {0, 1, 0, 0, 0, 1, 0, 1, 0, 0}, // 7
                {0, 0, 1, 0, 0, 0, 1, 0, 1, 0}, // 8
                {0, 0, 0, 1, 0, 0, 0, 1, 0, 1}, // 9
                {0, 0, 0, 0, 1, 1, 0, 0, 1, 0}, // 10
        };
        return Converter.buildGraphFromMatrix(matrix, new GraphMetadata(false, false, false, false));
    }

    public GraphModel getGrotzschGraph() {
        int[][] matrix = {
              // 1  2  3  4  5  6  7  8  9  10 11
                {0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0}, // 1
                {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1}, // 2
                {0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0}, // 3
                {0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1}, // 4
                {1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0}, // 5
                {0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1}, // 6
                {1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0}, // 7
                {0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1}, // 8
                {0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0}, // 9
                {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1}, // 10
                {0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0}, // 11
        };
        return Converter.buildGraphFromMatrix(matrix, new GraphMetadata(false, false, false, false));
    }

    public GraphModel getChvatalGraph() {
        int[][] matrix = {
              // 1  2  3  4  5  6  7  8  9  10 11 12
                {0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0}, // 1
                {1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0}, // 2
                {0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0}, // 3
                {1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1}, // 4
                {0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0}, // 5
                {0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0}, // 6
                {0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1, 0}, // 7
                {0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1}, // 8
                {0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1}, // 9
                {1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0}, // 10
                {1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1}, // 11
                {0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0}, // 12
        };
        return Converter.buildGraphFromMatrix(matrix, new GraphMetadata(false, false, false, false));
    }

    public GraphModel getApollonianGraph() {
        int[][] matrix = {
              // 1  2  3  4  5  6  7  8  9  10
                {0, 1, 1, 1, 1, 0, 0, 1, 1, 1}, // 1
                {1, 0, 1, 1, 1, 1, 0, 0, 0, 0}, // 2
                {0, 1, 0, 0, 1, 1, 1, 1, 0, 1}, // 3
                {1, 1, 0, 0, 1, 0, 0, 0, 0, 0}, // 4
                {1, 1, 1, 1, 0, 1, 1, 0, 0, 1}, // 5
                {0, 1, 1, 0, 1, 0, 0, 0, 0, 0}, // 6
                {0, 0, 1, 0, 1, 0, 0, 0, 0, 1}, // 7
                {1, 0, 1, 0, 0, 0, 0, 0, 1, 1}, // 8
                {1, 0, 0, 0, 0, 0, 0, 1, 0, 1}, // 9
                {1, 0, 1, 0, 1, 0, 1, 1, 1, 0}, // 10
        };
        return Converter.buildGraphFromMatrix(matrix, new GraphMetadata(false, false, false, false));
    }

    public GraphModel getHerschelGraph() {
        int[][] matrix = {
              // 1  2  3  4  5  6  7  8  9  10 11
                {0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0}, // 1
                {1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0}, // 2
                {0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1}, // 3
                {1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0}, // 4
                {0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1}, // 5
                {0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0}, // 6
                {0, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0}, // 7
                {0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1}, // 8
                {1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0}, // 9
                {0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1}, // 10
                {0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0}, // 11
        };
        return Converter.buildGraphFromMatrix(matrix, new GraphMetadata(false, false, false, false));
    }
}
