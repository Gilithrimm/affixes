package com.gilithrimm.affixes.builders;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;


public class StatusEffectBuilder {
   private final StatusEffect type;
   private int duration = 0;
   private int amplifier = 0;
   private boolean ambient = false;
   private boolean showParticles = false;
   private boolean showIcon = false;

   public StatusEffectBuilder(StatusEffect type) {
      this.type = type;
   }

   public StatusEffectBuilder duration(int duration) {
      this.duration = duration;
      return this;
   }

   public StatusEffectBuilder amplifier(int amplifier) {
      this.amplifier = amplifier;
      return this;
   }

   public StatusEffectBuilder ambient() {
      ambient = true;
      return this;
   }

   public StatusEffectBuilder noAmbient() {
      ambient = false;
      return this;
   }

   public StatusEffectBuilder particles() {
      showParticles = true;
      return this;
   }

   public StatusEffectBuilder noParticles() {
      showParticles = false;
      return this;
   }

   public StatusEffectBuilder icon() {
      showIcon = true;
      return this;
   }

   public StatusEffectBuilder noIcon() {
      showIcon = false;
      return this;
   }

   public void apply(LivingEntity target) {
      apply(target, null);
   }

   public void apply(LivingEntity target, Entity source) {
      target.addStatusEffect(build(), source);
   }

   public StatusEffectInstance build() {
      return new StatusEffectInstance(type, duration, amplifier, ambient,
            showParticles, showIcon);
   }
}

