package com.github.ynverxe.entitydecoder;

import org.jglrxavpok.hephaistos.nbt.*;

import java.util.Arrays;

public interface LivingEntityVanillaConstants {

  // default values
  // UUID, ENTITY_ID and ON_GROUND values depends on the entity object values
  short DEF_AIR = 300;
  String DEF_CUSTOM_NAME = "";
  boolean DEF_CUSTOM_NAME_VISIBLE = true;
  int DEF_FALL_DISTANCE = 0;
  int DEF_FIRE_TICKS = 20;
  boolean DEF_GLOWING = false;
  boolean DEF_HAS_VISUAL_FIRE = false;
  boolean DEF_INVULNERABLE = false;
  boolean DEF_SILENT = false;
  int DEF_PORTAL_COOLDOWN = 300;
  int DEF_TICKS_FROZEN = 0;

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
}