package com.pfm.helpers.topology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class Graph<T> {

  private List<Node<T>> nodes = new ArrayList<>();

  public Graph<T> addNode(Node<T> node) {
    this.nodes.add(node);
    return this;
  }

  @Getter
  @AllArgsConstructor
  public static final class Node<T> {

    private final T object;
    private final Set<Edge<T>> inEdges = new HashSet<>();
    private final Set<Edge<T>> outEdges = new HashSet<>();

    public Node<T> addEdge(Node<T> node) {
      Edge<T> edge = new Edge<>(this, node);
      outEdges.add(edge);
      node.inEdges.add(edge);
      return this;
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) {
        return true;
      }
      if (!(other instanceof Node)) {
        return false;
      }

      @SuppressWarnings("unchecked") final Node<T> otherNode = (Node<T>) other;

      // object cannot be null
      return this.object.equals(otherNode.getObject());
    }

    @Override
    public int hashCode() {
      return object.hashCode();
    }

    @Override
    public String toString() {
      return object.toString();
    }

  }

  @Data
  @AllArgsConstructor
  static final class Edge<T> {

    private final Node<T> from;
    private final Node<T> to;

  }

}
