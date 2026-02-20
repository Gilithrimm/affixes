package com.gilithrimm.affixes.config;

import com.gilithrimm.affixes.affixes.Affixes;
import eu.midnightdust.lib.config.MidnightConfig;

/**
 * Every option in a MidnightConfig class has to be public and static, so we can access it from other classes.
 * The config class also has to extend MidnightConfig
 */
public class AffixConfig extends MidnightConfig {
   // categories (they can be private, right?)
   private static final String LOOT = "loot";
   private static final String DEBUG = "debug";
   private static final String AFFIX = "affix";
   private static final String CLIENT = "client";
   // Comments containing the word "spacer" will just appear as a blank line
   /**
    * Debug Mode - makes most chance-based affixes work 100% of the time
    */
   @Entry(category = DEBUG)
   public static boolean debugMode = false;
   /**
    * Chance for {@link Affixes#WEATHERED_POISON Weathered Poison} to poison the attacker
    */
   @Entry(category = AFFIX, min = 0, max = 1000, width = 1000)
   public static int wpSelfPoisonChance = 3;
   /**
    * {@link Affixes#BOUNDARY_OF_DEATH Boundary Of Death}'s damage multiplier -
    * applied after each reroll (funni number goes 30k)
    */
   @Entry(category = AFFIX, min = 1, max = 10, width = 4)
   public static float bodDamageMultiplier = 1.45f;
   /**
    * Chance for {@link Affixes#STRIKE_OF_CHARGE Strike of Charge} to strike the attacker instead of the target
    */
   @Entry(category = AFFIX, min = 0, max = 10000, width = 10000)
   public static int socSelfLightningChance = 455;
   /**
    * {@link Affixes#HUNTER_MARK Hunter's Mark} damage multiplier
    */
   @Entry(category = AFFIX, min = 1, max = 10)
   public static float hmMultiplier = 2f;
   /**
    * Shows a short(-ish) description of all the Affixes' effects
    */
   @Entry(category = CLIENT)
   public static boolean showDescriptions = true;
   /**
    * Shows additional info attached to some of the Affixes (like no. of damage dealt in previous hit, or amount of Charge stored)
    */
   @Entry(category = CLIENT)
   public static boolean showAdditionalInfo = true;

   /**
    * midnight config requires this to be public
    */
   public AffixConfig() {}
}