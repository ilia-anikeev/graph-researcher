package com.graphResearcher.service;

import com.graphResearcher.model.graphInfo.FlowResearchInfo;
import com.graphResearcher.repository.GraphManager;
import com.graphResearcher.repository.UserManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.graphResearcher.model.graphInfo.GraphResearchInfo;
import com.graphResearcher.repository.InfoManager;
import com.graphResearcher.model.GraphModel;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
public class GraphArchiveService {
    private final GraphManager graphManager;
    private final InfoManager infoManager;
    private final UserManager userManager;

    @Async
    public Future<Integer> saveGraph(int userID, GraphModel graphModel) {
        return CompletableFuture.completedFuture(graphManager.saveGraph(userID, graphModel));
    }

    @Async
    public void saveResearchResult(int userID, int graphID, GraphResearchInfo researchResult) {
        infoManager.saveResearchInfo(userID, graphID, researchResult);
    }

    @Async
    public void saveFlowResult(int graphID, FlowResearchInfo flowResearchInfo) {
        infoManager.saveFlowResearch(graphID, flowResearchInfo);
    }

    @Async
    public Future<Map<Integer, String>> getAllUserGraphIDs(int userID) {
        return CompletableFuture.completedFuture(userManager.getAllUserGraphIDs(userID));
    }

    @Async
    public Future<GraphModel> getGraph(int graphID) {
        return CompletableFuture.completedFuture(graphManager.getGraph(graphID));
    }

    @Async
    public Future<GraphResearchInfo> getGraphInfo(int graphID) {
        return CompletableFuture.completedFuture(infoManager.getResearchInfo(graphID));

    }
    @Async
    public Future<String> getComment(int graphID) {
        return CompletableFuture.completedFuture(infoManager.getComment(graphID));
    }

    @Async
    public void deleteGraph(int graphID) {
        graphManager.deleteGraph(graphID);
        infoManager.deleteResearchInfo(graphID);
    }

    @Async
    public void saveComment(int graphID, String comment) {
        infoManager.saveComment(graphID, comment);
    }

    public GraphArchiveService(InfoManager infoManager, GraphManager graphManager, UserManager userManager) {
        this.infoManager = infoManager;
        this.graphManager = graphManager;
        this.userManager = userManager;
    }
}