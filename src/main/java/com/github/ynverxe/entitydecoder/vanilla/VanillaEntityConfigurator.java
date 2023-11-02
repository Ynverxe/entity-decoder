package com.github.ynverxe.entitydecoder.vanilla;

import com.github.ynverxe.entitydecoder.EntityConfigurator;
import com.github.ynverxe.entitydecoder.EntityFactory;
import com.github.ynverxe.entitydecoder.RichEntityFactory;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagHandler;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.*;

import static com.github.ynverxe.entitydecoder.vanilla.EntityVanillaConstants.*;

public class VanillaEntityConfigurator implements EntityConfigurator {

  private final EntityFactory passengerResolver;

  public VanillaEntityConfigurator(@NotNull EntityFactory passengerResolver, boolean configurePassengers) {
    if (configurePassengers) {
      passengerResolver = new RichEntityFactory(passengerResolver, this);
    }

    this.passengerResolver = passengerResolver;
  }

  @Override
  public void configure(@NotNull Entity entity, @NotNull NBTCompound entityData) {
    TagHandler tagHandler = TagHandler.fromCompound(entityData);

    TagHandler unusedTags = TagHandler.fromCompound(new NBTCompound());

    EntityMeta entityMeta = entity.getEntityMeta();

    entityMeta.setAirTicks(tagHandler.getTag(AIR));
    entityMeta.setTickFrozen(tagHandler.getTag(TICKS_FROZEN));

    entity.setCustomName(tagHandler.getTag(CUSTOM_NAME));
    entity.setCustomNameVisible(tagHandler.getTag(CUSTOM_NAME_VISIBLE));
    entity.setGlowing(tagHandler.getTag(GLOWING));
    entity.setOnFire(tagHandler.getTag(HAS_VISUAL_FIRE));
    entity.setNoGravity(tagHandler.getTag(NO_GRAVITY));
    entity.setSilent(tagHandler.getTag(SILENT));
    entity.setUuid(tagHandler.getTag(UUID));

    deserializeVelocity(entity, (NBTList<NBTDouble>) tagHandler.getTag(MOTION));
    deserializePassengers(entity, (NBTList<NBTCompound>) tagHandler.getTag(PASSENGERS));
    deserializePosition(entity, (NBTList<NBTDouble>) tagHandler.getTag(POS), (NBTList<NBTFloat>) tagHandler.getTag(ROTATION));

    unusedTags.setTag(FALL_DISTANCE, tagHandler.getTag(FALL_DISTANCE));
    unusedTags.setTag(ON_GROUND, tagHandler.getTag(ON_GROUND));
    unusedTags.setTag(PORTAL_COOLDOWN, tagHandler.getTag(PORTAL_COOLDOWN));
    unusedTags.setTag(TAGS, tagHandler.getTag(TAGS));

    if (entity instanceof LivingEntity livingEntity) {
      livingEntity.setFireForDuration(tagHandler.getTag(FIRE_TICKS));
      livingEntity.setInvulnerable(tagHandler.getTag(INVULNERABLE));
    }

    tagHandler.setTag(Tag.NBT("vanilla-unused"), unusedTags.asCompound());
  }

  protected void deserializeVelocity(Entity entity, NBTList<NBTDouble> motion) {
    if (motion.isEmpty()) {
      return;
    }

    double x = motion.get(0).getValue();
    double y = motion.get(0).getValue();
    double z = motion.get(0).getValue();

    entity.setVelocity(new Vec(x, y, z));
  }

  protected void deserializePosition(Entity entity, NBTList<NBTDouble> axis, NBTList<NBTFloat> rotation) {
    Pos pos = Pos.ZERO;

    if (!axis.isEmpty()) {
      pos = pos.add(
        axis.get(0).getValue(),
        axis.get(1).getValue(),
        axis.get(2).getValue()
      );
    }

    if (!rotation.isEmpty()) {
      pos = pos.withView(
        rotation.get(0).getValue(),
        rotation.get(1).getValue()
      );
    }

    entity.teleport(pos); // 🥶
  }

  protected void deserializePassengers(Entity entity, NBTList<NBTCompound> passengersData) {
    for (NBTCompound entry : passengersData) {
      try {
        Entity passenger = passengerResolver.guessTypeAndCreate(entry);
        entity.addPassenger(passenger);
      } catch (Exception e) {
        throw new RuntimeException("Unable to decode passenger", e);
      }
    }
  }

  public static VanillaEntityConfigurator withEntityPassengerResolver(boolean configurePassengers) {
    return new VanillaEntityConfigurator((type, entries) -> new Entity(type), configurePassengers);
  }

  public static VanillaEntityConfigurator withLivingEntityPassengerResolver(boolean configurePassengers) {
    return new VanillaEntityConfigurator((type, entries) -> new LivingEntity(type), configurePassengers);
  }
}