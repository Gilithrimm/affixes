package com.gilithrimm.affixes.mixin;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.interfaces.IAffixable;
import com.gilithrimm.affixes.hooks.*;
import com.gilithrimm.affixes.hooks.AttackHook.AttackHookContext;
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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.gilithrimm.affixes.AffixesMod.LOGGER;
import static com.gilithrimm.affixes.affixes.interfaces.IAffixable.getAffixesFromStack;
import static com.gilithrimm.affixes.hooks.AttackHooks.*;

/**
 * mixin for {@link PlayerEntity}, allowing {@link AttackHook attack hooks} to work
 *
 * @since I decided it is a good idea to fuck around with this shit
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
   @Unique
   private final List<DamageHook> affixes$damageHooks = new ArrayList<>();
   @Unique
   private final List<AttackHook> affixes$attackHooks = new ArrayList<>();
   @Unique
   private AttackHookContext affixes$attackHookContext;
   @Unique
   private DamageHook.DamageHookContext affixes$damageHookContext;

   // attack

   private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType,
                             World world) {
      super(entityType, world);
   }

   @Inject(method = "attack", at = @At("HEAD"))
   private void setup_attack(Entity target, CallbackInfo ci) {
      affixes$attackHookContext = new AttackHookContext(this, target);
      affixes$attackHooks.addAll(getAffixesFromStack(getMainHandStack())
            .stream()
            .map(Affix::hook)
            .filter(AttackHook.class::isInstance)
            .map(AttackHook.class::cast)
            .toList());
      affixes$attackHooks.forEach(Hook::setup);
   }

   @Inject(method = "attack", at = @At(value = "INVOKE",
                                       target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F"))
   private void attribute(Entity target, CallbackInfo ci,
                          @Local(ordinal = 0) LocalFloatRef damage) {
      damage.set(affixes$attackHooks.stream()
                                    .map((Function<AttackHook, PostAttribute>) attackHook -> attackHook::postAttribute)
                                    .reduce(PostAttribute::andThen)
                                    .orElse(PostAttribute.identity())
                                    .postAttribute(damage.get(),
                                          affixes$attackHookContext));
      LOGGER.debug("postAttribute: {}", damage.get());
   }

   @WrapOperation(method = "attack", at = @At(value = "INVOKE",
                                              target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V"))
   private void cooldown(PlayerEntity instance, Operation<Void> original,
                         @Local(ordinal = 0) LocalFloatRef damage) {
      damage.set(affixes$attackHooks.stream()
                                    .map((Function<AttackHook, PostAttackCooldown>) attackHook -> attackHook::postAttackCooldown)
                                    .reduce(PostAttackCooldown::andThen)
                                    .orElse(PostAttackCooldown.identity())
                                    .postAttackCooldown(damage.get(),
                                          affixes$attackHookContext));
      LOGGER.debug("postAttackCooldown: {}", damage.get());
   }

   @Definition(id = "f", local = @Local(type = float.class, ordinal = 0))
   @Expression("f * 1.5")
   @ModifyExpressionValue(method = "attack", at = @At("MIXINEXTRAS:EXPRESSION"))
   private float crit(float damage) {
      float total = affixes$attackHooks.stream()
                                       .map((Function<AttackHook, PostCritical>) attackHook -> attackHook::postCritical)
                                       .reduce(PostCritical::andThen)
                                       .orElse(PostCritical.identity())
                                       .postCritical(damage, affixes$attackHookContext);
      LOGGER.debug("postCritical: {}", total);
      return total;
   }

   @Definition(id = "f", local = @Local(type = float.class, ordinal = 0))
   @Definition(id = "g", local = @Local(type = float.class, ordinal = 1))
   @Expression("f+g")
   @ModifyExpressionValue(method = "attack", at = @At("MIXINEXTRAS:EXPRESSION"))
   private float enchantments(float damage) {
      float total = affixes$attackHooks.stream()
                                       .map((Function<AttackHook, PostEnchantments>) attackHook -> attackHook::postEnchantments)
                                       .reduce(PostEnchantments::andThen)
                                       .orElse(PostEnchantments.identity())
                                       .postEnchantments(damage,
                                             affixes$attackHookContext);
      LOGGER.debug("postEnchantments: {}", total);
      return total;
   }

   @Inject(method = "attack",
           at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setOnFireFor(I)V",
                    ordinal = 0))
   private void fireAspect(Entity target, CallbackInfo ci,
                           @Local(ordinal = 0) LocalFloatRef damage) {
      damage.set(affixes$attackHooks.stream()
                                    .map((Function<AttackHook, PostFireAspect>) attackHook -> attackHook::postFireAspect)
                                    .reduce(PostFireAspect::andThen)
                                    .orElse(PostFireAspect.identity())
                                    .postFireAspect(damage.get(),
                                          affixes$attackHookContext));
      LOGGER.debug("postFireAspect: {}", damage.get());
   }

   @Definition(id = "damage",
               method = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
   @Definition(id = "f", local = @Local(type = float.class, ordinal = 0))
   @Expression("?.damage(?, f)")
   @ModifyExpressionValue(method = "attack",
                          at = @At(value = "MIXINEXTRAS:EXPRESSION"))
   private boolean hit(boolean isHit, @Local(ordinal = 0) float dmg) {
      affixes$attackHooks.stream()
                         .map((Function<AttackHook, PostHit>) ah -> ah::postHit)
                         .reduce(PostHit::andThen)
                         .orElse(PostHit.identity())
                         .postHit(isHit, dmg, affixes$attackHookContext);
      LOGGER.debug("postHit: {}, damage: {}", isHit, dmg);
      return isHit;
   }

   @Inject(method = "attack",
           at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
   private void knockback(Entity target, CallbackInfo ci,
                          @Local(ordinal = 0) float damage,
                          @Local(ordinal = 0) int knockback) {
      affixes$attackHooks.stream()
                         .map((Function<AttackHook, PostKnockback>) attackHook -> attackHook::postKnockback)
                         .reduce(PostKnockback::andThen)
                         .orElse(PostKnockback.identity())
                         .postKnockback(damage, knockback * .5f,
                               affixes$attackHookContext);
      LOGGER.debug("postKnockback: dmg:{}, kb:{}", damage, knockback);
   }

   @Inject(method = "attack",
           at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V",
                    ordinal = 1))
   private void sweeping(Entity target, CallbackInfo ci,
                         @Local(ordinal = 0) LivingEntity entity,
                         @Local(ordinal = 4) LocalFloatRef sweep) {
      sweep.set(affixes$attackHooks.stream()
                                   .map((Function<AttackHook, PostSweeping>) attackHook -> attackHook::postSweeping)
                                   .reduce(PostSweeping::andThen)
                                   .orElse(PostSweeping.identity())
                                   .postSweeping(sweep.get(), entity,
                                         affixes$attackHookContext));
      LOGGER.debug("postSweeping: {} dmg for entity {}", sweep.get(), entity);
   }

   @Inject(method = "attack",
           at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V",
                    ordinal = 1))
   private void sweeping_toggle(Entity target, CallbackInfo ci,
                                @Local(ordinal = 0) LivingEntity entity,
                                @Local(ordinal = 4) LocalFloatRef sweep) {
      var hook = affixes$attackHooks.stream()
                                    .filter(AttackHook::shouldTriggerWhenSweeping)
                                    .reduce(AttackHookAdapter::reduce)
                                    .orElse(new AttackHookAdapter());
      var sweepContext = new AttackHookContext(this, entity);
      sweep.set(hook.postAttribute(sweep.get(), sweepContext));
      sweep.set(hook.postAttackCooldown(sweep.get(), sweepContext));
      sweep.set(hook.postCritical(sweep.get(), sweepContext));
      sweep.set(hook.postEnchantments(sweep.get(), sweepContext));
      sweep.set(hook.postFireAspect(sweep.get(), sweepContext));
   }

   @Definition(id = "damage",
               method = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
   @Definition(id = "l", local = @Local(type = float.class, ordinal = 4))
   @Expression("?.damage(?, l)")
   @ModifyExpressionValue(method = "attack",
                          at = @At(value = "MIXINEXTRAS:EXPRESSION"))
   private boolean sweep_toggle_hit(boolean original,
                                    @Local(ordinal = 0) LivingEntity entity,
                                    @Local(ordinal = 4) float sweep) {
      var hook = affixes$attackHooks.stream()
                                    .filter(AttackHook::shouldTriggerWhenSweeping)
                                    .reduce(AttackHookAdapter::reduce)
                                    .orElse(new AttackHookAdapter());
      var sweepContext = new AttackHookContext(this, entity);
      hook.postHit(original, sweep, sweepContext);
      hook.postKnockback(sweep, .4f, sweepContext);
      return original;
   }

   @Inject(method = "attack", at = @At("TAIL"))
   private void teardown_attack(Entity target, CallbackInfo ci) {
      affixes$attackHooks.clear();
   }

   // damage

   @Inject(method = "damage", at = @At("HEAD"))
   private void setup_damage(DamageSource source, float amount,
                             CallbackInfoReturnable<Boolean> cir) {
      affixes$damageHookContext = new DamageHook.DamageHookContext(source, this);
//      getArmorItems()
//            .iterator()
//            .forEachRemaining(itemStack -> damageHooks.addAll(
//                  ((IAffixable) (Object) itemStack)
//                        .getAffixes()
//                        .stream()
//                        .map(Affix::hook)
//                        .filter(DamageHook.class::isInstance)
//                        .map(DamageHook.class::cast)
//                        .toList()));
      affixes$damageHooks.clear();
      List<ItemStack> affixedItems = new ArrayList<>();

      getArmorItems().forEach(affixedItems::add);
      if (getStackInHand(getActiveHand()).isIn(
            TagKey.of(RegistryKeys.ITEM, new Identifier("c:shields")))) {
         affixedItems.add(getStackInHand(getActiveHand()));
      }

      affixes$damageHooks.addAll(affixedItems
            .stream()
            .map(IAffixable::getAffixesFromStack)
            .flatMap(Collection::stream)
            .map(Affix::hook)
            .filter(DamageHook.class::isInstance)
            .map(DamageHook.class::cast)
            .toList());
      affixes$damageHooks.forEach(Hook::setup);
   }

   @WrapOperation(method = "applyDamage", at = @At(value = "INVOKE",
                                                   target = "Lnet/minecraft/entity/player/PlayerEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"))
   private float armor(PlayerEntity instance, DamageSource source, float amount,
                       Operation<Float> original) {
      amount = affixes$damageHooks.stream()
                                  .map((Function<DamageHook, DamageHooks.PostArmor>) dh -> dh::postArmor)
                                  .reduce(DamageHooks.PostArmor::andThen)
                                  .orElse(DamageHooks.PostArmor.identity())
                                  .postArmor(affixes$damageHookContext,
                                        original.call(instance, source, amount));
      LOGGER.debug("armor:{}", amount);
      return amount;
   }

   @Inject(method = "applyDamage",
           at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
   private void absorption(DamageSource source, float amount, CallbackInfo ci) {
      amount = affixes$damageHooks.stream()
                                  .map((Function<DamageHook, DamageHooks.PostAbsorption>) dh -> dh::postAbsorption)
                                  .reduce(DamageHooks.PostAbsorption::andThen)
                                  .orElse(DamageHooks.PostAbsorption.identity())
                                  .postAbsorption(affixes$damageHookContext, amount);
      LOGGER.debug("absorb:{}", amount);
   }

   @Inject(method = "applyDamage", at = @At(value = "INVOKE",
                                            target = "Lnet/minecraft/entity/player/PlayerEntity;setHealth(F)V",
                                            shift = At.Shift.AFTER))
   private void damage(DamageSource source, float amount, CallbackInfo ci) {
      affixes$damageHooks.stream()
                         .map((Function<DamageHook, DamageHooks.PostDamage>) dh -> dh::postDamage)
                         .reduce(DamageHooks.PostDamage::andThen)
                         .orElse(DamageHooks.PostDamage.identity())
                         .postDamage(affixes$damageHookContext, amount);
      LOGGER.debug("dmg:{}", amount);
   }

}