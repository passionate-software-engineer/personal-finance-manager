package com.pfm.helpers.topology;

import com.pfm.helpers.topology.Graph.Edge;
import com.pfm.helpers.topology.Graph.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class TopologicalSortProvider {

  public static <T> List<Node<T>> sort(Graph<T> graph) {
    ArrayList<Node<T>> sortedElements = new ArrayList<>();

    HashSet<Node<T>> nodesWithNoIncomingEdges = new HashSet<Node<T>>();
    for (Node<T> node : graph.getNodes()) {
      if (node.getInEdges().size() == 0) {
        nodesWithNoIncomingEdges.add(node);
      }
    }

    while (!nodesWithNoIncomingEdges.isEmpty()) {
      // take first node from nodes not having incoming edges, add it to result list and remove from list of nodes to review
      Node<T> currentNode = nodesWithNoIncomingEdges.iterator().next();
      sortedElements.add(currentNode);
      nodesWithNoIncomingEdges.remove(currentNode);

      // remove all edges from currentNode to other nodes
      for (Iterator<Edge<T>> it = currentNode.getOutEdges().iterator(); it.hasNext(); ) {
        Edge<T> edge = it.next();
        it.remove(); // Remove edge from currentNode

        Node<T> nextNode = edge.getTo();
        nextNode.getInEdges().remove(edge); // Remove incoming edge from nextNode

        if (nextNode.getInEdges().isEmpty()) {
          nodesWithNoIncomingEdges.add(nextNode);
        }
      }
    }

    //Check to see if all edges are removed
    for (Node<T> node : graph.getNodes()) {
      if (!node.getInEdges().isEmpty()) {
        throw new IllegalStateException("Cycle detected, topological sort not possible");
      }
    }

    return sortedElements;
  }

}