package com.gilithrimm.affixes.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingTableScreenMixin extends ForgingScreenHandler {
   public SmithingTableScreenMixin(
         @Nullable ScreenHandlerType<?> type,
         int syncId, PlayerInventory playerInventory,
         ScreenHandlerContext context) {
      super(type, syncId, playerInventory, context);
   }
}
