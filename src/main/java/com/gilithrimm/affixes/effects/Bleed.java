package com.gilithrimm.affixes.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * increases dmg taken by a %, depending on lvl
 */
public class Bleed extends StatusEffect {
   /**
    * Java requires 'constructor matching super' so it got 'constructor matching super'
    *
    * @param category this one's harmful 100%
    * @param color    a color of the particles I guess
    */
   public Bleed(StatusEffectCategory category, int color) {
      super(category, color);
   }

   /**
    * Calculates the multiplier by which this status effect increases damage taken
    *
    * @param lvl level of bleed, or amplifier + 1
    * @return bleed multiplier
    */
   public static float calculateDamage(int lvl) {
      return lvl * lvl / 50f;
   }
}
