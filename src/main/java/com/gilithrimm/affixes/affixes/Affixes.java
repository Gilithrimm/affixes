package com.gilithrimm.affixes.affixes;


import com.gilithrimm.affixes.hooks.AttackHookAdapter;
import com.gilithrimm.affixes.hooks.Hook;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gilithrimm.affixes.AffixesMod.AFFIX_LIST;
import static com.gilithrimm.affixes.AffixesMod.MOD_ID;
import static com.gilithrimm.affixes.hooks.AttackHooks.*;
import static net.minecraft.text.Text.translatable;

/*
Affixes ideas:
 * - Bleed You Dry: applies bleed +1 on hit entity with 50% chance
 *      (bleed: DoT, take damage equal to the amount of bleed stacks on you every 1s & decrease the stack by 50%)
 * - Striking the Weaknesses: applies Vulnerable I for 30s on 20% chance
 *      (Vulnerable: status effect, take % more damage based on lvl)
 * - Deal with Devil: Roulette: every 6th projectile hit deals 66% more damage, every 36th projectile hit deals 666% damage on 66% chance;
 *      all other projectiles deal 6.6% damage
 * - Sky Fall: increase dmg dealt by 1 per fallen block if falling from at least 50 blocks
 * - Marksman: increase dmg dealt by 1/5 of distance between you and the target
 * -
 */
public class Affixes {
   /**
    * Sharpened Blade: deal +5 dmg on hit, lower by 1 with each hit, resets after killing the enemy
    *
    * @implNote dmg mod value's doubled since function executes twice: once on client and once on server
    */
   public static final Affix SHARPENED_BLADE = register(new Identifier(MOD_ID, "sharpened_blade"), new PostAttribute() {
      private int damageModifier = 0;
      private Entity previous;

      @Override
      public float postAttribute(float total, AttackHookContext context) {
         if (!context.target().isAlive() || !context.target().equals(previous)) {
            damageModifier = 10;
         } else {
            damageModifier--;
         }
         previous = context.target();
         return total + (damageModifier >> 1);
      }

      @Override
      public List<Text> advancedTooltip() {
         List<Text> tooltip = new ArrayList<>();
         tooltip.add(translatable("affix.affixes.sharpened_blade.tooltip", damageModifier / 2));
         return tooltip;
      }
   });
   /**
    * <b>Learning from Pain:</b> increase dmg dealt by the (max health - current health)/2. If on 1hp, deal 4x more damage
    */
   public static final Affix LEARNING_FROM_PAIN = register(new Identifier(MOD_ID, "learning_from_pain"),
         new PostAttribute() {
            private float increase;

            @Override
            public float postAttribute(float total, AttackHookContext context) {
               float health = context.attacker().getHealth();
               increase = (context.attacker().getMaxHealth() - health) / 2;
               total += increase;
               if (health <= 1)//health is a float
               {
                  total *= 4;
               }
               return total;

            }

            @Override
            public List<Text> advancedTooltip() {
               List<Text> tooltip = new ArrayList<>();
               tooltip.add(translatable(
                     "affix.affixes.learning_from_pain.tooltip",
                     increase));
               return tooltip;
            }
         });
   /**
    * <b>Friday the 13th:</b> every 13th strike deals 130% damage
    *
    * @implNote counter doubled since function executes twice
    */
   public static final Affix FRIDAY_THE_13TH = register(new Identifier(MOD_ID, "friday_the_13th"),
         new PostEnchantments() {
            private int counter = 0;

            @Override
            public float postEnchantments(float total, AttackHookContext context) {
               if (counter == 25) {
                  counter = 0;
                  return total * 1.3F;
               } else {
                  counter++;
                  return total;
               }
            }

            @Override
            public List<Text> advancedTooltip() {
               List<Text> tooltips = new ArrayList<>();
               tooltips.add(translatable("affix.affixes.friday_the_13th.tooltip", (26 - counter) / 2));
               return tooltips;
            }
         });

   /**
    * <b>Grated Blade:</b> regens 2 hunger on each successful hit
    *
    * @implNote (successful hit := `target.damage()` returns true)
    */
   public static final Affix GRATED_BLADE = register(new Identifier(MOD_ID, "grated_blade"),
         (OnDamaged) (isDamaged, context) -> {
            if (isDamaged) {
               ((PlayerEntity) context.attacker()).getHungerManager().add(2, .2f);
               if (context.target().isOnFire()) {
                  ((PlayerEntity) context.attacker()).getHungerManager().add(4, .45f);
               }
            }
         });
   /**
    * <b>Healing Blade:</b> regens 2 hearts on each successful hit (both sides)
    */
   public static final Affix HEALING_BLADE = register(new Identifier(MOD_ID, "healing_blade"),
         (OnDamaged) (isDamaged, context) -> {
            if (isDamaged) {
               context.attacker().heal(4);
               ((LivingEntity) context.target()).heal(4);
            }
         });
   /**
    * Weathered Poison: 30% chance to apply Poison 1 for 5s on target with each hit, .3% chance to also apply on yourself
    */
   public static final Affix WEATHERED_POISON = register(new Identifier(MOD_ID, "weathered_poison"),
         (OnDamaged) (isDamaged, context) -> {
            if (isDamaged) {
               final int result = Random.createLocal().nextBetween(1, 1000);
               int poisonOdds = 300, poisonSelfOdds = 3;
               if (result <= poisonSelfOdds) {
                  context.attacker()
                         .addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 5 * 20), context.attacker());
               }
               if (result <= poisonOdds && context.target() instanceof LivingEntity entity) {
                  entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 5 * 20), context.attacker());
               }
            }
         });
   /**
    * Burning Passion: deal +2 dmg if you're on fire, deal +1 dmg on top if target's also on fire
    */
   public static final Affix BURNING_PASSION = register(new Identifier(MOD_ID, "burning_passion"),
         (PostAttribute) (total, context) -> {
            if (context.attacker().isOnFire()) {
               total += 2;
               if (context.target().isOnFire()) {
                  total += 1;
               }
            }
            return total;
         });

   /**
    * <b>Deal with Devil: Dice</b>: random events per hit
    * 0..(eff - 1)[eff:=amount of status effects]: apply random status effect to attacker (lvl 2 55s)
    * eff..(2 * eff - 1): apply random status effect to target (lvl 2 55s)
    * (2 * eff)..(2 * eff + aff - 1)[aff:=amount of affixes]: apply random affix effect
    */
   public static final Affix DEVIL_DIE = register(new Identifier(MOD_ID, "devil_die"), new AttackHookAdapter() {
      int event;
      StatusEffect[] statusEffects;
      int allEvents;
      /**'tis here to prevent doubles*/
      boolean didExecute = false;

      @Override
      public void setup() {
         if (!didExecute) {
            statusEffects = Registries.STATUS_EFFECT
                  .getEntrySet()
                  .stream()
                  .map(Map.Entry::getValue)
                  .toArray(StatusEffect[]::new);
            allEvents = statusEffects.length * 2;//doubled for attacker & target separately
            allEvents += AFFIX_LIST.size();
            event = Random.createLocal().nextInt(allEvents);
         }
         didExecute = !didExecute;
      }

      @Override
      public float postAttribute(float total, AttackHookContext context) {
         if (!didExecute) {
            int affixPosInList = event - 2 * statusEffects.length;

            if (event < statusEffects.length) {
               context.attacker().addStatusEffect(new StatusEffectInstance(statusEffects[event], 20 * 55, 2));
            } else if (event - statusEffects.length < statusEffects.length) {
               ((LivingEntity) (context.target())).addStatusEffect(
                     new StatusEffectInstance(statusEffects[event - statusEffects.length], 20 * 55, 2));
            } else if (affixPosInList < AFFIX_LIST.size()) {
               final Affix affix = AFFIX_LIST.toArray(new Affix[]{})[affixPosInList];
               final IAffixHolder mainHandStack = ((IAffixHolder) (Object) context.affixedTool());
               //this can trigger <=> (attacker's a living /\ attacker's main hand stack exists /\ attacker's main hand stack has >0 affixes bcuz THIS IS AN AFFIX)
               mainHandStack.getAffixes()
                            .stream()
                            .filter(//filter out this affix - can't have the 'fun' stop!
                                  aff -> !aff.idEquals(new Identifier(MOD_ID, "devil_die")))
                            .forEach(mainHandStack::removeAffix);
               mainHandStack.addAffix(affix);
            }
         }
         return total;
      }
   });
   /**
    * As The Day Ends: increase dmg depending on time of night (max 8 at midnight +- 11s)
    */
   public static final Affix AS_THE_DAY_ENDS = register(new Identifier(MOD_ID, "as_the_day_ends"),
         new PostAttribute() {
            float timeDmg;
            boolean proc = true;

            @Override
            public float postAttribute(float total, AttackHookContext context) {
               proc = !proc;
               if (!proc) {
                  timeDmg = f(context.world().getTimeOfDay());
                  return total + timeDmg;
               }
               return total;
            }

            public float f(long timeOfDay) {
               int r = 1100;//for the sake of convenience
               float x = (timeOfDay % 23500 - 12500.0f) / r;
               return x > 4.8 && x < 5.2
                      ? 8
                      : -x * x / 5 + 2 * x < 0
                        ? 0
                        : -x * x / 5 + 2 * x;
            }

            @Override
            public List<Text> advancedTooltip() {
               List<Text> tooltips = new ArrayList<>();
               tooltips.add(translatable("affix.affixes.as_the_day_ends.tooltip", timeDmg));
               return tooltips;
            }
         });

   //helper methods - todo shouldn't they be at `Affix`?
   public static Affix register(Identifier id, Hook hook) {
      Affix affix = new Affix(id, hook);
      AFFIX_LIST.add(affix);
      return affix;
   }

   public static Affix fromRegistry(Identifier id) {
      if (id == null) {
         return Affix.NO_AFFIX;
      }
      return AFFIX_LIST.stream()
                       .filter(affix -> affix.idEquals(id))
                       .findFirst()
                       .orElse(Affix.NO_AFFIX);
   }

   public static void init() {
      //stupid init order
   }
}