package com.github.projectrake.dbm.updatescripts;

import org.jgrapht.graph.DefaultEdge;

/**
 * Created on 21.11.2017.
 *
 * ScriptEdge represents an directed edge from a version vertex to a target version vertex.
 */
public class ScriptEdge extends DefaultEdge {
    private int v1;
    private int v2;
    private UpdateScript label;

    public ScriptEdge(int v1, int v2, UpdateScript label) {
        this.v1 = v1;
        this.v2 = v2;
        this.label = label;
    }

    public int getV1() {
        return v1;
    }

    public int getV2() {
        return v2;
    }

    public UpdateScript getLabel() {
        return label;
    }

    public void setLabel(UpdateScript label) {
        this.label = label;
    }

    public String toString() {
        return "[" + v1 + ", " + v2 + "]";
    }
}