package com.enncompany.ecom.exception;

public class InvalidSortException extends RuntimeException {

  public InvalidSortException(String property) {
    super("Invalid sort property: " + property);
  }
}
