package com.gilithrimm.affixes.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * thanks to this nifty piece of shit every entity now gets some Charge after getting struck by lightning!
 * yaaay...
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
   /**
    * no-op
    */
   protected EntityMixin() {}

   /**
    * used to implement in children
    *
    * @param world     server world
    * @param lightning lightning entity
    * @param ci        callback info
    */
   @Inject(method = "onStruckByLightning", at = @At("TAIL"))
   protected void lightning(ServerWorld world, LightningEntity lightning,
                            CallbackInfo ci) {}
}
