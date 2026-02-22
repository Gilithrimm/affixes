package com.gilithrimm.affixes;

import com.gilithrimm.affixes.affixes.AffixRegistry;
import com.gilithrimm.affixes.affixes.Affixes;
import com.gilithrimm.affixes.affixes.interfaces.IAffixEntityData;
import com.gilithrimm.affixes.command.AffixCommand;
import com.gilithrimm.affixes.config.AffixConfig;
import com.gilithrimm.affixes.effects.AffixEffects;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.random.RandomGenerator;

/**
 * entrypoint for the mod
 */
public class AffixesMod implements ModInitializer {
   /**
    * singular random since we don't need more than one
    */
   public static final Random RANDOM = Random.createLocal();
   /**
    * nbt prefix for {@link IAffixEntityData AffixEntityData} shtick;
    * essentially namespace for this mod's (entity) data
    */
   public static final String NBT_PREFIX = "affixData";
   /**
    * nbt prefix for {@link AffixItems#INSCRIBED_AFFIX Inscribed Affix item} and all of everything related to that
    */
   public static final String NBT_STORE_PREFIX = "stored_affixes";
   private static final String MOD_ID = "affixes";
   /**
    * this way I can find my mod's stuff in the logs
    */
   public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
   // todo settings screen (using yacl? cloth? owo? fzzy? midnight?)
   // settings to add:
   // - additional status effects: bool for each
   // - affix on loot chance: int/float
   // -

   /**
    * fabric requires this public <br>
    * otherwise 'exception when loading entrypoints for mod "affixes"' <br>
    * so. there you have it. it's public now.
    */
   public AffixesMod() {}

   /**
    * Helper method to generate {@link Identifier ids} with mod id already in
    *
    * @param path identifier path, or a unique name to differentiate between resources within this namespace
    * @return new Identifier from the path
    * @implNote mainly exists since recommended way of initializing Identifiers changes in newer versions,
    * so this allows to keep initialization in one place<br>
    * added benefit of this is restricting access to MOD_ID to this class only
    */
   public static Identifier id(String path) {
      return new Identifier(MOD_ID, path);
   }

   /**
    * {@link #RANDOM Minecraft's random} mapped into {@link RandomGenerator Java's random}
    *
    * @return {@link #RANDOM Minecraft's random} mapped into {@link RandomGenerator Java's random}
    */
   public static java.util.Random javaRandom() {
      return new java.util.Random() {
         @Override
         public int nextInt() {
            return RANDOM.nextInt();
         }

         @Override
         public int nextInt(int bound) {
            return RANDOM.nextInt(bound);
         }

         @Override
         public long nextLong() {
            return RANDOM.nextLong();
         }

         @Override
         public boolean nextBoolean() {
            return RANDOM.nextBoolean();
         }

         @Override
         public float nextFloat() {
            return RANDOM.nextFloat();
         }

         @Override
         public double nextDouble() {
            return RANDOM.nextDouble();
         }

         @Override
         public int nextInt(int origin, int bound) {
            return RANDOM.nextBetweenExclusive(origin, bound);
         }
      };
   }

   @Override
   public void onInitialize() {
      Affixes.init();
      AffixItems.init();
      AffixCommand.init();
      AffixEffects.init();
      MidnightConfig.init(MOD_ID, AffixConfig.class);
      AffixRegistry.init();
   }
}
