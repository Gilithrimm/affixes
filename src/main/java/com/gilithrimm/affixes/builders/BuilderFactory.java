package com.gilithrimm.affixes.builders;

import net.minecraft.entity.effect.StatusEffect;

/**
 * a class grouping factory methods for builders
 */
public class BuilderFactory {
   private BuilderFactory() {/*no-instance*/}

   /**
    * Creates a new {@link ChanceBuilder} with a defined roll range
    *
    * @param max max value of the roll
    * @return a new {@link ChanceBuilder}
    */
   public static ChanceBuilder onChance(int max) {
      return new ChanceBuilder(max);
   }

   /**
    * Creates a new {@link StatusEffectBuilder} with all visibility options on
    *
    * @param type a type of status effect
    * @return a new {@link StatusEffectBuilder} with icon, particles and ambience
    */
   public static StatusEffectBuilder statusEffect(StatusEffect type) {
      return new StatusEffectBuilder(type)
            .icon()
            .particles()
            .ambient();
   }

   /**
    * Creates a new {@link StatusEffectBuilder} with all visibility options off
    *
    * @param type a type of status effect
    * @return a new {@link StatusEffectBuilder} with no icon, ambience nor particles
    */
   public static StatusEffectBuilder hiddenStatusEffect(StatusEffect type) {
      return new StatusEffectBuilder(type)
            .noAmbient()
            .noIcon()
            .noParticles();
   }
}
