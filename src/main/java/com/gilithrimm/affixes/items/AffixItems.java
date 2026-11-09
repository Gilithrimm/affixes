package com.gilithrimm.affixes.items;

import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

import static com.gilithrimm.affixes.AffixesMod.id;

/**
 * grouping all item init &amp; registering related stuff in one separate place
 */
public class AffixItems {
   // todo items
   /**
    * Combine this with an affixed item in the anvil to reroll all affixes on the item, consuming this in the process
    */
   public static final Item AFFIX_REROLL = register("affix_reroll",
         new Item(new Settings().rarity(Rarity.RARE)));
   /**
    * Right-click to reveal what affix is underneath!
    */
   //todo proper revealing via crafting/world interaction
   public static final Item SCRIBED_PAPER = register("scribed_paper",
         new ScribedPaper(new Settings().rarity(Rarity.UNCOMMON)));
   /**
    * Combine this with an item in a smithing table to add an affix onto the item, consuming this item in the process
    */
   // todo split into several items depending on where they're applicable
   public static final Item AFFIX_PAPER = register(//todo clay tablet not paper?
         "inscribed_affix",// todo change var name w/o breaking everything
         new Item(new Settings().rarity(Rarity.RARE)));

   private AffixItems() {}

   private static <T extends Item> T register(String path, T item) {
      return Registry.register(Registries.ITEM, id(path), item);
   }

   /**
    * calling this function forces all the static fields to init which is the only reason it exists at all
    */
   public static void forceLoad() {}

}
