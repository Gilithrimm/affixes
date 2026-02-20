package com.gilithrimm.affixes.affixes;

import com.gilithrimm.affixes.affixes.interfaces.IAffixEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtInt;

import static java.lang.Math.max;

/**
 * A collection of functions specifically built to manage Charge across Affixes,
 * abstracting away all NBT-related stuff
 */
//todo Charge docs
//todo Overcharge in one location
public final class ChargeManager {

   private ChargeManager() {}

   /**
    * Changes the amount of Charge in the entity by given amount
    *
    * @param holder the entity that holds Charge
    * @param value  how much Charge to add to the holder
    * @apiNote Charge will always be &ge; 0
    */
   public static void addCharge(LivingEntity holder, int value) {
      setCharge(holder, max(getCharge(holder) + value, 0));
   }

   /**
    * Sets the amount of Charge in the entity to a specified value (or 0 if {@code value < 0})
    *
    * @param holder the entity that holds Charge
    * @param value  the amount of Charge to set to
    * @apiNote Charge will always be &ge; 0
    */
   public static void setCharge(LivingEntity holder, int value) {
      ((IAffixEntityData) holder).affixes$putData("charge",
            NbtInt.of(max(value, 0)));
   }

   /**
    * Grabs the amount of Charge in the entity
    *
    * @param holder the entity that holds Charge
    * @return the amount of Charge in the entity
    * @apiNote Charge will always be &ge; 0
    */
   public static int getCharge(LivingEntity holder) {
      IAffixEntityData data = (IAffixEntityData) holder;
      if (data.affixes$containsData("charge")) {
         return max(((NbtInt) data.affixes$getData(
               "charge")).intValue(), 0);
      }
      return 0;
   }
}
