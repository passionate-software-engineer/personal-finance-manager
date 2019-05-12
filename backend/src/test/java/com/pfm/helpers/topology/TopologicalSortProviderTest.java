package com.pfm.helpers.topology;

import static com.pfm.helpers.topology.TopologicalSortProvider.sort;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pfm.helpers.topology.Graph.Node;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

class TopologicalSortProviderTest {

  @Test
  void shouldSortGraphTopologically() {
    // given
    Node<String> two = new Node<>("2");
    Node<String> nine = new Node<>("9");
    Node<String> ten = new Node<>("10");

    Node<String> eleven = new Node<>("11")
        .addEdge(two)
        .addEdge(nine)
        .addEdge(ten);

    Node<String> eight = new Node<>("8")
        .addEdge(nine)
        .addEdge(ten);

    Node<String> seven = new Node<>("7")
        .addEdge(eleven)
        .addEdge(eight);

    Node<String> five = new Node<>("5")
        .addEdge(eleven);

    Node<String> three = new Node<>("3")
        .addEdge(eight)
        .addEdge(ten);

    Graph<String> graph = new Graph<String>()
        .addNode(seven)
        .addNode(five)
        .addNode(three)
        .addNode(eleven)
        .addNode(eight)
        .addNode(two)
        .addNode(nine)
        .addNode(ten);

    // when
    List<Node<String>> sortedNodes = sort(graph);

    // then
    assertThat(sortedNodes, hasSize(8));
    assertThat(sortedNodes.get(0), is(three));
    assertThat(sortedNodes.get(1), is(five));
    assertThat(sortedNodes.get(2), is(seven));
    assertThat(sortedNodes.get(3), is(eleven));
    assertThat(sortedNodes.get(4), is(two));
    assertThat(sortedNodes.get(5), is(eight));
    assertThat(sortedNodes.get(6), is(nine));
    assertThat(sortedNodes.get(7), is(ten));
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenCycleIsDetectedInGraph() {
    // given
    Node<Integer> one = new Node<>(1);
    Node<Integer> two = new Node<>(2);

    one.addEdge(two);
    two.addEdge(one);

    Graph<Integer> graph = new Graph<Integer>()
        .addNode(one)
        .addNode(two);

    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      // when
      List<Node<Integer>> sortedNodes = sort(graph);
    });

    //then
    assertThat(exception.getMessage(), CoreMatchers.is(equalTo("Cycle detected, topological sort not possible")));
  }

  // just to get coverage
  @Test
  void shouldNotThrowExceptionWhenCreatingObject() {
    // when
    TopologicalSortProvider provider = new TopologicalSortProvider();

    // then
    assertNotNull(provider);
  }
}
