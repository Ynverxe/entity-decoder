package com.github.ynverxe.entitydecoder.file;

import com.github.ynverxe.entitydecoder.util.nbt.NBTFileLoader;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTList;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public final class EntityRegionFileUtil {

  private EntityRegionFileUtil() {}

  public static @NotNull NBTCompound loadRegionEntities(
    @NotNull Path dirPath, int regionX, int regionZ, @NotNull Function<InputStream, InputStream> decompresserStreamFunction) throws IOException {
    Path filePath = dirPath.resolve(createFileName(regionX, regionZ));

    if (!filePath.toFile().exists()) return NBTCompound.EMPTY;

    return (NBTCompound) NBTFileLoader.readFileNBT(filePath, decompresserStreamFunction);
  }

  public static void saveRegionEntities(
    @NotNull Path dirPath, int regionX, int regionZ,
    @NotNull Function<OutputStream, OutputStream> compressorStreamFunction, @NotNull NBTCompound data) throws IOException {
    Path filePath = dirPath.resolve(createFileName(regionX, regionZ));

    NBTFileLoader.saveNBT(filePath, data, compressorStreamFunction);
  }

  public static @NotNull String createFileName(int regionX, int regionZ) {
    return "entities." + regionX + "." + regionZ + ".nbt";
  }
}