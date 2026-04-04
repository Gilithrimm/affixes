package com.gilithrimm.affixes.builders;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;


/**
 * Builder made to simplify creating {@link StatusEffectInstance status effect instances}.
 */
public class StatusEffectBuilder {
   private final StatusEffect type;
   private int duration = 0;
   private int amplifier = 0;
   private boolean ambient = false;
   private boolean showParticles = false;
   private boolean showIcon = false;

   /**
    * Creates this builder with a set status effect type
    *
    * @param type a type of this status effect
    */
   StatusEffectBuilder(StatusEffect type) {
      this.type = type;
   }

   /**
    * Sets the duration for this status effect
    *
    * @param duration duration of this status effect (in ticks)
    * @return this builder
    */
   public StatusEffectBuilder duration(int duration) {
      this.duration = duration;
      return this;
   }

   /**
    * Sets the amplifier for this status effect
    *
    * @param amplifier the amplifier of this effect (equivalent to status effect's level - 1)
    * @return this builder
    */
   public StatusEffectBuilder amplifier(int amplifier) {
      this.amplifier = amplifier;
      return this;
   }

   /**
    * Marks this status effect as with ambient effects
    *
    * @return this builder
    */
   public StatusEffectBuilder ambient() {
      ambient = true;
      return this;
   }

   /**
    * Marks this status effect as without ambient effects
    *
    * @return this builder
    */
   public StatusEffectBuilder noAmbient() {
      ambient = false;
      return this;
   }

   /**
    * Makes this status effect create particles
    *
    * @return this builder
    */
   public StatusEffectBuilder particles() {
      showParticles = true;
      return this;
   }

   /**
    * Makes this status effect not create particles
    *
    * @return this builder
    */
   public StatusEffectBuilder noParticles() {
      showParticles = false;
      return this;
   }

   /**
    * Makes this status effect's icon visible
    *
    * @return this builder
    */
   public StatusEffectBuilder icon() {
      showIcon = true;
      return this;
   }

   /**
    * Makes this status effect's icon invisible
    *
    * @return this builder
    */
   public StatusEffectBuilder noIcon() {
      showIcon = false;
      return this;
   }

   /**
    * Builds the status effect instance with all provided arguments and applies it to target with no source
    *
    * @param target the entity to which the status effect is added
    */
   public void apply(LivingEntity target) {
      apply(target, null);
   }

   /**
    * Builds the status effect instance with all provided arguments and applies it to target
    *
    * @param target the entity to which the status effect is added
    * @param source the source entity or null
    */
   public void apply(LivingEntity target, Entity source) {
      target.addStatusEffect(build(), source);
   }

   /**
    * Builds the status effect instance with all provided arguments
    *
    * @return the built instance
    */
   public StatusEffectInstance build() {
      return new StatusEffectInstance(type, duration, amplifier, ambient,
            showParticles, showIcon);
   }
}

