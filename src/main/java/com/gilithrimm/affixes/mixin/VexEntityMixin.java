package com.gilithrimm.affixes.mixin;

import com.gilithrimm.affixes.affixes.interfaces.IOwner;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VexEntity.class)
public abstract class VexEntityMixin extends Entity implements IOwner {
   @Unique
   @Nullable
   Entity betterOwner;

   public VexEntityMixin(EntityType<?> type,
                         World world) {
      super(type, world);
   }

   @ModifyReturnValue(method = "getOwner()Lnet/minecraft/entity/Entity;",
                      at = @At("RETURN"))
   Entity owner_get(Entity original) {
      return betterOwner;
   }

   @Inject(method = "setOwner", at = @At(value = "FIELD",
                                         target = "Lnet/minecraft/entity/mob/VexEntity;owner:Lnet/minecraft/entity/mob/MobEntity;",
                                         opcode = Opcodes.PUTFIELD))
   void owner_set(MobEntity owner, CallbackInfo ci) {
      betterOwner = owner;
   }

   @Override
   public void setOwner(Entity owner) {
      betterOwner = owner;
   }
}
/*Hi, I'm trying to make an enchantment that summons vexes whenever an entity takes damage
(with intent that the vexes help the hurt entity). What would be the simplest way to implement this?*/