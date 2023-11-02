package com.github.ynverxe.entitydecoder;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class RichEntityFactory implements EntityFactory {

  private final EntityFactory factory;
  private final EntityConfigurator configurator;

  public RichEntityFactory(EntityFactory factory, EntityConfigurator configurator) {
    this.factory = factory;
    this.configurator = configurator;
  }

  @Override
  public Entity guessTypeAndCreate(@NotNull NBTCompound entityData) throws Exception {
    Entity entity = factory.guessTypeAndCreate(entityData);
    configurator.configure(entity, entityData);
    return entity;
  }

  @Override
  public @NotNull Entity createByType(@NotNull EntityType type, @NotNull NBTCompound entityData) throws Exception {
    Entity entity = factory.createByType(type, entityData);
    configurator.configure(entity, entityData);
    return entity;
  }

  public EntityConfigurator configurator() {
    return configurator;
  }

  public EntityFactory factory() {
    return factory;
  }
}