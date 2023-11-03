package com.github.ynverxe.entitydecoder.vanilla;

import com.github.ynverxe.entitydecoder.DelegatedEntityExpansion;
import com.github.ynverxe.entitydecoder.EntityExpansion;
import com.github.ynverxe.entitydecoder.EntityFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class VanillaEntityExpansion {

  private VanillaEntityExpansion() {}

  public static @NotNull DelegatedEntityExpansion make(
    @NotNull EntityFactory entityFactory, @Nullable EntityFactory entityPassengersFactory, boolean configurePassengers) {
    if (entityPassengersFactory == null) {
      entityPassengersFactory = entityFactory;
    }

    return EntityExpansion.make(
      new VanillaEntityConfigurator(entityPassengersFactory, configurePassengers), entityFactory, new VanillaEntitySerializer());
  }

  public static @NotNull DelegatedEntityExpansion make(@NotNull EntityFactory entityFactory, boolean configurePassengers) {
    return make(entityFactory, null, configurePassengers);
  }

  public static @NotNull DelegatedEntityExpansion withEntityFactory() {
    return make(EntityFactory.ENTITY_FACTORY, true);
  }

  public static @NotNull DelegatedEntityExpansion withLivingEntityFactory() {
    return make(EntityFactory.LIVING_ENTITY_FACTORY, true);
  }
}