package com.github.ynverxe.entitydecoder.vanilla;

import com.github.ynverxe.entitydecoder.EntitySerializer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.tag.TagHandler;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.ynverxe.entitydecoder.vanilla.EntityVanillaConstants.*;

public class VanillaEntitySerializer implements EntitySerializer {

  private static final Field FIRE_EXTINGUISH_TIME;

  static {
    try {
      FIRE_EXTINGUISH_TIME = LivingEntity.class.getDeclaredField("fireExtinguishTime");
      FIRE_EXTINGUISH_TIME.setAccessible(true);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
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
        long fireMillis = FIRE_EXTINGUISH_TIME.getLong(livingEntity);
        long currentTimeMillis = System.currentTimeMillis();

        short fireTicks = 0;
        if (fireMillis > currentTimeMillis) {
          fireTicks = (short) Math.round((float) (fireMillis - currentTimeMillis) / MinecraftServer.TICK_MS);
        }

        tagHandler.setTag(FIRE_TICKS, fireTicks);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }

      tagHandler.setTag(INVULNERABLE, livingEntity.isInvulnerable());
    }

    return tagHandler.asCompound();
  }
}