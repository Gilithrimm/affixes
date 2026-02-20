package com.gilithrimm.affixes.affixes.interfaces;

import com.gilithrimm.affixes.affixes.Affix;

import java.util.List;

/**
 * Implemented in mixins - used to manipulate affixes on items
 */
public interface IAffixable {
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
    * @return list of affixes on this item
    */
   List<Affix> affixes$getAffixes();

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
