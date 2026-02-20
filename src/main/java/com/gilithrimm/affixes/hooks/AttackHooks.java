package com.gilithrimm.affixes.hooks;

import net.minecraft.entity.LivingEntity;

/**
 * a collection of all the functions in {@link AttackHook} interface, separated into
 * {@link FunctionalInterface functional interfaces} with default implementations &amp;
 * identity functions &amp; a way of chaining these functions together
 */
public abstract class AttackHooks {
   private AttackHooks() {/*no-instance*/}

   /**
    * Called after getting attack damage value from attribute data.
    * In other words, called right at the beginning of the calculations.
    *
    * @apiNote This is 1st step of damage calculation.
    * @implSpec call {@code return total} if you don't want to modify damage
    * @see AttackHook#postAttribute(float, AttackHookContext)
    */
   @FunctionalInterface
   public interface PostAttribute extends AttackHook {
      /**
       * The identity function for {@link AttackHook#postAttribute(float, AttackHookContext) postAttribute()} call.
       *
       * @return identity function for {@link AttackHook#postAttribute(float, AttackHookContext) postAttribute} call.
       */
      static PostAttribute identity() {
         return (total, context) -> total;
      }

      @Override
      default float postAttackCooldown(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postCritical(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postEnchantments(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postFireAspect(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default void postHit(boolean isHit, float damage,
                           AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb,
                                 AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity,
                                 AttackHookContext context) {
         return sweepDmg;
      }

      /**
       * composes this call with another PostAttribute() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostAttribute() call returning a composition of this call and the provided one
       */
      default PostAttribute andThen(PostAttribute call) {
         return (total, context) -> call.postAttribute(
               postAttribute(total, context), context);
      }
   }

   /**
    * Called after factoring in attack cooldown.
    *
    * @apiNote This is 2nd step of damage calculation.
    * @implSpec call {@code return total} if you don't want to modify damage
    * @see AttackHook#postAttackCooldown(float, AttackHookContext)
    */
   @FunctionalInterface
   public interface PostAttackCooldown extends AttackHook {
      /**
       * The identity function for {@link AttackHook#postAttackCooldown(float, AttackHookContext) postAttackCooldown()} call.
       *
       * @return identity function for {@link AttackHook#postAttackCooldown(float, AttackHookContext) postAttackCooldown()} call.
       */
      static PostAttackCooldown identity() {
         return (t, c) -> t;
      }

      @Override
      default float postAttribute(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postCritical(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postEnchantments(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postFireAspect(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default void postHit(boolean isHit, float damage,
                           AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb,
                                 AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity,
                                 AttackHookContext context) {
         return sweepDmg;
      }

      /**
       * composes this call with another PostAttackCooldown() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostAttackCooldown() call returning a composition of this call and the provided one
       */
      default PostAttackCooldown andThen(PostAttackCooldown call) {
         return (t, c) -> call.postAttackCooldown(postAttackCooldown(t, c), c);
      }
   }

   /**
    * Called after factoring in critical hit.
    *
    * @apiNote This is 3rd step of damage calculation.
    * @implNote Called only if the attack was critical.
    * @implSpec call {@code return total} if you don't want to modify damage
    * @see AttackHook#postCritical(float, AttackHookContext)
    */
   @FunctionalInterface
   public interface PostCritical extends AttackHook {
      /**
       * The identity function for {@link AttackHook#postCritical(float, AttackHookContext) postCritical()} call.
       *
       * @return identity function for {@link AttackHook#postCritical(float, AttackHookContext) postCritical()} call.
       */
      static PostCritical identity() {
         return (t, h) -> t;
      }

      @Override
      default float postAttribute(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postAttackCooldown(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postEnchantments(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postFireAspect(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default void postHit(boolean isHit, float damage,
                           AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb,
                                 AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity,
                                 AttackHookContext context) {
         return sweepDmg;
      }

      /**
       * composes this call with another PostCritical() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostCritical() call returning a composition of this call and the provided one
       */
      default PostCritical andThen(PostCritical call) {
         return (t, c) -> call.postCritical(postCritical(t, c), c);
      }
   }

   /**
    * Called after factoring in damage from enchantments.
    *
    * @apiNote This is 4th step of damage calculation.
    * @implSpec call {@code return total} if you don't want to modify damage
    * @see AttackHook#postEnchantments(float, AttackHookContext)
    */
   @FunctionalInterface
   public interface PostEnchantments extends AttackHook {
      /**
       * The identity function for {@link AttackHook#postEnchantments(float, AttackHookContext) postEnchantments()} call.
       *
       * @return identity function for {@link AttackHook#postEnchantments(float, AttackHookContext) postEnchantments()} call.
       */
      static PostEnchantments identity() {
         return (t, c) -> t;
      }

      @Override
      default float postAttribute(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postAttackCooldown(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postCritical(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postFireAspect(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default void postHit(boolean isHit, float damage,
                           AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb,
                                 AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity,
                                 AttackHookContext context) {
         return sweepDmg;
      }

      /**
       * composes this call with another PostEnchantments() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostEnchantments() call returning a composition of this call and the provided one
       */
      default PostEnchantments andThen(PostEnchantments call) {
         return (t, c) -> call.postEnchantments(postEnchantments(t, c), c);
      }
   }

   /**
    * Called after applying Fire Aspect onto target.
    *
    * @apiNote This is last step of damage calculation.
    * @implNote Called only if Fire Aspect is present.
    * @implSpec call {@code return total} if you don't want to modify damage
    * @see AttackHook#postFireAspect(float, AttackHookContext)
    */
   @FunctionalInterface
   public interface PostFireAspect extends AttackHook {
      /**
       * The identity function for {@link AttackHook#postFireAspect(float, AttackHookContext)  postFireAspect()} call.
       *
       * @return identity function for {@link AttackHook#postFireAspect(float, AttackHookContext) postFireAspect()} call.
       */
      static PostFireAspect identity() {
         return (t, c) -> t;
      }

      @Override
      default float postAttribute(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postAttackCooldown(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postCritical(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postEnchantments(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default void postHit(boolean isHit, float damage,
                           AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb,
                                 AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity,
                                 AttackHookContext context) {
         return sweepDmg;
      }

      /**
       * composes this call with another PostFireAspect() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostFireAspect() call returning a composition of this call and the provided one
       */
      default PostFireAspect andThen(PostFireAspect call) {
         return (t, c) -> call.postFireAspect(postFireAspect(t, c), c);
      }
   }

   /**
    * Called right after damaging the target.
    *
    * @see AttackHook#postHit(boolean, float, AttackHookContext)
    */
   @FunctionalInterface
   public interface PostHit extends AttackHook {
      /**
       * The identity function for {@link AttackHook#postHit(boolean, float, AttackHookContext) postHit()} call.
       *
       * @return identity function for {@link AttackHook#postHit(boolean, float, AttackHookContext) postHit()} call.
       */
      static PostHit identity() {
         return (d, damage, c) -> {};
      }

      @Override
      default float postAttribute(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postAttackCooldown(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postCritical(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postEnchantments(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postFireAspect(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default void postKnockback(float dmg, float kb,
                                 AttackHookContext context) {
      }

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity,
                                 AttackHookContext context) {
         return sweepDmg;
      }


      /**
       * composes this call with another PostHit() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostHit() call returning a composition of this call and the provided one
       */
      default PostHit andThen(PostHit call) {
         return (b, d, c) -> {
            postHit(b, d, c);
            call.postHit(b, d, c);
         };
      }
   }

   /**
    * Called after applying knockback to target.
    *
    * @implNote Requires target actually get hit.
    * @see AttackHook#postKnockback(float, float, AttackHookContext)
    */
   @FunctionalInterface
   public interface PostKnockback extends AttackHook {
      /**
       * The identity function for {@link AttackHook#postKnockback(float, float, AttackHookContext) postKnockback()} call.
       *
       * @return identity function for {@link AttackHook#postKnockback(float, float, AttackHookContext) postKnockback()} call.
       */
      static PostKnockback identity() {
         return (d, k, c) -> {};
      }

      @Override
      default float postAttribute(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postAttackCooldown(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postCritical(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postEnchantments(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postFireAspect(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default void postHit(boolean isHit, float damage,
                           AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity,
                                 AttackHookContext context) {
         return sweepDmg;
      }


      /**
       * composes this call with another PostKnockback() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostKnockback() call returning a composition of this call and the provided one
       */
      default PostKnockback andThen(PostKnockback call) {
         return (d, k, c) -> {
            postKnockback(d, k, c);
            call.postKnockback(d, k, c);
         };
      }
   }

   /**
    * Called for each entity hit with a sweeping attack separately; right before applying damage to them.
    *
    * @implNote Requires original target actually get hit.
    * @implSpec call {@code return total} if you don't want to modify damage
    * @see AttackHook#postSweeping(float, LivingEntity, AttackHookContext)
    */
   @FunctionalInterface
   public interface PostSweeping extends AttackHook {
      /**
       * The identity function for {@link AttackHook#postSweeping(float, LivingEntity, AttackHookContext) postSweeping()} call.
       *
       * @return identity function for {@link AttackHook#postSweeping(float, LivingEntity, AttackHookContext) postSweeping()} call.
       */
      static PostSweeping identity() {
         return (d, e, c) -> d;
      }

      @Override
      default float postAttribute(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postAttackCooldown(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postCritical(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postEnchantments(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default float postFireAspect(float total, AttackHookContext context) {
         return total;
      }

      @Override
      default void postHit(boolean isHit, float damage,
                           AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb,
                                 AttackHookContext context) {}

      /**
       * composes this call with another PostSweeping() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostSweeping() call returning a composition of this call and the provided one
       */
      default PostSweeping andThen(PostSweeping call) {
         return (d, e, c) -> call.postSweeping(postSweeping(d, e, c), e, c);
      }
   }
}
