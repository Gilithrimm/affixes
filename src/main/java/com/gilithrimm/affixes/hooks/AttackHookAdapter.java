package com.gilithrimm.affixes.hooks;

import net.minecraft.entity.LivingEntity;

public class AttackHookAdapter implements AttackHook {

   @Override
   public float postAttribute(float total, AttackHookContext context) {
      return total;
   }

   @Override
   public float postAttackCooldown(float total, AttackHookContext context) {
      return total;
   }

   @Override
   public float postCritical(float total, AttackHookContext context) {
      return total;
   }

   @Override
   public float postEnchantments(float total, AttackHookContext context) {
      return total;
   }

   @Override
   public float postFireAspect(float total, AttackHookContext context) {
      return total;
   }

   @Override
   public void onDamage(boolean isDamaged, AttackHookContext context) {}

   @Override
   public void postKnockback(float dmg, float kb, AttackHookContext context) {}

   @Override
   public float postSweeping(float sweepDmg, LivingEntity sweptEntity, AttackHookContext context) {
      return sweepDmg;
   }
}
