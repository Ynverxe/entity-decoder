package com.github.ynverxe.entitydecoder;

import com.github.ynverxe.entitydecoder.exception.MissingTagException;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public interface EntityDecoder<T extends Entity> {

  @NotNull T decodeWithType(@NotNull EntityType type, @NotNull NBTCompound entityData) throws Exception;

  default T decode(@NotNull NBTCompound entityData) throws Exception {
    String typeId = entityData.getString("id");

    if (typeId == null) {
      throw new MissingTagException("Missing Entity type ID");
    }

    EntityType type = EntityType.fromNamespaceId(typeId);

    return decodeWithType(type, entityData);
  }
}