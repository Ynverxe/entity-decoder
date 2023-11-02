package com.github.ynverxe.entitydecoder.util.compress;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.OutputStream;

public interface CompressionIOFactory {

  CompressionIOFactory IDENTITY = new CompressionIOFactory() {};

  default @NotNull InputStream decompress(@NotNull InputStream stream) {
    return stream;
  }

  default @NotNull OutputStream compress(@NotNull OutputStream stream) {
    return stream;
  }
}