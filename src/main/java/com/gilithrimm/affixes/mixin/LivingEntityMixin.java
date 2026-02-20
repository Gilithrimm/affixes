package com.gilithrimm.affixes.mixin;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.Affixes;
import com.gilithrimm.affixes.affixes.interfaces.IAffixEntityData;
import com.gilithrimm.affixes.affixes.interfaces.IAffixable;
import com.gilithrimm.affixes.effects.Bleed;
import com.gilithrimm.affixes.effects.Vulnerability;
import com.gilithrimm.affixes.hooks.DamageHook;
import com.gilithrimm.affixes.hooks.DamageHooks.*;
import com.gilithrimm.affixes.hooks.Hook;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Function;

import static com.gilithrimm.affixes.AffixesMod.NBT_PREFIX;
import static com.gilithrimm.affixes.effects.AffixEffects.BLEED;
import static com.gilithrimm.affixes.effects.AffixEffects.VULNERABILITY;

/**
 * mixin for {@link LivingEntity}, allowing a bunch of shit to work - {@link IAffixEntityData data saved on entities for affixes}
 * (for example {@link Affixes#HUNTER_MARK Hunter's Mark} {@link Affixes#MARK_OF_THE_BEAST and} {@link Affixes#BLOOD_MARKS related} to it),
 * {@link DamageHook}, {@link Bleed bleed}, {@link Vulnerability vulnerability}
 *
 * @since this part's actually quite a bit newer (together with damage hooks as mentioned above) -
 * which explains overabundance of affixes triggering on attack and lack of affixes triggering on damage
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin
      implements IAffixEntityData {
   @Shadow
   @Final
   private static Logger LOGGER;
   @Unique
   private final List<DamageHook> damageHooks = new ArrayList<>();
   @Unique
   private final Map<String, NbtElement> affixData = new HashMap<>();
   @Unique
   private DamageHook.DamageHookContext context;

   private LivingEntityMixin() {}
//hooks

   @Inject(method = "damage", at = @At("HEAD"))
   private void setup(DamageSource source, float amount,
                      CallbackInfoReturnable<Boolean> cir) {
      damageHooks.clear();
      context = new DamageHook.DamageHookContext(source,
            (LivingEntity) (Object) this);
      List<ItemStack> affixedItems = new ArrayList<>();

      getArmorItems().forEach(affixedItems::add);
      if (getMainHandStack().isIn(
            TagKey.of(RegistryKeys.ITEM, new Identifier("c:shields")))) {
         affixedItems.add(getMainHandStack());
      }
      if (getOffHandStack().isIn(
            TagKey.of(RegistryKeys.ITEM, new Identifier("c:shields")))) {
         affixedItems.add(getOffHandStack());
      }

      damageHooks.addAll(affixedItems
            .stream()
            .map(IAffixable.class::cast)
            .map(IAffixable::affixes$getAffixes)
            .flatMap(Collection::stream)
            .map(Affix::hook)
            .filter(DamageHook.class::isInstance)
            .map(DamageHook.class::cast)
            .toList());
      damageHooks.forEach(Hook::setup);
   }

   /**
    * Shadowed from {@link LivingEntity}. ignore.
    *
    * @return iterable over item stacks
    */
   @Shadow
   public abstract Iterable<ItemStack> getArmorItems();

   /**
    * Shadowed from {@link LivingEntity}. ignore.
    *
    * @return item stack
    */
   @Shadow
   public abstract ItemStack getMainHandStack();

   /**
    * Shadowed from {@link LivingEntity}. ignore.
    *
    * @return item stack
    */
   @Shadow
   public abstract ItemStack getOffHandStack();

   @Inject(method = "damage",
           at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V",
                    shift = At.Shift.AFTER))
   private void shieldBlock(DamageSource source, float amount,
                            CallbackInfoReturnable<Boolean> cir) {
      damageHooks.stream()
                 .map((Function<? super DamageHook, PostShieldBlock>) dh -> dh::postShieldBlock)
                 .reduce(PostShieldBlock::andThen)
                 .orElse(PostShieldBlock.identity())
                 .postShieldBlock(context, amount);
      LOGGER.debug("shield:{}", amount);
   }

   @WrapOperation(method = "applyDamage", at = @At(value = "INVOKE",
                                                   target = "Lnet/minecraft/entity/LivingEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"))
   private float armor(LivingEntity instance, DamageSource source, float amount,
                       Operation<Float> original) {
      amount = damageHooks.stream()
                          .map((Function<DamageHook, PostArmor>) dh -> dh::postArmor)
                          .reduce(PostArmor::andThen)
                          .orElse(PostArmor.identity())
                          .postArmor(context,
                                original.call(instance, source, amount));
      LOGGER.debug("armor:{}", amount);
      return amount;
   }

   @WrapOperation(method = "modifyAppliedDamage", at = @At(value = "INVOKE",
                                                           target = "Lnet/minecraft/entity/DamageUtil;getInflictedDamage(FF)F"))
   private float protection(float damageDealt, float protection,
                            Operation<Float> original) {
      damageDealt = damageHooks.stream()
                               .map((Function<DamageHook, PostProtection>) dh -> dh::postProtection)
                               .reduce(PostProtection::andThen)
                               .orElse(PostProtection.identity())
                               .postProtection(context,
                                     original.call(damageDealt, protection),
                                     protection);
      LOGGER.debug("prot:{} dmg:{}", protection, damageDealt);
      return damageDealt;
   }

   @Inject(method = "modifyAppliedDamage",
           at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
   private void resistance(DamageSource source, float amount,
                           CallbackInfoReturnable<Float> cir) {
      amount = damageHooks.stream()
                          .map((Function<DamageHook, PostResistance>) dh -> dh::postResistance)
                          .reduce(PostResistance::andThen)
                          .orElse(PostResistance.identity())
                          .postResistance(context, amount);
      LOGGER.debug("res:{}", amount);
   }

   @Inject(method = "applyDamage",
           at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"))
   private void absorption(DamageSource source, float amount, CallbackInfo ci) {
      amount = damageHooks.stream()
                          .map((Function<DamageHook, PostAbsorption>) dh -> dh::postAbsorption)
                          .reduce(PostAbsorption::andThen)
                          .orElse(PostAbsorption.identity())
                          .postAbsorption(context, amount);
      LOGGER.debug("absorb:{}", amount);
   }

   @Inject(method = "applyDamage", at = @At(value = "INVOKE",
                                            target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
   private void damage(DamageSource source, float amount, CallbackInfo ci) {
      damageHooks.stream()
                 .map((Function<DamageHook, PostDamage>) dh -> dh::postDamage)
                 .reduce(PostDamage::andThen)
                 .orElse(PostDamage.identity())
                 .postDamage(context, amount);
      LOGGER.debug("dmg:{}", amount);
   }

   @Inject(method = "damage", at = @At(value = "INVOKE",
                                       target = "Lnet/minecraft/entity/LivingEntity;takeKnockback(DDD)V"))
   private void knockback(DamageSource source, float amount,
                          CallbackInfoReturnable<Boolean> cir) {
      damageHooks.stream()
                 .map((Function<DamageHook, PostKnockback>) dh -> dh::postKnockback)
                 .reduce(PostKnockback::andThen)
                 .orElse(PostKnockback.identity())
                 .postKnockback(context, amount);
      LOGGER.debug("kb:{}", amount);
   }

   @Inject(method = "damage", at = @At(value = "INVOKE",
                                       target = "Lnet/minecraft/entity/LivingEntity;onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"))
   private void death(DamageSource source, float amount,
                      CallbackInfoReturnable<Boolean> cir) {
      damageHooks.stream()
                 .map((Function<DamageHook, PostDeath>) dh -> dh::postDeath)
                 .reduce(PostDeath::andThen)
                 .orElse(PostDeath.identity())
                 .postDeath(context, amount);
      LOGGER.debug("ded:{}", amount);
   }

   //affix data fuckery

   @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
   private void writeNbt(NbtCompound nbt, CallbackInfo ci) {
      if (!nbt.contains(NBT_PREFIX)) {
         nbt.put(NBT_PREFIX, new NbtCompound());
      }
      affixData.forEach(nbt.getCompound(NBT_PREFIX)::put);
   }

   @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
   private void readNbt(NbtCompound nbt, CallbackInfo ci) {
      if (nbt.contains(NBT_PREFIX)) {
         final NbtCompound data = nbt.getCompound(NBT_PREFIX);
         data.getKeys().forEach(key -> affixData.put(key, data.get(key)));
      }
   }

   @Override
   public NbtElement affixes$getData(String key) {
      return affixData.get(key);
   }

   @Override
   public void affixes$putData(String key, NbtElement data) {
      affixData.put(key, data);
   }

   @Override
   public boolean affixes$containsData(String key) {
      return affixData.containsKey(key);
   }

   //vulnerable status effect

   @Definition(id = "getAmplifier",
               method = "Lnet/minecraft/entity/effect/StatusEffectInstance;getAmplifier()I")
   @Definition(id = "getStatusEffect",
               method = "Lnet/minecraft/entity/LivingEntity;getStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/entity/effect/StatusEffectInstance;")
   @Expression("(this.getStatusEffect(?).getAmplifier() + 1) * 5")
   @ModifyExpressionValue(method = "modifyAppliedDamage",
                          at = @At(value = "MIXINEXTRAS:EXPRESSION"))
   private int vulnerableAndResistant(int original) {
      if (hasStatusEffect(VULNERABILITY)) {
         original -= (getStatusEffect(VULNERABILITY).getAmplifier() + 1) * 5;
      }
      return original;
   }

   /**
    * shadowed from {@link LivingEntity}. ignore.
    *
    * @param effect effect
    * @return boolean
    */
   @Shadow
   public abstract boolean hasStatusEffect(StatusEffect effect);

   /**
    * shadowed from {@link LivingEntity}. ignore.
    *
    * @param effect effect
    * @return effect instance
    */
   @Shadow
   public abstract @Nullable StatusEffectInstance getStatusEffect(
         StatusEffect effect);

   @ModifyReturnValue(method = "modifyAppliedDamage", at = @At("RETURN"))
   private float vulnerable(float original,
                            @Local(argsOnly = true) DamageSource src) {
      if (original != 0 &&
          (!hasStatusEffect(StatusEffects.RESISTANCE) ||
           src.isIn(DamageTypeTags.BYPASSES_RESISTANCE))) {
         if (hasStatusEffect(VULNERABILITY)) {
            int impact = 5 + getStatusEffect(VULNERABILITY).getAmplifier() + 1;
            original *= impact / 5f;
         }
      }
      return original;
   }

   //bleed

   @ModifyReturnValue(method = "modifyAppliedDamage", at = @At("RETURN"))
   private float bleed(float damage, @Local(argsOnly = true) DamageSource src) {
      if (damage != 0 && hasStatusEffect(BLEED)) {
         float impact = Bleed.calculateDamage(
               getStatusEffect(BLEED).getAmplifier() + 1);
         damage += damage * impact;
      }
      return damage;
   }

   //charge from lightning

   @Override
   protected void lightning(ServerWorld world, LightningEntity lightning,
                            CallbackInfo ci) {
      super.lightning(world, lightning, ci);
      String key = "charge";
      int charge = 1000;
      if (affixData.containsKey(key)) {
         charge += ((NbtInt) affixData.get(key)).intValue();
      }
      affixData.put(key, NbtInt.of(charge));
   }
}
