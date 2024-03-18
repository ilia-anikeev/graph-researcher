package com.graphResearcher.model;

public enum GraphType {
    PSEUDO_GRAPH,
    MULTI_GRAPH,
    SIMPLE_GRAPH;

    public static GraphType fromString(String value) {
        for (GraphType type : GraphType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant " + GraphType.class.getName() + "." + value);
    }
}