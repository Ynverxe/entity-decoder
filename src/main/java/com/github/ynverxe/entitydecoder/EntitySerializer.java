package com.github.ynverxe.entitydecoder;

import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public interface EntitySerializer {

  @Nullable NBTCompound serialize(@NotNull Entity entity);

}