package com.graphResearcher.util;

import com.graphResearcher.model.GraphResearchInfo;
import com.graphResearcher.model.Vertex;

import java.util.ArrayList;
import java.util.List;

public class ParseResearchInfo {
    public String fieldsName;

    public String fields;

    public ParseResearchInfo(GraphResearchInfo info, int graphID) {
        fieldsName = getFieldsName(info);
        fields = getFields(info, graphID);
    }

    public String getFieldsName(GraphResearchInfo info) {
        List<String> notNullFields = new ArrayList<>();
        notNullFields.add("graph_id");
        if (info.isConnected != null) {
            notNullFields.add("isConnected");
        }
        if (info.isBiconnected != null) {
            notNullFields.add("isBiconnected");
        }
        if (info.articulationPoints != null) {
            notNullFields.add("articulation_points");
        }
        if (info.bridges != null) {
            notNullFields.add("bridges");
        }
        if (info.connectedComponents != null) {
            notNullFields.add("connected_components");
        }
        if (info.blocks != null) {
            notNullFields.add("blocks");
        }
        if (info.isPlanar != null) {
            notNullFields.add("isPlanar");
        }
        if (info.isChordal != null) {
            notNullFields.add("isChordal");
        }
        StringBuilder fields = new StringBuilder();
        for (int i = 0; i < notNullFields.size(); ++i) {
            if (i == 0) {
                fields.append(notNullFields.get(i));
            } else {
                fields.append(", ").append(notNullFields.get(i));
            }
        }
        return fields.toString();
    }
    public String getFields(GraphResearchInfo info, int graphID) {
        StringBuilder answer = new StringBuilder();

        answer.append(graphID);

        if (info.isConnected != null) {
            answer.append(", '");
            answer.append(info.isConnected);
            answer.append("'");
        }
        if (info.isBiconnected != null) {
            answer.append(", '");
            answer.append(info.isBiconnected);
            answer.append("'");
        }
        if (info.articulationPoints != null) {
            answer.append(", '");
            answer.append(info.articulationPoints.size());
            answer.append("'");
        }
        if (info.bridges != null) {
            answer.append(", '");
            answer.append(info.bridges.size());
            answer.append("'");
        }
        if (info.connectedComponents != null) {
            answer.append(", '");
            answer.append(info.connectedComponents.size());
            answer.append("'");
        }
        if (info.blocks != null) {
            answer.append(", '");
            answer.append(info.blocks.size());
            answer.append("'");
        }
        if (info.isPlanar != null) {
            answer.append(", '");
            answer.append(info.isPlanar);
            answer.append("'");
        }
        if (info.isChordal != null) {
            answer.append(", '");
            answer.append(info.isChordal);
            answer.append("'");
        }
        return answer.toString();
    }
}
