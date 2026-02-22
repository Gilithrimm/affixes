package com.gilithrimm.affixes;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.AffixRegistry;
import com.gilithrimm.affixes.affixes.interfaces.IAffixContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.gilithrimm.affixes.AffixesMod.id;
import static com.gilithrimm.affixes.AffixesMod.javaRandom;

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
   public static final Item SCRIBED_PAPER = register("scribed_paper",
         new ScribedPaper(new Settings().rarity(Rarity.UNCOMMON)));
   /**
    * Combine this with an item in an anvil to add an affix onto the item, consuming this item in the process
    */
   public static final Item INSCRIBED_AFFIX = register("inscribed_affix",
         new Item(new Settings().rarity(Rarity.RARE)));

   private AffixItems() {}

   private static <T extends Item> T register(String path, T item) {
      return Registry.register(Registries.ITEM, id(path), item);
   }

   /**
    * calling this function forces all the static fields to init which is the only reason it exists at all
    */
   public static void init() {}

   private static class ScribedPaper extends Item {
      public ScribedPaper(Settings settings) {
         super(settings);
      }

      @Override
      public TypedActionResult<ItemStack> use(World world, PlayerEntity user,
                                              Hand hand) {
         ItemStack inHand = user.getStackInHand(hand);
         ItemStack affixPaper = new ItemStack(INSCRIBED_AFFIX);
         var affixList = AffixRegistry.stream()
                                      .filter(a -> !a.hook().isCreativeOnly())
                                      .collect(Collectors.toCollection(
                                            ArrayList::new));
         // Collections.shuffle(affixList); // there's no way to convert between java Random and RandomGenerator :(
         Collections.shuffle(affixList, javaRandom());// hacky as fuck but whatever
         Affix random = affixList.get(0);
         ((IAffixContainer) (Object) affixPaper).affixes$storeAffix(random);
         user.giveItemStack(affixPaper);
         inHand.decrement(1);
         return TypedActionResult.consume(inHand);
      }
   }

}
