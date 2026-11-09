package com.gilithrimm.affixes.items;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.AffixRegistry;
import com.gilithrimm.affixes.affixes.interfaces.IAffixContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.gilithrimm.affixes.AffixesMod.javaRandom;

/**
 * Use to get an affix to apply to your items
 */
public class ScribedPaper extends Item {

   /**
    * constructor
    *
    * @param settings settings
    * @see Item#Item(Settings)
    */
   public ScribedPaper(Settings settings) {
      super(settings);
   }

   @Override
   public TypedActionResult<ItemStack> use(World world, PlayerEntity user,
                                           Hand hand) {
      ItemStack inHand = user.getStackInHand(hand);
      ItemStack affixPaper = new ItemStack(AffixItems.AFFIX_PAPER);
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
