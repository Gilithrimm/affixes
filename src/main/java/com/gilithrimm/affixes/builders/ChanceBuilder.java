package com.gilithrimm.affixes.builders;

import com.gilithrimm.affixes.config.AffixConfig;

import static com.gilithrimm.affixes.AffixesMod.RANDOM;


public class ChanceBuilder {
   private final int max;
   private Runnable lucky;
   private Runnable unlucky;
   private boolean debug = false;

   ChanceBuilder(int max) {
      this.max = max;
   }

   public ChanceBuilder debug() {
      debug = true;
      return this;
   }

   public ChanceBuilder noDebug() {
      debug = false;
      return this;
   }

   public ChanceBuilder lucky(Runnable l) {
      lucky = l;
      return this;
   }

   public ChanceBuilder unlucky(Runnable u) {
      unlucky = u;
      return this;
   }

   public void apply(int chance) {
      if (lucky == null && unlucky == null) {
         throw new IllegalArgumentException("at least one Runnable should be defined");
      }
      int result = RANDOM.nextBetween(1, max);
      if (result <= chance || (debug && AffixConfig.debugMode)) {
         if (lucky != null) {
            lucky.run();
         }
      } else {
         if (unlucky != null) {
            unlucky.run();
         }
      }
   }
}
