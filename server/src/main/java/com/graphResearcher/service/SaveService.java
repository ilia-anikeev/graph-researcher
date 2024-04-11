package com.graphResearcher.service;


import com.graphResearcher.model.GraphResearchInfo;
import com.graphResearcher.dao.DataBaseManager;
import org.springframework.stereotype.Service;
import com.graphResearcher.model.GraphModel;

@Service
public class SaveService {
    //
    private final DataBaseManager db;

    public int saveGraph(int userID, GraphModel graphModel) {
        return db.saveGraph(userID, graphModel);
    }
//
    public void saveResearchResult(int userID, int graphID, GraphResearchInfo researchResult) {
        db.saveResearchInfo(userID, graphID, researchResult);
    }
    public SaveService(DataBaseManager db) {
        this.db = db;
    }
//
}