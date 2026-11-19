package com.gilithrimm.affixes.affixes.interfaces;

import com.gilithrimm.affixes.affixes.Affix;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * Used to mark items that can hold affixes and transfer them, but not use them
 * (like {@link net.minecraft.item.EnchantedBookItem enchanted books} with enchants)
 */
public interface IAffixContainer {
   /**
    * static wrapper for {@link #affixes$getStoredAffixes()} because double-cast is unwieldy
    * (&amp; so is the method name)
    *
    * @param stack {@link ItemStack} that stores affixes
    * @return a set of affixes stored on the given item, or empty set if none
    */
   static Set<Affix> getStoredAffixes(ItemStack stack) {
      return ((IAffixContainer) (Object) stack).affixes$getStoredAffixes();
   }

   /**
    * tries adding given affix to stored affixes
    *
    * @param affix affix to store
    * @return if the affix was actually stored
    */
   boolean affixes$storeAffix(Affix affix);

   /**
    * gives you affixes currently stored on this item
    *
    * @return a set of affixes stored on this item, or empty set if none
    */
   Set<Affix> affixes$getStoredAffixes();

   /**
    * checks if this item stores any affixes
    *
    * @return whether this item stores any affixes or not
    */
   boolean affixes$hasStoredAffixes();
}
