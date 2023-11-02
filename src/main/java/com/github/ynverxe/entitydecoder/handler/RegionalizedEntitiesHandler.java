package com.github.ynverxe.entitydecoder.handler;

import com.github.ynverxe.entitydecoder.EntityExpansion;
import com.github.ynverxe.entitydecoder.event.EntityConfigurationEvent;
import com.github.ynverxe.entitydecoder.event.EntityDeserializationEvent;
import com.github.ynverxe.entitydecoder.event.EntitySerializationEvent;
import com.github.ynverxe.entitydecoder.util.file.EntityRegionFileUtil;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTList;
import org.jglrxavpok.hephaistos.nbt.NBTType;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores and reads the entity data of a world from NBT files
 * that contians an unnamed compound.
 *
 * The compound always has the following structure:
 * <pre>"": TAG_Compound {
 *   "chunkX:chunkZ": TAG_List[
 *      TAG_Compound {
 *        some entity data...
 *      }
 *   ]
 *}
 * </pre>
 */
@SuppressWarnings("all")
public class RegionalizedEntitiesHandler implements ChunkEntitiesHandler {

  private final @NotNull Map<IntIntImmutablePair, RegionData> entityDataByRegion = new ConcurrentHashMap<>();

  private final @NotNull EntityExpansion entityExpansion;
  private final @NotNull Path entitiesDir;

  public RegionalizedEntitiesHandler(@NotNull EntityExpansion entityExpansion, @NotNull Path entitiesDir) {
    this.entityExpansion = entityExpansion;
    this.entitiesDir = entitiesDir;
  }

  @Override
  public void loadEntities(@NotNull Chunk chunk) throws IOException {
    RegionData regionEntities = computeRegionData(chunk);

    MutableNBTCompound data = regionEntities.data;
    NBTList<NBTCompound> chunkEntities = data.getList(chunkKey(chunk.getChunkX(), chunk.getChunkZ()));

    if (chunkEntities == null) return;

    GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

    List<Entity> toSpawn = new ArrayList<>();
    Instance instance = chunk.getInstance();
    for (NBTCompound someEntityData : chunkEntities) {
      Entity entity;
      try {
        entity = entityExpansion.guessTypeAndCreate(someEntityData);

        entity.setInstance(instance);

        entityExpansion.configure(entity, someEntityData);

        eventHandler.call(new EntityConfigurationEvent(entityExpansion.configurator(), entity, someEntityData));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

      eventHandler.call(new EntityDeserializationEvent(entityExpansion.factory(), instance, entity, entity.getPosition()));
    }
  }

  @Override
  public void saveEntities(@NotNull Chunk chunk) throws IOException {
    Instance instance = chunk.getInstance();

    RegionData regionData = computeRegionData(chunk);
    MutableNBTCompound data = regionData.data;

    String chunkKey = chunkKey(chunk.getChunkX(), chunk.getChunkZ());
    NBTList<NBTCompound> chunkEntities = (NBTList<NBTCompound>) data.getOrElse(chunkKey, new NBTList<>(NBTType.TAG_Compound));

    List<NBTCompound> toSave = new ArrayList<>(chunkEntities.getValue());
    for (Entity chunkEntity : instance.getChunkEntities(chunk)) {
      NBTCompound serialized = entityExpansion.serialize(chunkEntity);

      toSave.add(serialized);

      EntitySerializationEvent event = new EntitySerializationEvent(entityExpansion.serializer(), chunkEntity, serialized);
      MinecraftServer.getGlobalEventHandler().call(event);

      data.put(chunkKey, new NBTList<>(NBTType.TAG_Compound, toSave));
    }

    int regionX = chunkToRegion(chunk.getChunkX());
    int regionZ = chunkToRegion(chunk.getChunkZ());

    EntityRegionFileUtil.saveRegionEntities(entitiesDir, regionX, regionZ, this::compress, data.toCompound());
  }

  @Override
  public void handleChunkUnload(@NotNull Chunk chunk) {
    IntIntImmutablePair regionCoordinates = regionCoords(chunk);

    RegionData data = this.entityDataByRegion.get(regionCoordinates);

    if (data == null) return;

    data.loadedChunks.remove(new IntIntImmutablePair(chunk.getChunkX(), chunk.getChunkZ()));

    if (data.loadedChunks.isEmpty()) {
      this.entityDataByRegion.remove(regionCoordinates);
    }
  }

  protected @NotNull InputStream decompress(@NotNull InputStream stream) {
    return stream;
  }

  protected @NotNull OutputStream compress(@NotNull OutputStream stream) {
    return stream;
  }

  protected IntIntImmutablePair regionCoords(@NotNull Chunk chunk) {
    return new IntIntImmutablePair(
      chunkToRegion(chunk.getChunkX()),
      chunkToRegion(chunk.getChunkZ())
    );
  }

  private RegionData computeRegionData(Chunk chunk) {
    return entityDataByRegion.computeIfAbsent(regionCoords(chunk), pair -> {
      try {
        int regionX = chunkToRegion(chunk.getChunkX());
        int regionZ = chunkToRegion(chunk.getChunkZ());

        NBTCompound data = EntityRegionFileUtil.loadRegionEntities(entitiesDir, regionX, regionZ, this::decompress);

        Map<String, NBT> map = new ConcurrentHashMap<>(data.asMapView());
        return new RegionData(new HashSet<>(), new MutableNBTCompound(map));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private int chunkToRegion(int coordinate) {
    return (int) Math.floor(coordinate / 32.0f);
  }

  private String chunkKey(int chunkX, int chunkZ) {
    return chunkX + ":" + chunkZ;
  }

  private static class RegionData {

    private final Set<IntIntImmutablePair> loadedChunks;
    private final MutableNBTCompound data;

    public RegionData(Set<IntIntImmutablePair> loadedChunks, MutableNBTCompound data) {
      this.loadedChunks = loadedChunks;
      this.data = data;
    }
  }

  public static @NotNull ChunkEntitiesHandler byWorldDir(@NotNull EntityExpansion entityExpansion, @NotNull Path worldDir) {
    return new RegionalizedEntitiesHandler(entityExpansion, worldDir.resolve("entities"));
  }
}