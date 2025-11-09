package com.gilithrimm.affixes.mixin;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.IAffixHolder;
import com.gilithrimm.affixes.hooks.AttackHook;
import com.gilithrimm.affixes.hooks.AttackHook.AttackHookContext;
import com.gilithrimm.affixes.hooks.Hook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.gilithrimm.affixes.AffixesMod.LOGGER;
import static com.gilithrimm.affixes.hooks.AttackHooks.*;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
   @Unique
   public List<AttackHook> attackHooks = new ArrayList<>();

   @Unique
   AttackHookContext context;

   protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
      super(entityType, world);
   }

   @Inject(method = "attack", at = @At("HEAD"))
   public void setup(Entity target, CallbackInfo ci) {
      context = new AttackHookContext(this, target);
      attackHooks.clear();
      attackHooks.addAll(((IAffixHolder) (Object) getStackInHand(getActiveHand()))
            .getAffixes()
            .stream()
            .map(Affix::hook)
            .filter(AttackHook.class::isInstance)//the smart way
            .map(AttackHook.class::cast)
            .toList());
      attackHooks.forEach(Hook::setup);
   }

   @Inject(method = "attack", at = @At(value = "INVOKE",
                                       target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"))
   void attribute(Entity target, CallbackInfo ci, @Local(ordinal = 0) LocalFloatRef damage) {
      damage.set(attackHooks.stream()
                            .map((Function<AttackHook, PostAttribute>) attackHook -> attackHook::postAttribute)
                            .reduce(PostAttribute::andThen)
                            .orElse(PostAttribute.identity())
                            .postAttribute(damage.get(), context));

      //render thread == base attribute
      //server thread == base + modifier
      LOGGER.debug("postAttribute: {}", damage.get());
   }

   @WrapOperation(method = "attack", at = @At(value = "INVOKE",
                                              target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V"))
   void cooldown(PlayerEntity instance, Operation<Void> original, @Local(ordinal = 0) LocalFloatRef damage) {
      damage.set(attackHooks.stream()
                            .map((Function<AttackHook, PostAttackCooldown>) attackHook -> attackHook::postAttackCooldown)
                            .reduce(PostAttackCooldown::andThen)
                            .orElse(PostAttackCooldown.identity())
                            .postAttackCooldown(damage.get(), context));
      LOGGER.debug("postAttackCooldown: {}", damage.get());
   }

   @Definition(id = "f", local = @Local(type = float.class, ordinal = 0))
   @Expression("f * 1.5")
   @ModifyExpressionValue(method = "attack", at = @At("MIXINEXTRAS:EXPRESSION"))
   float crit(float damage) {
      float total = attackHooks.stream()
                               .map((Function<AttackHook, PostCritCalc>) attackHook -> attackHook::postCritical)
                               .reduce(PostCritCalc::andThen)
                               .orElse(PostCritCalc.identity())
                               .postCritical(damage, context);
      LOGGER.debug("postCritical: {}", total);
      return total;
   }

   @Definition(id = "f", local = @Local(type = float.class, ordinal = 0))
   @Definition(id = "g", local = @Local(type = float.class, ordinal = 1))
   @Expression("f+g")
   @ModifyExpressionValue(method = "attack", at = @At("MIXINEXTRAS:EXPRESSION"))
   float enchantments(float damage) {
      float total = attackHooks.stream()
                               .map((Function<AttackHook, PostEnchantments>) attackHook -> attackHook::postEnchantments)
                               .reduce(PostEnchantments::andThen)
                               .orElse(PostEnchantments.identity())
                               .postEnchantments(damage, context);
      LOGGER.debug("postEnchantments: {}", total);
      return total;
   }

   @Inject(method = "attack",
           at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setOnFireFor(I)V", ordinal = 0))
   void fireAspect(Entity target, CallbackInfo ci, @Local(ordinal = 0) LocalFloatRef damage) {
      damage.set(attackHooks.stream()
                            .map((Function<AttackHook, PostFireAspect>) attackHook -> attackHook::postFireAspect)
                            .reduce(PostFireAspect::andThen)
                            .orElse(PostFireAspect.identity())
                            .postFireAspect(damage.get(), context));
      LOGGER.debug("postFireAspect: {}", damage.get());
   }

   @Definition(id = "damage",
               method = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
   @Definition(id = "f", local = @Local(type = float.class, ordinal = 0))
   @Expression("?.damage(?, f)")
   @ModifyExpressionValue(method = "attack", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
   boolean damage(boolean isDamaged) {
      attackHooks.stream()
                 .map((Function<AttackHook, OnDamaged>) iah -> iah::onDamage)
                 .reduce(OnDamaged::andThen)
                 .orElse(OnDamaged.identity())
                 .onDamage(isDamaged, context);
      LOGGER.debug("onDamage: {}", isDamaged);
      return isDamaged;
   }

   @Inject(method = "attack",
           at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
   void knockback(Entity target, CallbackInfo ci, @Local(ordinal = 0) float damage, @Local(ordinal = 0) int knockback) {
      attackHooks.stream()
                 .map(PostKnockback.class::cast)
                 .reduce(PostKnockback::andThen)
                 .orElse(PostKnockback.identity())
                 .postKnockback(damage, knockback * .5f, context);
      LOGGER.debug("postKnockback: dmg:{}, kb:{}", damage, knockback);
   }

   @Inject(method = "attack",
           at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V", ordinal = 1))
   void sweeping(Entity target, CallbackInfo ci, @Local(ordinal = 0) LivingEntity entity,
                 @Local(ordinal = 4) LocalFloatRef sweep) {
      sweep.set(attackHooks.stream()
                           .map((Function<AttackHook, PostSweeping>) attackHook -> attackHook::postSweeping)
                           .reduce(PostSweeping::andThen)
                           .orElse(PostSweeping.identity())
                           .postSweeping(sweep.get(), entity, context));
      LOGGER.debug("postSweeping: {} dmg for entity {}", sweep.get(), entity);
   }
}