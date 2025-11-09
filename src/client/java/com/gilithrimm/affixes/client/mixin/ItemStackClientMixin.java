package com.gilithrimm.affixes.client.mixin;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.Affixes;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {
   @Shadow
   private @Nullable NbtCompound nbt;

   @Inject(method = "getTooltip", at = @At(value = "INVOKE",
                                           target = "Lnet/minecraft/item/ItemStack;appendEnchantments(Ljava/util/List;Lnet/minecraft/nbt/NbtList;)V",
                                           shift = At.Shift.AFTER))
   void displayAffixes(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir,
                       @Local List<Text> list) {
      if (nbt != null && nbt.contains("affixes") && !nbt.getList("affixes", 8).isEmpty()) {
         final List<Affix> affixes = nbt.getList("affixes", 8).stream()
                                        .map(nbt -> Affixes.fromRegistry(Identifier.tryParse(nbt.asString())))
                                        .toList();
         affixes.stream()
                .map(a -> a.tooltip(context.isAdvanced()))
                .forEach(list::addAll);
         list.add(ScreenTexts.EMPTY);
      }
   }
}
