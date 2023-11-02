package com.github.ynverxe.entitydecoder.exception;

public class InvalidEntityTypeId extends RuntimeException {

  public InvalidEntityTypeId() {
  }

  public InvalidEntityTypeId(String message) {
    super(message);
  }

  public InvalidEntityTypeId(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidEntityTypeId(Throwable cause) {
    super(cause);
  }

  public InvalidEntityTypeId(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}