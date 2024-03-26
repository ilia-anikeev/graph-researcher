package com.graphResearcher.service;

import com.graphResearcher.dao.DataBaseManager;
import com.graphResearcher.model.GraphModel;
import com.graphResearcher.model.GraphResearchInfo;
import org.springframework.stereotype.Service;

@Service
public class SaveService {
    private final DataBaseManager db;

    public SaveService(DataBaseManager db) {
        this.db = db;
    }

    public int saveGraph(int userID, GraphModel graphModel) {
        return db.saveGraph(userID, graphModel);
    }

    public void saveResearchResult(int userID, int graphID, GraphResearchInfo researchResult) {
        db.saveResearchInfo(userID, graphID, researchResult);
    }
}
