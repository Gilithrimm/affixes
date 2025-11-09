package com.gilithrimm.affixes.hooks;

import net.minecraft.entity.LivingEntity;

public abstract class AttackHooks {
   private AttackHooks() {/*no-instance*/}

   @FunctionalInterface
   public interface PostAttribute extends AttackHook {
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
      default void onDamage(boolean isDamaged, AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb, AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity, AttackHookContext context) {
         return sweepDmg;
      }

      default PostAttribute andThen(PostAttribute hook) {
         return (total, context) -> hook.postAttribute(postAttribute(total, context), context);
      }
   }

   @FunctionalInterface
   public interface PostAttackCooldown extends AttackHook {
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
      default void onDamage(boolean isDamaged, AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb, AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity, AttackHookContext context) {
         return sweepDmg;
      }

      default PostAttackCooldown andThen(PostAttackCooldown hook) {
         return (t, c) -> hook.postAttackCooldown(postAttackCooldown(t, c), c);
      }
   }

   @FunctionalInterface
   public interface PostCritCalc extends AttackHook {
      static PostCritCalc identity() {
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
      default void onDamage(boolean isDamaged, AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb, AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity, AttackHookContext context) {
         return sweepDmg;
      }

      default PostCritCalc andThen(PostCritCalc hook) {
         return (t, c) -> hook.postCritical(postCritical(t, c), c);
      }
   }

   @FunctionalInterface
   public interface PostEnchantments extends AttackHook {
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
      default void onDamage(boolean isDamaged, AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb, AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity, AttackHookContext context) {
         return sweepDmg;
      }

      default PostEnchantments andThen(PostEnchantments hook) {
         return (t, c) -> hook.postEnchantments(postEnchantments(t, c), c);
      }
   }

   @FunctionalInterface
   public interface PostFireAspect extends AttackHook {
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
      default void onDamage(boolean isDamaged, AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb, AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity, AttackHookContext context) {
         return sweepDmg;
      }

      default PostFireAspect andThen(PostFireAspect hook) {
         return (t, c) -> hook.postFireAspect(postFireAspect(t, c), c);
      }
   }

   @FunctionalInterface
   public interface OnDamaged extends AttackHook {
      static OnDamaged identity() {
         return (d, c) -> {};
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
      default void postKnockback(float dmg, float kb, AttackHookContext context) {
      }

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity, AttackHookContext context) {
         return sweepDmg;
      }

      default OnDamaged andThen(OnDamaged hook) {
         return (d, c) -> {
            onDamage(d, c);
            hook.onDamage(d, c);
         };
      }
   }

   @FunctionalInterface
   public interface PostKnockback extends AttackHook {
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
      default void onDamage(boolean isDamaged, AttackHookContext context) {}

      @Override
      default float postSweeping(float sweepDmg, LivingEntity sweptEntity, AttackHookContext context) {
         return sweepDmg;
      }

      default PostKnockback andThen(PostKnockback hook) {
         return (d, k, c) -> {
            postKnockback(d, k, c);
            hook.postKnockback(d, k, c);
         };
      }
   }

   @FunctionalInterface
   public interface PostSweeping extends AttackHook {
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
      default void onDamage(boolean isDamaged, AttackHookContext context) {}

      @Override
      default void postKnockback(float dmg, float kb, AttackHookContext context) {}

      default PostSweeping andThen(PostSweeping hook) {
         return (d, e, c) -> hook.postSweeping(postSweeping(d, e, c), e, c);
      }
   }
}
