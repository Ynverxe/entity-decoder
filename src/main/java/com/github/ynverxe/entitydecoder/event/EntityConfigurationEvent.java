package com.github.ynverxe.entitydecoder.event;

import com.github.ynverxe.entitydecoder.EntityConfigurator;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.trait.EntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class EntityConfigurationEvent implements EntityEvent {

  private final @NotNull EntityConfigurator configurator;
  private final @NotNull Entity entity;
  private final @NotNull NBTCompound entityData;

  public EntityConfigurationEvent(@NotNull EntityConfigurator configurator, @NotNull Entity entity, @NotNull NBTCompound entityData) {
    this.configurator = configurator;
    this.entity = entity;
    this.entityData = entityData;
  }

  public EntityConfigurator configurator() {
    return configurator;
  }

  @Override
  public @NotNull Entity getEntity() {
    return entity;
  }

  public NBTCompound entityData() {
    return entityData;
  }
}