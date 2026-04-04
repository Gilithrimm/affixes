package com.gilithrimm.affixes.builders;

import com.gilithrimm.affixes.config.AffixConfig;

import static com.gilithrimm.affixes.AffixesMod.RANDOM;


/**
 * Builder made to simplify making events happen with certain probability.
 */
public class ChanceBuilder {
   private final int max;
   private Runnable success;
   private Runnable failure;
   private boolean debug = false;

   /**
    * Creates this builder with max value possible to roll
    *
    * @param max max value rolled
    */
   ChanceBuilder(int max) {
      this.max = max;
   }

   /**
    * Marks this event as guaranteed in {@link AffixConfig#debugMode debug mode}
    *
    * @return this builder
    */
   public ChanceBuilder debug() {
      debug = true;
      return this;
   }

   /**
    * Marks this event as not guaranteed in {@link AffixConfig#debugMode debug mode}
    *
    * @return this builder
    */
   public ChanceBuilder noDebug() {
      debug = false;
      return this;
   }

   /**
    * Sets an action to happen when chance succeeds
    *
    * @param l action that should happen if chance succeeds
    * @return this builder
    * @apiNote either this or {@link ChanceBuilder#failure} must be not null
    */
   public ChanceBuilder lucky(Runnable l) {
      success = l;
      return this;
   }

   /**
    * Sets an action to happen when chance fails
    *
    * @param u action that should happen if chance fails
    * @return this builder
    * @apiNote either this or {@link ChanceBuilder#success} must be not null
    */
   public ChanceBuilder unlucky(Runnable u) {
      failure = u;
      return this;
   }

   /**
    * Builds and executes the chance-based code with the provided max required to pass
    *
    * @param chance D&amp;D's DC, essentially
    * @implNote unlike D&amp;D, success is defined as getting a number <b>less than or equal to</b> the provided chance.
    * This is done mostly for the sake of simplicity when working with percentage-based probabilities.
    */
   public void apply(int chance) {
      if (success == null && failure == null) {
         throw new IllegalArgumentException("at least one action should be defined");
      }
      int result = RANDOM.nextBetween(1, max);
      if (result <= chance || (debug && AffixConfig.debugMode)) {
         if (success != null) {
            success.run();
         }
      } else {
         if (failure != null) {
            failure.run();
         }
      }
   }
}
