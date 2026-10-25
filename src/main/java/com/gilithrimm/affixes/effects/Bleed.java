package com.gilithrimm.affixes.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * increases dmg taken depending on a lvl in a non-linear fashion (lvl<sup>2</sup>/50)
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

//todo rework?? it feels extremely insignificant rn
// unless you're running Puncturing Wounds in which case it turns from insignificant to unbalanced