package com.pfm.history;

import java.util.List;

public interface DifferenceProvider<E> {

  String UPDATE_ENTRY_TEMPLATE = "%s changed from '%s' to '%s'";
  String ENTRY_VALUES_TEMPLATE = "The value of '%s' '%s' property is '%s'";

  List<String> getDifferences(E e);

  List<String> getObjectPropertiesWithValues();

  String getObjectDescriptiveName();

}