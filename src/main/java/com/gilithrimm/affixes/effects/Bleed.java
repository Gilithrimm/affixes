package com.gilithrimm.affixes.effects;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import static com.gilithrimm.affixes.AffixesMod.id;

/**
 * deals extra dmg based on its lvl every time the character afflicted takes dmg
 */
public class Bleed extends StatusEffect {
   public static final RegistryKey<DamageType> BLEED_DAMAGE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("bleed_damage"));

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
    * Calculates the damage dealt by Bleed based on the lvl
    *
    * @param lvl level of bleed, or amplifier + 1
    * @return bleed damage
    */
   public static float calculateDamage(int lvl) {
      //...genuinely how do i make this worthwhile & not extra op
      return lvl / 3f;
      //...I keep forgetting that I should balance this game around lvl<=5
   }

}

//todo rework?? it feels extremely insignificant rn
// unless you're running Puncturing Wounds in which case it turns from insignificant to unbalanced