package com.github.ynverxe.entitydecoder.loader;

import com.github.ynverxe.entitydecoder.EntityExpansion;
import com.github.ynverxe.entitydecoder.handler.ChunkEntitiesHandler;
import com.github.ynverxe.entitydecoder.handler.RegionalizedEntitiesHandler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class EntityChunkLoader implements IChunkLoader {

  private final @NotNull ChunkEntitiesHandler entitiesHandler;
  private final @NotNull IChunkLoader delegate;

  public EntityChunkLoader(@NotNull ChunkEntitiesHandler entitiesHandler, @NotNull IChunkLoader delegate) {
    this.entitiesHandler = entitiesHandler;
    this.delegate = delegate;
  }

  @Override
  public void loadInstance(@NotNull Instance instance) {
    delegate.loadInstance(instance);
  }

  @Override
  public @NotNull CompletableFuture<@Nullable Chunk> loadChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
    return delegate.loadChunk(instance, chunkX, chunkZ).thenApply(chunk -> {
      instance.scheduleNextTick(unused -> {
        Chunk loaded = instance.getChunk(chunkX, chunkZ);

        if (loaded == null) return; // better safe than sorry

        onChunkLoad(loaded);
      });

      return chunk;
    });
  }

  @Override
  public @NotNull CompletableFuture<Void> saveChunk(@NotNull Chunk chunk) {
    return delegate.saveChunk(chunk).thenAccept(unused -> onChunkSave(chunk));
  }

  @Override
  public @NotNull CompletableFuture<Void> saveInstance(@NotNull Instance instance) {
    return delegate.saveInstance(instance);
  }

  @Override
  public boolean supportsParallelLoading() {
    return delegate.supportsParallelLoading();
  }

  @Override
  public boolean supportsParallelSaving() {
    return delegate.supportsParallelSaving();
  }

  @Override
  public void unloadChunk(Chunk chunk) {
    delegate.unloadChunk(chunk);

    if (!chunk.isLoaded()) {
      entitiesHandler.handleChunkUnload(chunk);
    }
  }

  private void onChunkLoad(Chunk chunk) {
    if (chunk == null) return;

    try {
      entitiesHandler.loadEntities(chunk);
    } catch (Exception e) {
      MinecraftServer.getExceptionManager().handleException(e);
    }
  }

  private void onChunkSave(Chunk chunk) {
    try {
      entitiesHandler.saveEntities(chunk);
    } catch (Exception e) {
      MinecraftServer.getExceptionManager().handleException(e);
    }
  }

  public static @NotNull EntityChunkLoader newRegionalizedLoader(
    @NotNull EntityExpansion entityExpansion, @NotNull Path worldPath, @NotNull IChunkLoader loader) {
    return new EntityChunkLoader(RegionalizedEntitiesHandler.byWorldDir(entityExpansion, worldPath), loader);
  }

  public static @NotNull EntityChunkLoader newRegionalizedAnvilLoader(
    @NotNull EntityExpansion entityExpansion, @NotNull Path worldPath) {
    return new EntityChunkLoader(RegionalizedEntitiesHandler.byWorldDir(entityExpansion, worldPath), new AnvilLoader(worldPath));
  }
}