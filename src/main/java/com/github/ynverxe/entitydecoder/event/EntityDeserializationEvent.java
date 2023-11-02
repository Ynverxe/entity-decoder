package com.github.ynverxe.entitydecoder.event;

import com.github.ynverxe.entitydecoder.EntityFactory;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

public class EntityDeserializationEvent implements EntityEvent {

  private final @NotNull EntityFactory factory;
  private final @NotNull Instance spawningInstance;
  private final @NotNull Entity entity;
  private @NotNull Pos spawnPosition;

  public EntityDeserializationEvent(@NotNull EntityFactory factory, @NotNull Instance spawningInstance, @NotNull Entity entity, @NotNull Pos spawnPosition) {
    this.factory = factory;
    this.spawningInstance = spawningInstance;
    this.entity = entity;
    this.spawnPosition = spawnPosition;
  }

  public EntityFactory caller() {
    return factory;
  }

  @Override
  public @NotNull Entity getEntity() {
    return entity;
  }

  public Instance spawningInstance() {
    return spawningInstance;
  }

  public Entity entity() {
    return entity;
  }

  public void setSpawnPosition(@NotNull Pos spawnPosition) {
    this.spawnPosition = spawnPosition;
  }

  public Pos spawnPosition() {
    return spawnPosition;
  }
}