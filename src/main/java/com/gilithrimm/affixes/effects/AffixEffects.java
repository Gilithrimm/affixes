package com.gilithrimm.affixes.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static com.gilithrimm.affixes.AffixesMod.id;

/**
 * grouping all effect init &amp; registering related stuff in one separate place
 */
public class AffixEffects {
   /**
    * {@link Vulnerability} registered
    */
   public static final Vulnerability VULNERABILITY = register("vulnerable",
         new Vulnerability(StatusEffectCategory.HARMFUL, 0x11201e));
   /**
    * {@link Bleed} registered
    */
   public static final Bleed BLEED = register("bleed",
         new Bleed(StatusEffectCategory.HARMFUL, 0xff111b));

   private AffixEffects() {}

   private static <T extends StatusEffect> T register(String path, T effect) {
      return Registry.register(Registries.STATUS_EFFECT, id(path), effect);
   }

   /**
    * calling this function forces all the static fields to init which is the only reason it exists at all
    */
   public static void init() {}
}
