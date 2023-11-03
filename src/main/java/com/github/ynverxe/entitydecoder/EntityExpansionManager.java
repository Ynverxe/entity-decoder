package com.github.ynverxe.entitydecoder;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityExpansionManager implements EntityExpansion {

  private final Map<EntityType, EntityExpansion> expansions = new ConcurrentHashMap<>();
  private @NotNull EntityExpansion fallbackExpansion = EntityExpansion.EMPTY;

  public @NotNull EntityExpansion getExpansion(@NotNull EntityType type) {
    return expansions.getOrDefault(type, fallbackExpansion);
  }

  public void registerExpansion(@NotNull EntityType type, @NotNull EntityExpansion expansion) {
    expansions.put(type, expansion);
  }

  public void removeSupportForEntityTypes(@NotNull EntityType... entityTypes) {
    for (EntityType entityType : entityTypes) {
      registerExpansion(entityType, EMPTY);
    }
  }

  public @NotNull Map<EntityType, EntityExpansion> expansions() {
    return expansions;
  }

  public EntityExpansionManager setFallbackExpansion(@NotNull EntityExpansion fallbackExpansion) {
    this.fallbackExpansion = fallbackExpansion;
    return this;
  }

  public @NotNull EntityExpansion fallbackExpansion() {
    return fallbackExpansion;
  }

  @Override
  public void configure(@NotNull Entity entity, @NotNull NBTCompound entityData) {
    EntityExpansion expansion = getExpansion(entity.getEntityType());

    expansion.configure(entity, entityData);
  }

  @Override
  public @Nullable Entity createByType(@NotNull EntityType type, @NotNull NBTCompound entityData) {
    return getExpansion(type).createByType(type, entityData);
  }

  @Override
  public @Nullable NBTCompound serialize(@NotNull Entity entity) {
    return getExpansion(entity.getEntityType()).serialize(entity);
  }
}