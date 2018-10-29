package com.pfm.history;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public interface DifferenceProvider<E> {

  String UPDATE_ENTRY_TEMPLATE = "%s changed from '%s' to '%s'";
  String ENTRY_VALUES_TEMPLATE = "%s is %s";

  @JsonIgnore
  List<String> getDifferences(E e);

  @JsonIgnore
  List<String> getObjectPropertiesWithValues();

  @JsonIgnore
  String getObjectDescriptiveName();

}