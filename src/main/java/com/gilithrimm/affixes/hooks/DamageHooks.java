package com.gilithrimm.affixes.hooks;

/**
 * a collection of all the functions in {@link DamageHook} interface, separated into
 * {@link FunctionalInterface functional interfaces} with default implementations &amp;
 * identity functions &amp; a way of chaining these functions together
 */
public abstract class DamageHooks {
   private DamageHooks() {}

   /**
    * Called when damage was successfully blocked by shield
    *
    * @see com.gilithrimm.affixes.hooks.DamageHook#postShieldBlock(DamageHookContext, float)
    */
   @FunctionalInterface
   public interface PostShieldBlock extends DamageHook {
      /**
       * The identity function for {@link DamageHook#postShieldBlock(DamageHookContext, float) postShieldBlock()} call.
       *
       * @return identity function for {@link DamageHook#postShieldBlock(DamageHookContext, float) postShieldBlock()} call.
       */
      static PostShieldBlock identity() {
         return (s, a) -> {};
      }

      @Override
      default float postArmor(DamageHookContext context, float amount) {
         return amount;
      }

      @Override
      default float postResistance(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default float postProtection(DamageHookContext context,
                                   float amount, float protection) {
         return amount;
      }

      @Override
      default float postAbsorption(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default void postDamage(DamageHookContext context, float amount) {}

      @Override
      default void postKnockback(DamageHookContext context,
                                 float amount) {}

      @Override
      default void postDeath(DamageHookContext context, float amount) {}

      /**
       * composes this call with another PostShieldBlock() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostShieldBlock() call returning a composition of this call and the provided one
       */
      default PostShieldBlock andThen(PostShieldBlock call) {
         return (s, a) -> {
            postShieldBlock(s, a);
            call.postShieldBlock(s, a);
         };
      }
   }

   /**
    * Called after calculating how much damage was blocked by the armor
    *
    * @see com.gilithrimm.affixes.hooks.DamageHook#postArmor(DamageHookContext, float)
    */
   @FunctionalInterface
   public interface PostArmor extends DamageHook {
      /**
       * The identity function for {@link DamageHook#postArmor(DamageHookContext, float) postArmor()} call.
       *
       * @return identity function for {@link DamageHook#postArmor(DamageHookContext, float)  postArmor()} call.
       */
      static PostArmor identity() {
         return (s, a) -> a;
      }

      /**
       * composes this call with another PostArmor() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostArmor() call returning a composition of this call and the provided one
       */
      default PostArmor andThen(PostArmor call) {
         return (s, a) -> call.postArmor(s, postArmor(s, a));
      }

      @Override
      default void postShieldBlock(DamageHookContext context,
                                   float amount) {}

      @Override
      default float postResistance(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default float postProtection(DamageHookContext context,
                                   float amount, float protection) {
         return amount;
      }

      @Override
      default float postAbsorption(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default void postDamage(DamageHookContext context, float amount) {}

      @Override
      default void postKnockback(DamageHookContext context,
                                 float amount) {}

      @Override
      default void postDeath(DamageHookContext context, float amount) {}
   }

   /**
    * Called after calculating how much did damage change from status effects (like
    * <a href="https://minecraft.wiki/w/Resistance">Resistance</a> and
    * {@link com.gilithrimm.affixes.effects.Vulnerability Vulnerability}
    *
    * @see com.gilithrimm.affixes.hooks.DamageHook#postResistance(DamageHookContext, float)
    */
   @FunctionalInterface
   public interface PostResistance extends DamageHook {
      /**
       * The identity function for {@link DamageHook#postResistance(DamageHookContext, float) postResistance()} call.
       *
       * @return identity function for {@link DamageHook#postResistance(DamageHookContext, float) postResistance()} call.
       */
      static PostResistance identity() {
         return (s, a) -> a;
      }

      /**
       * composes this call with another PostResistance() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostResistance() call returning a composition of this call and the provided one
       */
      default PostResistance andThen(PostResistance call) {
         return (s, a) -> call.postResistance(s, postResistance(s, a));
      }

      @Override
      default void postShieldBlock(DamageHookContext context,
                                   float amount) {}

      @Override
      default float postArmor(DamageHookContext context, float amount) {
         return amount;
      }

      @Override
      default float postProtection(DamageHookContext context,
                                   float amount, float protection) {
         return amount;
      }

      @Override
      default float postAbsorption(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default void postDamage(DamageHookContext context, float amount) {}

      @Override
      default void postKnockback(DamageHookContext context,
                                 float amount) {}

      @Override
      default void postDeath(DamageHookContext context, float amount) {}
   }

   /**
    * Called after calculating how much damage was reduced by <a href="https://minecraft.wiki/w/Protection">Protection enchantments</a>
    *
    * @see com.gilithrimm.affixes.hooks.DamageHook#postProtection(DamageHookContext, float, float)
    */
   @FunctionalInterface
   public interface PostProtection extends DamageHook {
      /**
       * The identity function for {@link DamageHook#postProtection(DamageHookContext, float, float) postProtection()} call.
       *
       * @return identity function for {@link DamageHook#postProtection(DamageHookContext, float, float) postProtection()} call.
       */
      static PostProtection identity() {
         return (s, a, p) -> a;
      }

      /**
       * composes this call with another PostProtection() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostProtection() call returning a composition of this call and the provided one
       */
      default PostProtection andThen(PostProtection call) {
         return (s, a, p) -> call.postProtection(s, postProtection(s, a, p), p);
      }

      @Override
      default void postShieldBlock(DamageHookContext context,
                                   float amount) {}

      @Override
      default float postArmor(DamageHookContext context, float amount) {
         return amount;
      }

      @Override
      default float postResistance(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default float postAbsorption(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default void postDamage(DamageHookContext context, float amount) {}

      @Override
      default void postKnockback(DamageHookContext context,
                                 float amount) {}

      @Override
      default void postDeath(DamageHookContext context, float amount) {}
   }

   /**
    * Called after reducing damage by health given by <a href="https://minecraft.wiki/w/Absorption">Absorption</a> status effect
    *
    * @see com.gilithrimm.affixes.hooks.DamageHook#postAbsorption(DamageHookContext, float)
    */
   @FunctionalInterface
   public interface PostAbsorption extends DamageHook {
      /**
       * The identity function for {@link DamageHook#postAbsorption(DamageHookContext, float) postAbsorption()} call.
       *
       * @return identity function for {@link DamageHook#postAbsorption(DamageHookContext, float) postAbsorption()} call.
       */
      static PostAbsorption identity() {
         return (s, a) -> a;
      }

      /**
       * composes this call with another PostAbsorption() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostAbsorption() call returning a composition of this call and the provided one
       */
      default PostAbsorption andThen(PostAbsorption call) {
         return (s, a) -> call.postAbsorption(s, postAbsorption(s, a));
      }

      @Override
      default void postShieldBlock(DamageHookContext context,
                                   float amount) {}

      @Override
      default float postArmor(DamageHookContext context, float amount) {
         return amount;
      }

      @Override
      default float postResistance(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default float postProtection(DamageHookContext context,
                                   float amount, float protection) {
         return amount;
      }

      @Override
      default void postDamage(DamageHookContext context, float amount) {}

      @Override
      default void postKnockback(DamageHookContext context,
                                 float amount) {}

      @Override
      default void postDeath(DamageHookContext context, float amount) {}
   }

   /**
    * Called right after damaging the victim.
    *
    * @see com.gilithrimm.affixes.hooks.DamageHook#postDamage(DamageHookContext, float)
    */
   @FunctionalInterface
   public interface PostDamage extends DamageHook {
      /**
       * The identity function for {@link DamageHook#postDamage(DamageHookContext, float) postDamage()} call.
       *
       * @return identity function for {@link DamageHook#postDamage(DamageHookContext, float) postDamage()} call.
       */
      static PostDamage identity() {
         return (s, a) -> {};
      }

      /**
       * composes this call with another PostDamage() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostDamage() call returning a composition of this call and the provided one
       */
      default PostDamage andThen(PostDamage call) {
         return (s, a) -> {
            postDamage(s, a);
            call.postDamage(s, a);
         };
      }

      @Override
      default void postShieldBlock(DamageHookContext context,
                                   float amount) {}

      @Override
      default float postArmor(DamageHookContext context, float amount) {
         return amount;
      }

      @Override
      default float postResistance(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default float postProtection(DamageHookContext context,
                                   float amount, float protection) {
         return amount;
      }

      @Override
      default float postAbsorption(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default void postKnockback(DamageHookContext context,
                                 float amount) {}

      @Override
      default void postDeath(DamageHookContext context, float amount) {}
   }

   /**
    * Called after applying knockback to this entity
    *
    * @see com.gilithrimm.affixes.hooks.DamageHook#postKnockback(DamageHookContext, float)
    */
   @FunctionalInterface
   public interface PostKnockback extends DamageHook {
      /**
       * The identity function for {@link DamageHook#postKnockback(DamageHookContext, float) postKnockback()} call.
       *
       * @return identity function for {@link DamageHook#postKnockback(DamageHookContext, float) postKnockback()} call.
       */
      static PostKnockback identity() {
         return (s, a) -> {};
      }

      /**
       * composes this call with another PostKnockback() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostKnockback() call returning a composition of this call and the provided one
       */
      default PostKnockback andThen(PostKnockback call) {
         return (s, a) -> {
            postKnockback(s, a);
            call.postKnockback(s, a);
         };
      }

      @Override
      default void postShieldBlock(DamageHookContext context,
                                   float amount) {}

      @Override
      default float postArmor(DamageHookContext context, float amount) {
         return amount;
      }

      @Override
      default float postResistance(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default float postProtection(DamageHookContext context,
                                   float amount, float protection) {
         return amount;
      }

      @Override
      default float postAbsorption(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default void postDamage(DamageHookContext context, float amount) {}

      @Override
      default void postDeath(DamageHookContext context, float amount) {}
   }

   /**
    * Called after this entity died
    *
    * @see com.gilithrimm.affixes.hooks.DamageHook#postDeath(DamageHookContext, float)
    */
   @FunctionalInterface
   public interface PostDeath extends DamageHook {
      /**
       * The identity function for {@link DamageHook#postDeath(DamageHookContext, float) postDeath()} call.
       *
       * @return identity function for {@link DamageHook#postDeath(DamageHookContext, float) postDeath()} call.
       */
      static PostDeath identity() {
         return (src, a) -> {};
      }

      @Override
      default void postShieldBlock(DamageHookContext context,
                                   float amount) {}

      @Override
      default float postArmor(DamageHookContext context, float amount) {
         return amount;
      }

      @Override
      default float postResistance(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default float postProtection(DamageHookContext context,
                                   float amount, float protection) {
         return amount;
      }

      @Override
      default float postAbsorption(DamageHookContext context,
                                   float amount) {
         return amount;
      }

      @Override
      default void postDamage(DamageHookContext context, float amount) {}

      @Override
      default void postKnockback(DamageHookContext context,
                                 float amount) {}

      /**
       * composes this call with another PostDeath() call, allowing for multiple affixes to work on the same hook
       *
       * @param call call on this hook to be composed with this call
       * @return a PostDeath() call returning a composition of this call and the provided one
       */
      default PostDeath andThen(PostDeath call) {
         return (src, a) -> {
            postDeath(src, a);
            call.postDeath(src, a);
         };
      }
   }
}
