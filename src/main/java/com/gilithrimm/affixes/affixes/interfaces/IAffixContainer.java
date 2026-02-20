package com.gilithrimm.affixes.affixes.interfaces;

import com.gilithrimm.affixes.affixes.Affix;

import java.util.Set;

/**
 * Used to mark items that can hold affixes and transfer them, but not use them
 * (like {@link net.minecraft.item.EnchantedBookItem enchanted books})
 */
public interface IAffixContainer {

   boolean affixes$storeAffix(Affix affix);

   Set<Affix> affixes$getStoredAffixes();

   boolean affixes$hasStoredAffixes();
}
