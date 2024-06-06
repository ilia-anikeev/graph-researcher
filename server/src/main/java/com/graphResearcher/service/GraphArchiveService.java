package com.graphResearcher.service;

import org.springframework.stereotype.Service;

import com.graphResearcher.model.GraphResearchInfo;
import com.graphResearcher.repository.DataBaseManager;
import com.graphResearcher.model.GraphModel;

import java.util.List;

@Service
public class GraphArchiveService {
    private final DataBaseManager db;

    public int saveGraph(int userID, GraphModel graphModel) {
        return db.saveGraph(userID, graphModel);
    }

    public void saveResearchResult(int userID, int graphID, GraphResearchInfo researchResult) {
        db.saveResearchInfo(userID, graphID, researchResult);
    }

    public List<GraphModel> getAllUserGraphs(int userID) {
        return db.getAllUserGraphs(userID);
    }

    public GraphArchiveService(DataBaseManager db) {
        this.db = db;
    }
}