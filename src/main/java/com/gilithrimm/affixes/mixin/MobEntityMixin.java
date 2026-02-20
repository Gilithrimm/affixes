package com.gilithrimm.affixes.mixin;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.interfaces.IAffixable;
import com.gilithrimm.affixes.hooks.AttackHook;
import com.gilithrimm.affixes.hooks.AttackHooks;
import com.gilithrimm.affixes.hooks.Hook;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.gilithrimm.affixes.hooks.AttackHook.AttackHookContext;

/**
 * mixin for {@link MobEntity}, allowing {@link AttackHook attack hooks} to work
 */
@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
   @Unique
   private final List<AttackHook> attackHooks = new ArrayList<>();
   @Unique
   private AttackHookContext context;

   private MobEntityMixin(EntityType<? extends LivingEntity> entityType,
                          World world) {
      super(entityType, world);
   }

   @Inject(method = "tryAttack", at = @At("HEAD"))
   private void setup(Entity target, CallbackInfoReturnable<Boolean> cir) {
      context = new AttackHookContext(this, target);
      attackHooks.clear();
      attackHooks.addAll(((IAffixable) (Object) getStackInHand(getActiveHand()))
            .affixes$getAffixes()
            .stream()
            .map(Affix::hook)
            .filter(AttackHook.class::isInstance)
            .map(AttackHook.class::cast)
            .toList());
      attackHooks.forEach(Hook::setup);
   }

   @Inject(method = "tryAttack", at = @At(value = "INVOKE",
                                          target = "Lnet/minecraft/entity/mob/MobEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D",
                                          ordinal = 1))
   private void attribute(Entity target, CallbackInfoReturnable<Boolean> cir,
                          @Local(ordinal = 0) LocalFloatRef damage) {
      damage.set(attackHooks.stream()
                            .map((Function<AttackHook, AttackHooks.PostAttribute>) attackHook -> attackHook::postAttribute)
                            .reduce(AttackHooks.PostAttribute::andThen)
                            .orElse(AttackHooks.PostAttribute.identity())
                            .postAttribute(damage.get(), context));
   }

   @Inject(method = "tryAttack", at = @At(value = "INVOKE",
                                          target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F",
                                          shift = At.Shift.AFTER))
   private void enchantments(Entity target, CallbackInfoReturnable<Boolean> cir,
                             @Local(ordinal = 0) LocalFloatRef damage) {
      damage.set(attackHooks.stream()
                            .map((Function<AttackHook, AttackHooks.PostEnchantments>) attackHook -> attackHook::postEnchantments)
                            .reduce(AttackHooks.PostEnchantments::andThen)
                            .orElse(AttackHooks.PostEnchantments.identity())
                            .postEnchantments(damage.get(), context));
   }

   @Inject(method = "tryAttack",
           at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setOnFireFor(I)V",
                    ordinal = 0))
   private void fireAspect(Entity target, CallbackInfoReturnable<Boolean> cir,
                           @Local(ordinal = 0) LocalFloatRef damage) {
      damage.set(attackHooks.stream()
                            .map((Function<AttackHook, AttackHooks.PostFireAspect>) attackHook -> attackHook::postFireAspect)
                            .reduce(AttackHooks.PostFireAspect::andThen)
                            .orElse(AttackHooks.PostFireAspect.identity())
                            .postFireAspect(damage.get(), context));
   }

   @Inject(method = "tryAttack", at = @At(value = "RETURN"))
   private void hit(Entity target, CallbackInfoReturnable<Boolean> cir,
                    @Local(ordinal = 0) float damage,
                    @Local boolean isHit) {
      attackHooks.stream()
                 .map(h -> (AttackHooks.PostHit) h::postHit)
                 .reduce(AttackHooks.PostHit::andThen)
                 .orElse(AttackHooks.PostHit.identity())
                 .postHit(isHit, damage, context);
   }

   @Inject(method = "tryAttack", at = @At(value = "INVOKE",
                                          target = "Lnet/minecraft/entity/mob/MobEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
   private void knockback(Entity target, CallbackInfoReturnable<Boolean> cir,
                          @Local(ordinal = 0) float damage,
                          @Local(ordinal = 1) float knockback) {
      attackHooks.stream()
                 .map(AttackHooks.PostKnockback.class::cast)
                 .reduce(AttackHooks.PostKnockback::andThen)
                 .orElse(AttackHooks.PostKnockback.identity())
                 .postKnockback(damage, knockback, context);
   }
}
