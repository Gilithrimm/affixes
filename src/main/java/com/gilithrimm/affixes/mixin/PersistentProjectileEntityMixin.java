package com.gilithrimm.affixes.mixin;

import com.gilithrimm.affixes.hooks.ProjectileHitHook;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/**
 * mixin for {@link PersistentProjectileEntity}, allowing {@link ProjectileHitHook projectile hooks} to work
 */
@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {
   private PersistentProjectileEntityMixin(
         EntityType<? extends ProjectileEntity> entityType,
         World world) {
      super(entityType, world);
   }
}
