package com.gilithrimm.affixes.affixes.interfaces;

import com.gilithrimm.affixes.affixes.Affix;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * Implemented in mixins - used to manipulate affixes on items
 */
public interface IAffixable {
   /**
    * static wrapper for {@link IAffixable#affixes$getAffixes()} because double-cast is unwieldy
    * (&amp; so is the method name)
    *
    * @param stack {@link ItemStack} that can have affixes on itself
    * @return a set of affixes on the item
    */
   static Set<Affix> getAffixesFromStack(ItemStack stack) {
      return ((IAffixable) (Object) stack).affixes$getAffixes();//might change to null check n stuff
   }

   /**
    * static wrapper for {@link #affixes$addAffix(Affix)} because double-cast is unwieldy
    * (&amp; so is the method name)
    *
    * @param stack the {@link ItemStack} we add the affix to
    * @param affix affix to add
    * @return if the affix was actually added
    */
   static boolean addAffix(ItemStack stack, Affix affix) {
      return ((IAffixable) (Object) stack).affixes$addAffix(affix);
   }


   /**
    * tries adding this affix to this item
    *
    * @param affix affix to add
    * @return if the affix was actually added
    */
   boolean affixes$addAffix(Affix affix);

   /**
    * gives you affixes currently on this item
    *
    * @return set of affixes on this item (empty set if none)
    */
   Set<Affix> affixes$getAffixes();

   /**
    * removes affix from this item. or at least it tries to do that.
    *
    * @param affix affix to remove
    * @return whether it succeeded in removing the provided affix or not
    */
   boolean affixes$removeAffix(Affix affix);

   /**
    * checks if this item contains this affix
    *
    * @param affix affix to check
    * @return if this item contains this affix
    */
   boolean affixes$containsAffix(Affix affix);
}
