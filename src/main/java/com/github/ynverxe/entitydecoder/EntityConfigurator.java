package com.github.ynverxe.entitydecoder;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.function.BiFunction;

public interface EntityConfigurator {

  void configure(@NotNull Entity entity, @NotNull NBTCompound entityData);

  default @NotNull EntityFactory factory(@NotNull BiFunction<EntityType, NBTCompound, Entity> entitySupplier) {
    return (type, entityData) -> {
      Entity entity = entitySupplier.apply(type, entityData);
      configure(entity, entityData);
      return entity;
    };
  }
}