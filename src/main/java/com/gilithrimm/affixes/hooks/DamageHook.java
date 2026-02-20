package com.gilithrimm.affixes.hooks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Functions hooked into {@link LivingEntity#damage(DamageSource, float) damage()} function (and adjacent)
 */
public interface DamageHook extends Hook {
   /**
    * Called when damage was successfully blocked by shield
    *
    * @param context damage context, i.e. attacker, victim, damage type etc.
    * @param amount  amount of damage that would be taken by victim instead.
    * @apiNote This is 1st step in calculating damage taken.
    */
   void postShieldBlock(DamageHookContext context, float amount);

   /**
    * Called after calculating how much damage was blocked by the armor
    *
    * @param context damage context, i.e. attacker, victim, damage type etc.
    * @param amount  total damage calculated so far
    * @return total damage after this hook
    * @apiNote This is 2nd step in calculating damage taken.
    */
   float postArmor(DamageHookContext context, float amount);

   /**
    * Called after calculating how much did damage change from status effects (like
    * <a href="https://minecraft.wiki/w/Resistance">Resistance</a> and
    * {@link com.gilithrimm.affixes.effects.Vulnerability Vulnerability})
    *
    * @param context damage context, i.e. attacker, victim, damage type etc.
    * @param amount  total damage calculated so far
    * @return total damage after this hook
    * @apiNote This is 3rd step in calculating damage taken.
    * @implNote This is called only if the entity has Resistance
    */
   float postResistance(DamageHookContext context, float amount);

   /**
    * Called after calculating how much damage was reduced by <a href="https://minecraft.wiki/w/Protection">Protection enchantments</a>
    *
    * @param context    damage context, i.e. attacker, victim, damage type etc.
    * @param amount     total damage calculated so far
    * @param protection total <a href="https://minecraft.wiki/w/Armor#Enchantments">Protection value</a>
    * @return total damage after this hook
    * @apiNote This is 4th step in calculating damage taken.
    * @implNote This is called only if damage after previous steps is above 0 and there are enchantments reducing damage on any equipped armor
    */
   float postProtection(DamageHookContext context, float amount,
                        float protection);

   /**
    * Called after reducing damage by health given by <a href="https://minecraft.wiki/w/Absorption">Absorption</a> status effect
    *
    * @param context damage context, i.e. attacker, victim, damage type etc.
    * @param amount  total damage calculated so far
    * @return total damage after this hook
    * @apiNote This is last step in calculating damage taken.
    */
   float postAbsorption(DamageHookContext context, float amount);

   /**
    * Called right after damaging the victim.
    *
    * @param context damage context, i.e. attacker, victim, damage type etc.
    * @param amount  damage taken by this entity
    * @implNote This is only called if there was any damage left to apply after absorption
    */
   void postDamage(DamageHookContext context, float amount);

   /**
    * Called after applying knockback to this entity
    *
    * @param context damage context, i.e. attacker, victim, damage type etc.
    * @param amount  amount of damage dealt to this entity
    */
   void postKnockback(DamageHookContext context, float amount);

   /**
    * Called after this entity died
    *
    * @param context damage context, i.e. attacker, victim, damage type etc.
    * @param amount  amount of damage that killed this entity
    * @implNote Called only if this entity died.
    */
   void postDeath(DamageHookContext context, float amount);

   /**
    * context needed by damage hooks.
    *
    * @param source source of the damage
    * @param victim the entity that got hit
    */
   record DamageHookContext(DamageSource source, Entity victim) {
      /**
       * tool used to damage this entity
       *
       * @return tool used to damage this entity
       */
      public ItemStack damageTool() {
         if (source.getAttacker() instanceof LivingEntity entity) {
            return entity.getMainHandStack();
         }
         if (source.getSource() instanceof LivingEntity entity) {
            return entity.getMainHandStack();
         }
         return ItemStack.EMPTY;
      }

      /**
       * world that this entity got damaged in
       *
       * @return world that this entity got damaged in
       */
      public World world() {
         return victim.getWorld();
      }
   }
}
