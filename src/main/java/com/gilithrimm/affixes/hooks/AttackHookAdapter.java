package com.gilithrimm.affixes.hooks;

import net.minecraft.entity.LivingEntity;

/**
 * A quick adapter with default implementations of all functions in {@link AttackHook}
 */
public class AttackHookAdapter implements AttackHook {

   /**
    * default constructor. you probably don't care about it either, so just ignore it.
    */
   public AttackHookAdapter() {}

   /**
    * Joins 2 {@link AttackHook attack hooks} together by casting arguments to subtypes &amp; calling andThen() on them.
    *
    * @param acc  attack hook to join to
    * @param next attack hook that gets joined
    * @return new attack hook, being composition of all functions within {@link AttackHook}
    * @see AttackHooks.PostAttribute#andThen(AttackHooks.PostAttribute)
    * @see AttackHooks.PostAttackCooldown#andThen(AttackHooks.PostAttackCooldown)
    * @see AttackHooks.PostCritical#andThen(AttackHooks.PostCritical)
    * @see AttackHooks.PostEnchantments#andThen(AttackHooks.PostEnchantments)
    * @see AttackHooks.PostFireAspect#andThen(AttackHooks.PostFireAspect)
    * @see AttackHooks.PostHit#andThen(AttackHooks.PostHit)
    * @see AttackHooks.PostKnockback#andThen(AttackHooks.PostKnockback)
    * @see AttackHooks.PostSweeping#andThen(AttackHooks.PostSweeping)
    */
   public static AttackHook reduce(AttackHook acc, AttackHook next) {
      if (next == null) {
         return acc;
      }
      return new AttackHook() {
         @Override
         public float postAttribute(
               float t,
               AttackHookContext c) {
            return ((AttackHooks.PostAttribute) acc::postAttribute)
                  .andThen(next::postAttribute)
                  .postAttribute(t, c);
         }

         @Override
         public float postAttackCooldown(
               float t,
               AttackHookContext c) {
            return ((AttackHooks.PostAttackCooldown) acc::postAttackCooldown)
                  .andThen(next::postAttackCooldown)
                  .postAttackCooldown(t, c);
         }

         @Override
         public float postCritical(
               float t,
               AttackHookContext c) {
            return ((AttackHooks.PostCritical) acc::postCritical)
                  .andThen(next::postCritical)
                  .postCritical(t, c);
         }

         @Override
         public float postEnchantments(
               float t,
               AttackHookContext c) {
            return ((AttackHooks.PostEnchantments) acc::postEnchantments)
                  .andThen(next::postEnchantments)
                  .postEnchantments(t, c);
         }

         @Override
         public float postFireAspect(
               float t,
               AttackHookContext c) {
            return ((AttackHooks.PostFireAspect) acc::postFireAspect)
                  .andThen(next::postFireAspect)
                  .postFireAspect(t, c);
         }

         @Override
         public void postHit(boolean h,
                             float t,
                             AttackHookContext c) {
            ((AttackHooks.PostHit) acc::postHit)
                  .andThen(next::postHit)
                  .postHit(h, t, c);
         }

         @Override
         public void postKnockback(float t,
                                   float k,
                                   AttackHookContext c) {
            ((AttackHooks.PostKnockback) acc::postKnockback)
                  .andThen(next::postKnockback)
                  .postKnockback(t, k, c);
         }

         @Override
         public float postSweeping(
               float t,
               LivingEntity e,
               AttackHookContext c) {
            return ((AttackHooks.PostSweeping) acc::postSweeping)
                  .andThen(next::postSweeping)
                  .postSweeping(t, e, c);
         }
      };
   }

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
   public void postHit(boolean isHit, float damage,
                       AttackHookContext context) {}

   @Override
   public void postKnockback(float dmg, float kb, AttackHookContext context) {}

   @Override
   public float postSweeping(float sweepDmg, LivingEntity sweptEntity,
                             AttackHookContext context) {
      return sweepDmg;
   }
}
