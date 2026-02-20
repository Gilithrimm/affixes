package com.gilithrimm.affixes.affixes;

import com.gilithrimm.affixes.hooks.Hook;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.stream.Stream;

import static com.gilithrimm.affixes.AffixesMod.LOGGER;
import static com.gilithrimm.affixes.AffixesMod.id;
import static net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder.createSimple;

/**
 * All and everything related to registering affixes.
 * Simultaneously a way to hide away the specific mechanism (vanilla registries vs a humble set)
 */
public class AffixRegistry {
   private static final RegistryKey<Registry<Affix>> KEY = RegistryKey.ofRegistry(
         id("affixes"));
   private static final Registry<Affix> REGISTRY = createSimple(KEY)
         .attribute(RegistryAttribute.SYNCED)
         .attribute(RegistryAttribute.PERSISTED)
         .buildAndRegister();

   private AffixRegistry() {}

   //main use

   /**
    * Finds in registry the affix with given id
    *
    * @param id id of affix we search for
    * @return affix matched by given id or {@link Affix#NO_AFFIX} if none found
    */
   public static Affix fromRegistry(Identifier id) {
      return id == null || !REGISTRY.containsId(id)
             ? Affix.NO_AFFIX
             : REGISTRY.get(id);
   }

   /**
    * Exposes a mechanism-independent way to access {@link Stream stream()}
    *
    * @return a stream of the registry
    */
   public static Stream<Affix> stream() {
      return REGISTRY.stream();
   }

   /**
    * Exposes a registry-independent way to get an element via an index
    *
    * @param index index at which the element might be
    * @return element at a given index
    */
   public static Affix get(int index) {
      return REGISTRY.get(index);
   }

   /**
    * run this to force init all the static fields
    */
   public static void init() {
      LOGGER.info("Currently registered {} affixes", registrySize());
   }

   //common interface stuff

   /**
    * Exposes a mechanism-independent way to access registry size
    *
    * @return size of the registry
    */
   public static int registrySize() {
      return REGISTRY.size();
   }

   /**
    * Creates and registers the affix
    *
    * @param id   unique identifier of the affix
    * @param hook actions that should happen when this affix is present (see {@link Hook} for details)
    * @return the registered affix
    */
   public static Affix register(Identifier id, Hook hook) {
      return Registry.register(REGISTRY, id, new Affix(id, hook));
   }

}
