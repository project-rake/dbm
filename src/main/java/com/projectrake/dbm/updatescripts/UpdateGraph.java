package com.projectrake.dbm.updatescripts;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BidirectionalDijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * Created on 21.11.2017.
 * <p>
 * Class representing a directed graph of scripts from version to version.
 */
public class UpdateGraph {
    private DefaultDirectedGraph<Integer, ScriptEdge> graph;

    public UpdateGraph(DefaultDirectedGraph<Integer, ScriptEdge> graph) {
        this.graph = graph;
    }

    public boolean hasPath(int from, int to) {
        return graphPath(from, to) != null;
    }

    public String getUpdateScript(int from, int to) {
        StringBuilder builder = new StringBuilder();
        graphPath(from, to).getEdgeList().stream().map(ScriptEdge::getLabel).map(UpdateScript::getScript).forEachOrdered(builder::append);
        return builder.toString();
    }

    private GraphPath<Integer, ScriptEdge> graphPath(int from, int to) {
        BidirectionalDijkstraShortestPath<Integer, ScriptEdge> bidij = new BidirectionalDijkstraShortestPath<>(graph);
        GraphPath<Integer, ScriptEdge> path = bidij.getPath(from, to);
        return path;
    }
}