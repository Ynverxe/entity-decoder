package com.github.ynverxe.entitydecoder.exception;

public class MissingTagException extends RuntimeException {

  public MissingTagException() {
  }

  public MissingTagException(String message) {
    super(message);
  }

  public MissingTagException(String message, Throwable cause) {
    super(message, cause);
  }

  public MissingTagException(Throwable cause) {
    super(cause);
  }

  public MissingTagException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}