package com.gilithrimm.affixes;

import com.gilithrimm.affixes.affixes.Affix;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * functionality related to tags is/will be here
 */
public class AffixTag {
   /**
    * Registry key for affixes. tbf I don't know either what these words mean lol
    */
   public static final RegistryKey<Registry<Affix>> AFFIX_REGISTRY_KEY = RegistryKey.ofRegistry(
         AffixesMod.id("affix"));

   private AffixTag() {}
}
