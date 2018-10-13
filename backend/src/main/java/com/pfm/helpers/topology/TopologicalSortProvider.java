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

    //while nodesWithNoIncomingEdges is non-empty do
    while (!nodesWithNoIncomingEdges.isEmpty()) {
      //remove a node currentNode from nodesWithNoIncomingEdges
      Node<T> currentNode = nodesWithNoIncomingEdges.iterator().next();
      nodesWithNoIncomingEdges.remove(currentNode);

      //insert currentNode into sortedElements
      sortedElements.add(currentNode);

      //for each node m with an edge e from currentNode to m do
      for (Iterator<Edge<T>> it = currentNode.getOutEdges().iterator(); it.hasNext(); ) {
        //remove edge from the graph
        Edge<T> edge = it.next();
        Node<T> nextNode = edge.getTo();
        it.remove(); // Remove edge from currentNode
        nextNode.getInEdges().remove(edge); // Remove incoming edge from nextNode

        //if nextNode has no other incoming edges then insert nextNode into nodesWithNoIncomingEdges
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