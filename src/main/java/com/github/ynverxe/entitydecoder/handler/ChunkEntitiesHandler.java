package com.github.ynverxe.entitydecoder.handler;

import net.minestom.server.instance.Chunk;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ChunkEntitiesHandler {

  void loadEntities(@NotNull Chunk chunk) throws IOException;

  void saveEntities(@NotNull Chunk chunk) throws IOException;

  void handleChunkUnload(@NotNull Chunk chunk);

}