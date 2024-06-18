package com.graphResearcher.service;

import com.graphResearcher.model.graphInfo.FlowResearchInfo;
import com.graphResearcher.repository.GraphManager;
import com.graphResearcher.repository.UserManager;
import org.springframework.stereotype.Service;

import com.graphResearcher.model.graphInfo.GraphResearchInfo;
import com.graphResearcher.repository.InfoManager;
import com.graphResearcher.model.GraphModel;

import java.util.List;
import java.util.Map;

@Service
public class GraphArchiveService {
    private final GraphManager graphManager;
    private final InfoManager infoManager;
    private final UserManager userManager;


    public int saveGraph(int userID, GraphModel graphModel) {
        return graphManager.saveGraph(userID, graphModel);
    }

    public void saveResearchResult(int userID, int graphID, GraphResearchInfo researchResult) {
        infoManager.saveResearchInfo(userID, graphID, researchResult);
    }

    public void saveFlowResult(int graphID, FlowResearchInfo flowResearchInfo) {
        infoManager.saveFlowResearch(graphID, flowResearchInfo);
    }

    public Map<Integer, String> getAllUserGraphIDs(int userID) {
        return userManager.getAllUserGraphIDs(userID);
    }

    public GraphModel getGraph(int graphID) {
        return graphManager.getGraph(graphID);
    }

    public GraphResearchInfo getGraphInfo(int graphID) {
        return infoManager.getResearchInfo(graphID);

    }

    public void deleteGraph(int graphID) {
        graphManager.deleteGraph(graphID);
        infoManager.deleteResearchInfo(graphID);
    }

    public GraphArchiveService(InfoManager infoManager, GraphManager graphManager, UserManager userManager) {
        this.infoManager = infoManager;
        this.graphManager = graphManager;
        this.userManager = userManager;
    }
}