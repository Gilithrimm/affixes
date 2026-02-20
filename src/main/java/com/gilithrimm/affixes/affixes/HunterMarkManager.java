package com.gilithrimm.affixes.affixes;

import com.gilithrimm.affixes.affixes.interfaces.IAffixEntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;

/**
 * A collection of functions specifically built to manage {@link Affixes#HUNTER_MARK Hunter's Mark}
 * across Affixes, abstracting away all NBT- and Mixin-related stuff
 */
public final class HunterMarkManager {
   private HunterMarkManager() {}

   /**
    * Checks if the specified holder was marked by the specified entity
    *
    * @param holder entity that potentially got marked
    * @param hunter entity that potentially marked the holder
    * @return whether the specified holder was marked by the specified entity
    */
   public static boolean isMarkedBy(LivingEntity holder, LivingEntity hunter) {
      return isMarked(holder) && hunter.equals(getHunter(holder));
   }

   /**
    * Checks if the specified holder was marked
    *
    * @param holder entity that potentially got marked
    * @return whether the specified holder was marked
    */
   public static boolean isMarked(LivingEntity holder) {
      return holder != null && ((IAffixEntityData) holder).affixes$containsData(
            "markedBy");
   }

   /**
    * {@return entity that marked the specified entity (or null if not marked or none exist)}
    *
    * @param holder the entity that potentially got marked
    */
   public static Entity getHunter(LivingEntity holder) {
      if (isMarked(holder) && holder.getWorld() instanceof ServerWorld server) {
         return server.getEntity(NbtHelper.toUuid(
               ((IAffixEntityData) holder).affixes$getData("markedBy")));
      }
      return null;
   }

   /**
    * Marks the specified entity with uuid of the other specified entity
    *
    * @param holder the entity that's about to be marked
    * @param hunter the entity that marked the holder
    */
   public static void setHunter(LivingEntity holder,
                                LivingEntity hunter) {
      if (holder != null && hunter != null) {
         ((IAffixEntityData) holder).affixes$putData("markedBy",
               NbtHelper.fromUuid(hunter.getUuid()));
      }
   }
}
