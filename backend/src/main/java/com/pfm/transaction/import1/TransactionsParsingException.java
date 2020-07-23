package com.pfm.transaction.import1;

public class TransactionsParsingException extends Exception {

  private static final String MESSAGE = "An error occurred during parsing transactions from file";
  private static final long serialVersionUID = 2L;

  public TransactionsParsingException(Exception cause) {
    super(MESSAGE, cause);
  }
}
