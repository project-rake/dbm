package com.projectrake.dbm.updatescripts;

import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.List;

/**
 * Created on 19.11.2017.
 * <p>
 * Factory class for {@link UpdateGraph} objects.
 */
public class UpdateGraphFactory {
    /**
     * Constructs an UpdateGraph from the provided scripts.
     *
     * @param scripts Scripts to construct the graph from.
     * @return UpdateGraph containing all scripts as edges and versions as vertices.
     */
    public UpdateGraph constructDependencyGraph(List<UpdateScript> scripts) {
        DefaultDirectedGraph<Integer, ScriptEdge> graph = new DefaultDirectedGraph<>(ScriptEdge.class);

        for (UpdateScript script : scripts) {
            for (Integer version : script.getHeader().getTo()) {
                if (!graph.containsVertex(version)) {
                    graph.addVertex(version);
                }
            }

            for (Integer version : script.getHeader().getFrom()) {
                if (!graph.containsVertex(version)) {
                    graph.addVertex(version);
                }
            }
        }

        for (UpdateScript script : scripts) {
            for (Integer fromversion : script.getHeader().getFrom()) {
                for (Integer toversion : script.getHeader().getTo()) {
                    graph.addEdge(fromversion, toversion, new ScriptEdge(fromversion, toversion, script));
                }
            }
        }

        return new UpdateGraph(graph);
    }
}
