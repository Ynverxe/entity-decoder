package com.github.ynverxe.entitydecoder;

import org.jetbrains.annotations.NotNull;

public interface EntityExpansion extends EntityFactory, EntityConfigurator, EntitySerializer {

  EmptyEntityExpansion EMPTY = EmptyEntityExpansion.INSTANCE;

  static @NotNull DelegatedEntityExpansion make(
    @NotNull EntityConfigurator configurator, @NotNull EntityFactory factory, @NotNull EntitySerializer serializer) {
    return new DelegatedEntityExpansion(configurator, factory, serializer);
  }
}