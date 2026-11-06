package com.gilithrimm.affixes.affixes.interfaces;

import com.gilithrimm.affixes.affixes.Affix;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * Used to mark items that can hold affixes and transfer them, but not use them
 * (like {@link net.minecraft.item.EnchantedBookItem enchanted books} with enchants)
 */
public interface IAffixContainer {
   static Set<Affix> getStoredAffixes(ItemStack stack) {
      return ((IAffixContainer) (Object) stack).affixes$getStoredAffixes();
   }

   boolean affixes$storeAffix(Affix affix);

   Set<Affix> affixes$getStoredAffixes();

   boolean affixes$hasStoredAffixes();
}
