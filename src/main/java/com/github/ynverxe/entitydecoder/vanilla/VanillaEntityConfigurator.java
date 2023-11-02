package com.github.ynverxe.entitydecoder.vanilla;

import com.github.ynverxe.entitydecoder.EntityConfigurator;
import com.github.ynverxe.entitydecoder.EntityFactory;
import com.github.ynverxe.entitydecoder.EntitySerializer;
import com.github.ynverxe.entitydecoder.exception.MissingTagException;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagHandler;
import net.minestom.server.tag.TagReadable;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.github.ynverxe.entitydecoder.vanilla.EntityVanillaConstants.*;

public class VanillaEntityExpansion implements EntityConfigurator<Entity>, EntitySerializer<Entity> {

  private static final Field FIRE_EXTINGUISH_TIME;

  static {
    try {
      FIRE_EXTINGUISH_TIME = LivingEntity.class.getDeclaredField("fireExtinguishTime");
      FIRE_EXTINGUISH_TIME.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private final EntityFactory<Entity> passengerResolver;

  public VanillaEntityExpansion(@NotNull EntityFactory<Entity> passengerResolver) {
    this.passengerResolver = passengerResolver;
  }

  public void

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

  @Override
  public @NotNull NBTCompound serialize(Entity entity) {
    TagHandler tagHandler = entity.tagHandler();

    EntityMeta entityMeta = entity.getEntityMeta();

    tagHandler.setTag(AIR, (short) entityMeta.getAirTicks());
    tagHandler.setTag(TICKS_FROZEN, entityMeta.getTickFrozen());

    tagHandler.setTag(ENTITY_ID, entity.getEntityType().namespace().asString());
    tagHandler.setTag(CUSTOM_NAME, entity.getCustomName());
    tagHandler.setTag(CUSTOM_NAME_VISIBLE, entity.isCustomNameVisible());
    tagHandler.setTag(GLOWING, entity.isGlowing());
    tagHandler.setTag(HAS_VISUAL_FIRE, entity.isOnFire());
    tagHandler.setTag(SILENT, entity.isSilent());
    tagHandler.setTag(UUID, entity.getUuid());
    tagHandler.setTag(NO_GRAVITY, entity.hasNoGravity());

    Pos pos = entity.getPosition();
    NBTList<NBTDouble> axis = new NBTList<>(NBTType.TAG_Double, Arrays.asList(
      new NBTDouble(pos.x()), new NBTDouble(pos.y()), new NBTDouble(pos.z())
    ));

    NBTList<NBTFloat> rotation = new NBTList<>(NBTType.TAG_Float, Arrays.asList(
      new NBTFloat(pos.yaw()), new NBTFloat(pos.pitch())
    ));

    tagHandler.setTag(POS, axis);
    tagHandler.setTag(ROTATION, rotation);

    Vec velocity = entity.getVelocity();
    NBTList<NBTDouble> motion = new NBTList<>(NBTType.TAG_Double, Arrays.asList(
      new NBTDouble(velocity.x()), new NBTDouble(velocity.y()), new NBTDouble(velocity.z())
    ));

    tagHandler.setTag(MOTION, motion);

    List<NBTCompound> passengerList = new ArrayList<>();
    for (Entity passenger : entity.getPassengers()) {
      passengerList.add(serialize(passenger));
    }

    tagHandler.setTag(PASSENGERS, new NBTList<>(NBTType.TAG_Compound, passengerList));

    tagHandler.setTag(FALL_DISTANCE, DEF_FALL_DISTANCE);
    tagHandler.setTag(ON_GROUND, entity.isOnGround());
    tagHandler.setTag(PORTAL_COOLDOWN, DEF_PORTAL_COOLDOWN);
    tagHandler.setTag(TAGS, defTags());

    if (entity instanceof LivingEntity livingEntity) {
      try {
        long fireTicks = FIRE_EXTINGUISH_TIME.getLong(livingEntity);
        long currentTimeMillis = System.currentTimeMillis();

        if (fireTicks <= currentTimeMillis) {
          fireTicks = 0;
        } else {
          fireTicks = fireTicks - currentTimeMillis;
        }

        tagHandler.setTag(FIRE_TICKS, (short) fireTicks);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }

      tagHandler.setTag(INVULNERABLE, livingEntity.isInvulnerable());
    }

    return tagHandler.asCompound();
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

    entity.refreshPosition(pos); // 🥶
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

  public static VanillaEntityExpansion withEntityPassengerResolver() {
    return new VanillaEntityExpansion((type, entries) -> new Entity(type));
  }

  public static VanillaEntityExpansion withLivingEntityPassengerResolver() {
    return new VanillaEntityExpansion((type, entries) -> new LivingEntity(type));
  }
}