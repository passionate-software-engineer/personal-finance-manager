package com.pfm.helpers.topology;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pfm.helpers.topology.Graph.Node;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class GraphTest {

  @Test
  void shouldEqualToItself() {
    // given
    Node<Double> node = new Node<>(0.9);

    // when
    boolean equals = node.equals(node);

    // then
    assertTrue(equals);
  }

  @Test
  void shouldEqualToObjectWithSaveValue() {
    // given
    String valueToTest = "first";
    Node<String> node1 = new Node<>(valueToTest);
    Node<String> node2 = new Node<>(valueToTest);

    // when
    boolean equals = node1.equals(node2);

    // then
    assertTrue(equals);
  }

  @Test
  @SuppressFBWarnings(value = "EC_UNRELATED_TYPES", justification = "Need to test not matching types")
  void shouldNotEqualToOtherObject() {
    // given
    Node<Float> node = new Node<>(0.9F);

    // when
    boolean equals = node.equals(0.99);

    // then
    assertFalse(equals);
  }

  @Test
  void shouldReturnHashCodeOfObjectWhenCallingHashCode() {
    // given
    String valueToTest = "Abracadabra";

    // when
    int hashCodeValue = new Node<>(valueToTest).hashCode();

    // then
    assertThat(hashCodeValue, is(valueToTest.hashCode()));
  }

  @Test
  void shouldReturnSameHashCodeIfObjectsAreEqual() {
    // given
    String valueToTest = "Kobranocka";
    Node<String> node1 = new Node<>(valueToTest);
    Node<String> node2 = new Node<>(valueToTest);

    // when
    int hashCodeValue1 = node1.hashCode();
    int hashCodeValue2 = node2.hashCode();

    // then
    assertThat(hashCodeValue1, is(hashCodeValue2));
  }

  @Test
  void shouldReturnElementToStringWhenCallingToString() {
    // given
    BigDecimal valueToTest = BigDecimal.TEN;

    // when
    String toStringValue = new Node<>(valueToTest).toString();

    // then
    assertThat(toStringValue, is(valueToTest.toString()));
  }
}
