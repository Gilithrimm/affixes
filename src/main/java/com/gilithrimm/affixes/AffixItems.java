package com.gilithrimm.affixes;

import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static com.gilithrimm.affixes.AffixesMod.MOD_ID;

public class AffixItems {
   public static final Item AFFIX_REROLL = register("affix_reroll", new Item(new Settings().rarity(Rarity.RARE)));

   public static <T extends Item> T register(String path, T item) {
      return Registry.register(Registries.ITEM, new Identifier(MOD_ID, path), item);
   }

   public static void init() {}
}
