package com.github.ynverxe.entitydecoder.event;

import com.github.ynverxe.entitydecoder.EntitySerializer;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.trait.EntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class EntitySerializationEvent implements EntityEvent {

  private final @NotNull EntitySerializer serializer;
  private final @NotNull Entity entity;
  private final @NotNull NBTCompound data;

  public EntitySerializationEvent(@NotNull EntitySerializer serializer, @NotNull Entity entity, @NotNull NBTCompound data) {
    this.serializer = serializer;
    this.entity = entity;
    this.data = data;
  }

  public EntitySerializer serializer() {
    return serializer;
  }

  @Override
  public @NotNull Entity getEntity() {
    return entity;
  }

  public NBTCompound data() {
    return data;
  }
}