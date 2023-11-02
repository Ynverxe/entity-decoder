package com.github.ynverxe.entitydecoder;

import com.github.ynverxe.entitydecoder.exception.InvalidEntityTypeId;
import com.github.ynverxe.entitydecoder.exception.MissingTagException;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public interface EntityFactory {

  EntityFactory ENTITY_FACTORY = (type, entityData) -> new Entity(type);
  EntityFactory LIVING_ENTITY_FACTORY = (type, entityData) -> new LivingEntity(type);

  @NotNull Entity createByType(@NotNull EntityType type, @NotNull NBTCompound entityData) throws Exception;

  default Entity guessTypeAndCreate(@NotNull NBTCompound entityData) throws Exception {
    String typeId = entityData.getString("id");

    if (typeId == null) {
      throw new MissingTagException("Missing Entity type ID");
    }

    EntityType type = EntityType.fromNamespaceId(typeId);

    if (type == null)
      throw new InvalidEntityTypeId(typeId);

    return createByType(type, entityData);
  }
}