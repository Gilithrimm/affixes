package com.gilithrimm.affixes.hooks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Functions hooked into {@code attack()} function
 */
public interface AttackHook extends Hook {
   // impact dmg dealt

   /**
    * Called after getting attack damage value from attribute data.
    * In other words, called right at the beginning of the calculations.
    *
    * @param total   total damage calculated so far, i.e. damage from the weapon used (and probably also potion effects of the Strength/Weakness kind)
    * @param context context of this hook, i.e. attacker and target
    * @return total damage calculated so far (call {@code return total} if you don't want to modify damage)
    * @apiNote This is 1st step of damage calculation.
    */
   float postAttribute(float total, AttackHookContext context);


   /**
    * Called after factoring in attack cooldown.
    *
    * @param total   total damage calculated so far, i.e. attack cooldown modifier * damage from previous steps
    * @param context context of this hook, i.e. attacker and target
    * @return total damage calculated so far (call {@code return total} if you don't want to modify damage)
    * @apiNote This is 2nd step of damage calculation.
    */
   float postAttackCooldown(float total, AttackHookContext context);

   /**
    * Called after factoring in critical hit.
    *
    * @param total   total damage calculated so far, i.e. 1.5 * damage from previous steps
    * @param context context of this hook, i.e. attacker and target
    * @return total damage calculated so far (call {@code return total} if you don't want to modify damage)
    * @apiNote This is 3rd step of damage calculation.
    * @implNote Called only if the attack was critical.
    */
   float postCritical(float total, AttackHookContext context);

   /**
    * Called after factoring in damage from enchantments.
    *
    * @param total   total damage calculated so far, i.e. enchantments damage + damage from previous steps
    * @param context context of this hook, i.e. attacker and target
    * @return total damage calculated so far (call {@code return total} if you don't want to modify damage)
    * @apiNote This is 4th step of damage calculation.
    */
   float postEnchantments(float total, AttackHookContext context);

   /**
    * Called after applying Fire Aspect onto target.
    *
    * @param total   total damage calculated so far, i.e. damage from previous steps
    * @param context context of this hook, i.e. attacker and target
    * @return total damage calculated so far (call {@code return total} if you don't want to modify damage)
    * @apiNote This is last step of damage calculation.
    * @implNote Called only if Fire Aspect is present.
    */
   float postFireAspect(float total, AttackHookContext context);

   // doesn't impact dmg dealt


   /**
    * Called right after damaging the target.
    *
    * @param isDamaged did the target actually get damaged, i.e. did damage get through
    * @param context   context of this hook, i.e. attacker and target
    */
   void onDamage(boolean isDamaged, AttackHookContext context);

   /**
    * Called after applying knockback to target.
    *
    * @param dmg     total damage applied to target
    * @param kb      knockback applied to the target
    * @param context context of this hook, i.e. attacker and target
    * @implNote Requires target actually get hit.
    */
   void postKnockback(float dmg, float kb, AttackHookContext context);

   /**
    * Called for each entity hit with a sweeping attack separately; right before applying damage to them.
    *
    * @param sweepDmg    total damage calculated so far, i.e. 1 + (sweeping modifier * damage from previous steps).
    * @param sweptEntity entity about to be hit with sweeping attack
    * @param context     context of this hook, i.e. attacker and original target
    * @return total damage calculated so far (call {@code return total} if you don't want to modify damage)
    * @implNote Requires original target actually get hit.
    */
   float postSweeping(float sweepDmg, LivingEntity sweptEntity, AttackHookContext context);

   /**
    * Context required for attack hooks.
    *
    * @param attacker entity attacking (we know it's a living since it holds the item that has the affix)
    * @param target   entity being attacked
    */
   record AttackHookContext(LivingEntity attacker, Entity target) {
      public ItemStack affixedTool() {
         return attacker.getMainHandStack();
      }

      public World world() {
         return attacker.getWorld();
      }
   }
}
