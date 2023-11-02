package com.github.ynverxe.entitydecoder.vanilla;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.tag.Tag;
import org.jglrxavpok.hephaistos.nbt.*;

import java.util.Arrays;
import java.util.UUID;

import static net.minestom.server.tag.Tag.*;

/**
 * During a {@link VanillaEntityConfigurator#configure(Entity, NBTCompound)} the
 * tags that Minestom doesn't provide a way to handle this his usage like {@link #FALL_DISTANCE}
 * are stored into the {@link Entity#tagHandler()}
 * in a new nbt structure named "vanilla-unused".
 * However, during a {@link VanillaEntitySerializer#serialize(Entity)} all unhandled vanilla tags
 * will be taken from the entity and written into the resultant {@link NBTCompound} in the
 * Vanilla Entity Format.
 * See <a href="https://minecraft.fandom.com/wiki/Entity_format">Vanilla Entity Format</a>
 * <p>
 * List of unhandled tags:
 * <ul>
 *   <li>{@link #FALL_DISTANCE} always 0 because Minestom doesn't provides an API to get the fall distance</li>
 *  <li>{@link #ON_GROUND} always {@link Entity#isOnGround()} during serialization</li>
 *  <li>{@link #PORTAL_COOLDOWN} always 300 ticks by default during serialization</li>
 *  <li>{@link #TAGS} always an empty list by default during serialization</li>
 * </ul>
 */
public interface EntityVanillaConstants {

  // default values
  // UUID, ENTITY_ID and ON_GROUND values depends on the entity object values
  short DEF_AIR = 300;
  Component DEF_CUSTOM_NAME = Component.empty();
  boolean DEF_CUSTOM_NAME_VISIBLE = true;
  float DEF_FALL_DISTANCE = 0;
  short DEF_FIRE_TICKS = -20;
  boolean DEF_GLOWING = false;
  boolean DEF_HAS_VISUAL_FIRE = false;
  boolean DEF_INVULNERABLE = false;
  boolean DEF_SILENT = false;
  int DEF_PORTAL_COOLDOWN = 300;
  int DEF_TICKS_FROZEN = 0;

  static NBTList<NBTString> defTags() {
    return new NBTList<>(NBTType.TAG_String);
  }

  static NBTList<NBTDouble> defMotion() {
    return new NBTList<>(NBTType.TAG_Double, Arrays.asList(
      new NBTDouble(0),
      new NBTDouble(0),
      new NBTDouble(0)
    ));
  }

  static NBTList<NBTDouble> defPos() {
    return new NBTList<>(NBTType.TAG_Double, Arrays.asList(
      new NBTDouble(0),
      new NBTDouble(0),
      new NBTDouble(0)
    ));
  }

  static NBTList<NBTFloat> defRotation() {
    return new NBTList<>(NBTType.TAG_Float, Arrays.asList(
      new NBTFloat(0),
      new NBTFloat(0)
    ));
  }

  static NBTList<NBTCompound> defPassengers() {
    return new NBTList<>(NBTType.TAG_Compound);
  }

  static NBTList<NBTString> defScoreboardTags() {
    return new NBTList<>(NBTType.TAG_String);
  }

  Tag<Short> AIR = Short("Air").defaultValue(DEF_AIR);
  Tag<Component> CUSTOM_NAME = Component("CustomName").defaultValue(DEF_CUSTOM_NAME);
  Tag<Boolean> CUSTOM_NAME_VISIBLE = Boolean("CustomNameVisible").defaultValue(DEF_CUSTOM_NAME_VISIBLE);
  Tag<Float> FALL_DISTANCE = Float("FallDistance").defaultValue(DEF_FALL_DISTANCE);
  Tag<Short> FIRE_TICKS = Short("Fire").defaultValue(DEF_FIRE_TICKS);
  Tag<Boolean> GLOWING = Boolean("Glowing").defaultValue(DEF_GLOWING);
  Tag<Boolean> HAS_VISUAL_FIRE = Boolean("HasVisualFire").defaultValue(DEF_HAS_VISUAL_FIRE);
  Tag<String> ENTITY_ID = String("id");
  Tag<Boolean> INVULNERABLE = Boolean("Invulnerable").defaultValue(DEF_INVULNERABLE);
  Tag<NBT> MOTION = NBT("Motion").defaultValue(EntityVanillaConstants::defMotion);
  Tag<Boolean> NO_GRAVITY = Boolean("NoGravity");
  Tag<Boolean> ON_GROUND = Boolean("OnGround");
  Tag<NBT> PASSENGERS = NBT("Passengers").defaultValue(EntityVanillaConstants::defPassengers);
  Tag<Integer> PORTAL_COOLDOWN = Integer("PortalCooldown").defaultValue(DEF_PORTAL_COOLDOWN);
  Tag<NBT> POS = NBT("Pos").defaultValue(EntityVanillaConstants::defPos);
  Tag<NBT> ROTATION = NBT("Rotation").defaultValue(EntityVanillaConstants::defRotation);
  Tag<Boolean> SILENT = Boolean("Silent").defaultValue(DEF_SILENT);
  Tag<NBT> TAGS = NBT("Tags").defaultValue(EntityVanillaConstants::defScoreboardTags);
  Tag<Integer> TICKS_FROZEN = Integer("TicksFrozen").defaultValue(DEF_TICKS_FROZEN);
  Tag<UUID> UUID = UUID("UUID");
}