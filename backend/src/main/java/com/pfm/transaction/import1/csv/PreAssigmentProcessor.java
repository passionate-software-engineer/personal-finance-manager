package com.pfm.transaction.import1.csv;

import com.opencsv.bean.processor.StringProcessor;

public class PreAssigmentProcessor implements StringProcessor {

  private final transient GeneralStringProcessor generalStringProcessor = new GeneralStringProcessor();

  @Override
  public String processString(String value) {
    return generalStringProcessor.replaceThenTrim(value, ",", ".");
  }

  @Override
  public void setParameterString(String value) {
  }
}
