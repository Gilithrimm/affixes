package com.gilithrimm.affixes.hooks;

/**
 * A quick adapter with default implementations of all functions in {@link DamageHook}
 */
public class DamageHookAdapter implements DamageHook {
   /**
    * default constructor. you probably don't care about it either, so just ignore it.
    */
   public DamageHookAdapter() {}

   @Override
   public void postShieldBlock(DamageHookContext context, float amount) {}

   @Override
   public float postArmor(DamageHookContext context, float amount) {
      return amount;
   }

   @Override
   public float postResistance(DamageHookContext context, float amount) {
      return amount;
   }

   @Override
   public float postProtection(DamageHookContext context, float amount,
                               float protection) {
      return amount;
   }

   @Override
   public float postAbsorption(DamageHookContext context, float amount) {
      return amount;
   }

   @Override
   public void postDamage(DamageHookContext context, float amount) {}

   @Override
   public void postKnockback(DamageHookContext context, float amount) {}

   @Override
   public void postDeath(DamageHookContext context, float amount) {}
}
