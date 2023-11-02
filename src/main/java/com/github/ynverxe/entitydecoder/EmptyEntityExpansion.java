package com.github.ynverxe.entitydecoder;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public class EmptyEntityExpansion implements EntityExpansion {

  public static final EmptyEntityExpansion INSTANCE = new EmptyEntityExpansion();

  private EmptyEntityExpansion() {}

  @Override
  public void configure(@NotNull Entity entity, @NotNull NBTCompound entityData) {

  }

  @Override
  public @Nullable Entity createByType(@NotNull EntityType type, @NotNull NBTCompound entityData) {
    return null;
  }

  @Override
  public @Nullable NBTCompound serialize(@NotNull Entity entity) {
    return null;
  }
}