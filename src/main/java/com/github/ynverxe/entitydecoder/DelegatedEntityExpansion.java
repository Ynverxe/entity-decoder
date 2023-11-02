package com.github.ynverxe.entitydecoder;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public record DelegatedEntityExpansion(@NotNull EntityConfigurator configurator,
                                       @NotNull EntityFactory factory,
                                       @NotNull EntitySerializer serializer) implements EntityExpansion {

  @Override
  public void configure(@NotNull Entity entity, @NotNull NBTCompound entityData) {
    configurator.configure(entity, entityData);
  }

  @Override
  public @NotNull Entity createByType(@NotNull EntityType type, @NotNull NBTCompound entityData) throws Exception {
    return factory.createByType(type, entityData);
  }

  @Override
  public Entity guessTypeAndCreate(@NotNull NBTCompound entityData) throws Exception {
    return factory.guessTypeAndCreate(entityData);
  }

  @Override
  public @NotNull NBTCompound serialize(@NotNull Entity entity) {
    return serializer.serialize(entity);
  }

  @Override
  public EntityConfigurator configurator() {
    return configurator;
  }

  @Override
  public EntityFactory factory() {
    return factory;
  }

  @Override
  public EntitySerializer serializer() {
    return serializer;
  }
}