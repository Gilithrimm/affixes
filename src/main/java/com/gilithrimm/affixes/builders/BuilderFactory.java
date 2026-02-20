package com.gilithrimm.affixes.builders;

import net.minecraft.entity.effect.StatusEffect;

public class BuilderFactory {
   public static ChanceBuilder onChance(int max) {
      return new ChanceBuilder(max);
   }

   public static StatusEffectBuilder statusEffect(StatusEffect type) {
      return new StatusEffectBuilder(type)
            .icon()
            .particles()
            .ambient();
   }

   public static StatusEffectBuilder hiddenStatusEffect(StatusEffect type) {
      return new StatusEffectBuilder(type)
            .noAmbient()
            .noIcon()
            .noParticles();
   }
}
