package com.github.ynverxe.entitydecoder;

import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

public interface EntitySerializer {

  @NotNull NBTCompound serialize(@NotNull Entity entity);

}