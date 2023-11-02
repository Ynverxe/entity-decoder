package com.github.ynverxe.entitydecoder;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class DelegatedEntityExpansion implements EntityExpansion {

  private final @NotNull EntityConfigurator configurator;
  private final @NotNull EntityFactory factory;
  private final @NotNull EntitySerializer serializer;

  public DelegatedEntityExpansion(
    @NotNull EntityConfigurator configurator, @NotNull EntityFactory factory, @NotNull EntitySerializer serializer) {
    this.configurator = configurator;
    this.factory = factory;
    this.serializer = serializer;
  }

  @Override
  public void configure(@NotNull Entity entity, @NotNull NBTCompound entityData) {
    configurator.configure(entity, entityData);
  }

  @Override
  public @Nullable Entity createByType(@NotNull EntityType type, @NotNull NBTCompound entityData) {
    return factory.createByType(type, entityData);
  }

  @Override
  public @Nullable Entity guessTypeAndCreate(@NotNull NBTCompound entityData) {
    return factory.guessTypeAndCreate(entityData);
  }

  @Override
  public @NotNull NBTCompound serialize(@NotNull Entity entity) {
    return serializer.serialize(entity);
  }

  public EntityConfigurator configurator() {
    return configurator;
  }

  public EntityFactory factory() {
    return factory;
  }

  public EntitySerializer serializer() {
    return serializer;
  }
}