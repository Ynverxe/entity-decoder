package com.github.ynverxe.entitydecoder.util.nbt;

import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static java.nio.file.Files.*;

public final class NBTFileLoader {

  public static @NotNull NBT readFileNBT(
    @NotNull Path nbtFilePath, @NotNull Function<InputStream, InputStream> decompresserStreamFunction) throws IOException {
    File file = nbtFilePath.toFile();

    if (!file.exists())
      throw new IllegalStateException("File doesn't exists");

    InputStream inputStream = decompresserStreamFunction.apply(Files.newInputStream(nbtFilePath));
    try (NBTReader reader = new NBTReader(inputStream)) {
      return reader.readNamed().getSecond();
    } catch (NBTException e) {
      throw new RuntimeException(e);
    }
  }

  public static void saveNBT(
    @NotNull Path nbtFilePath, @NotNull NBT nbt, @NotNull Function<OutputStream, OutputStream> compressorStreamFunction) throws IOException {
    if (!exists(nbtFilePath)) {
      synchronized (NBTFileLoader.class) {
        Path parent = nbtFilePath.getParent();

        if (!exists(parent)) {
          createDirectories(parent);
        }

        createFile(nbtFilePath);
      }
    }

    OutputStream outputStream = compressorStreamFunction.apply(Files.newOutputStream(nbtFilePath));
    try (NBTWriter writer = new NBTWriter(outputStream)) {
      writer.writeNamed("", nbt);
    }
  }
}