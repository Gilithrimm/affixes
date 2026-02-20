package com.gilithrimm.affixes.client.mixin;

import com.gilithrimm.affixes.affixes.AffixRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.gilithrimm.affixes.AffixesMod.NBT_STORE_PREFIX;

@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {
   @Shadow
   private @Nullable NbtCompound nbt;

   @Inject(method = "getTooltip", at = @At(value = "INVOKE",
                                           target = "Lnet/minecraft/item/ItemStack;appendEnchantments(Ljava/util/List;Lnet/minecraft/nbt/NbtList;)V",
                                           shift = At.Shift.AFTER))
   void displayAffixes(@Nullable PlayerEntity player, TooltipContext context,
                       CallbackInfoReturnable<List<Text>> cir,
                       @Local List<Text> list) {
      if (nbt != null) {
         if (nbt.contains("affixes")) {
            NbtList affixes = nbt.getList("affixes", NbtElement.STRING_TYPE);
            if (!affixes.isEmpty()) {
               affixes.stream()
                      .map(NbtElement::asString)
                      .map(Identifier::tryParse)
                      .map(AffixRegistry::fromRegistry)
                      .map(a -> a.tooltip(context.isAdvanced(), false))
                      .forEach(list::addAll);
               list.add(ScreenTexts.EMPTY);
            }
         } else {
            if (nbt.contains(NBT_STORE_PREFIX)) {
               NbtList nbtList = nbt.getList(NBT_STORE_PREFIX,
                     NbtElement.STRING_TYPE);
               if (!nbtList.isEmpty()) {
                  nbtList.stream()
                         .map(NbtElement::asString)
                         .map(Identifier::tryParse)
                         .map(AffixRegistry::fromRegistry)
                         .map(a -> a.tooltip(context.isAdvanced(), true))
                         .forEach(list::addAll);
                  list.add(ScreenTexts.EMPTY);
               }
            }
         }
      }
   }
}
