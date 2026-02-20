package com.gilithrimm.affixes.mixin;

import com.gilithrimm.affixes.AffixItems;
import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.AffixRegistry;
import com.gilithrimm.affixes.affixes.interfaces.IAffixContainer;
import com.gilithrimm.affixes.affixes.interfaces.IAffixable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static com.gilithrimm.affixes.AffixesMod.NBT_PREFIX;
import static com.gilithrimm.affixes.AffixesMod.RANDOM;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
   @Shadow
   @Final
   private static Logger LOGGER;

   public AnvilScreenHandlerMixin(
         @Nullable ScreenHandlerType<?> type,
         int syncId, PlayerInventory playerInventory,
         ScreenHandlerContext context) {
      super(type, syncId, playerInventory, context);
   }

   @Inject(method = "updateResult", at = @At(value = "INVOKE",
                                             target = "Lnet/minecraft/enchantment/EnchantmentHelper;get(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;",
                                             ordinal = 0,
                                             shift = At.Shift.AFTER))
   private void action(CallbackInfo ci,
                       @Local(ordinal = 0) ItemStack slot0,
                       @Local(ordinal = 1) ItemStack copied,
                       @Local(ordinal = 2) ItemStack slot1,
                       @Local(ordinal = 0) LocalIntRef cost) {
      Collection<Affix> affixes;


      // stored affixes
      if (((IAffixContainer) (Object) slot1).affixes$hasStoredAffixes()) {
         affixes = ((IAffixContainer) (Object) slot1).affixes$getStoredAffixes();
         LOGGER.info("affixes stored: {}", affixes);


         // affixes on 2nd item
         if (slot1.isOf(copied.getItem())) {
            affixes = ((IAffixable) (Object) slot1).affixes$getAffixes();
            LOGGER.info("affixes on slot1: {}", affixes);
         }

         LOGGER.info("affixes on copied b4: {}",
               (((IAffixable) (Object) copied)).affixes$getAffixes());
         // add affixes
         affixes.stream()
                .filter(a -> !((IAffixable) (Object) slot0).affixes$containsAffix(a))
                .forEach(affix -> ((IAffixable) (Object) copied).affixes$addAffix(affix));
         LOGGER.info("affixes on copied after: {}",
               (((IAffixable) (Object) copied)).affixes$getAffixes());

         LOGGER.info("cost b4: {}", cost.get());
         // increase cost
         cost.set((int) (cost.get() + affixes.stream()
                                             .filter(
                                                   a -> !((IAffixable) (Object) slot1).affixes$containsAffix(
                                                         a))
                                             .count()));
         LOGGER.info("cost after: {}", cost.get());

         // copy affixData
         if (slot1.hasNbt() && slot1.getNbt().contains(NBT_PREFIX)) {
            NbtCompound slot1Nbt = slot1.getSubNbt(NBT_PREFIX);
            slot1Nbt.getKeys().forEach(key -> {
               NbtCompound copiedNbt = copied.getOrCreateSubNbt(NBT_PREFIX);
               if (!copiedNbt.contains(key)) {
                  copiedNbt.put(key, slot1Nbt.get(key));
               }
            });
         }


      }
      // reroll
      if (slot1.isOf(AffixItems.AFFIX_REROLL)) {
         reroll(slot0, copied);
      }
   }

   @Unique
   private static void reroll(ItemStack s0, ItemStack cp) {
      // if (s0.hasNbt() && s0.getNbt().contains("affixes")) {
      //    var current = s0.getNbt().getList("affixes", NbtElement.STRING_TYPE);
      //
      // }
      var slot0 = (IAffixable) (Object) s0;
      var copied = (IAffixable) (Object) cp;
      List<Affix> rerolled = List.copyOf(slot0.affixes$getAffixes());
      LOGGER.info(rerolled.toString());
      rerolled.forEach(copied::affixes$removeAffix);
      IntStream.range(0, rerolled.size())
               .map(i -> RANDOM.nextInt(AffixRegistry.registrySize()))
               .mapToObj(AffixRegistry::get)
               .forEach(affix -> {
                  LOGGER.info(affix.toString());
                  if (copied.affixes$addAffix(affix)) {
                     LOGGER.info("added!");
                  }
               });
   }

   @ModifyVariable(method = "updateResult", at = @At("STORE"), ordinal = 0)
   private boolean check(boolean isBookNotEmpty, @Local(ordinal = 2) ItemStack slot1,
                         @Local(ordinal = 1) ItemStack copy) {
      return isBookNotEmpty
             || slot1.isOf(AffixItems.AFFIX_REROLL)
             || ((((IAffixContainer) ((Object) slot1)).affixes$hasStoredAffixes())
                 && ((IAffixContainer) ((Object) slot1)).affixes$getStoredAffixes()
                                                        .stream()
                                                        .allMatch(
                                                              ((IAffixable) ((Object) copy))::affixes$containsAffix));
   }
}
