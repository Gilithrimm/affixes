package com.gilithrimm.affixes.affixes;


import com.gilithrimm.affixes.affixes.interfaces.IAffixable;
import com.gilithrimm.affixes.config.AffixConfig;
import com.gilithrimm.affixes.hooks.AttackHook;
import com.gilithrimm.affixes.hooks.AttackHookAdapter;
import com.gilithrimm.affixes.hooks.DamageHookAdapter;
import com.gilithrimm.affixes.hooks.DamageHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Map;

import static com.gilithrimm.affixes.AffixesMod.*;
import static com.gilithrimm.affixes.affixes.AffixRegistry.register;
import static com.gilithrimm.affixes.affixes.AffixRegistry.registrySize;
import static com.gilithrimm.affixes.affixes.ChargeManager.*;
import static com.gilithrimm.affixes.affixes.HunterMarkManager.*;
import static com.gilithrimm.affixes.builders.BuilderFactory.onChance;
import static com.gilithrimm.affixes.builders.BuilderFactory.statusEffect;
import static com.gilithrimm.affixes.config.AffixConfig.*;
import static com.gilithrimm.affixes.effects.AffixEffects.BLEED;
import static com.gilithrimm.affixes.effects.AffixEffects.VULNERABILITY;
import static com.gilithrimm.affixes.hooks.AttackHooks.*;
import static com.gilithrimm.affixes.hooks.DamageHooks.PostArmor;
import static java.lang.Math.max;
import static java.lang.Math.round;
import static net.minecraft.entity.effect.StatusEffects.*;
import static net.minecraft.text.Text.translatable;

/**
 * class grouping all affixes and affix registry related stuff
 *
 * @since without this class there wouldn't be anything
 */
/*
Affixes ideas:
 * - Deal with Devil - Roulette: every 6th projectile hit deals 66% more damage, every 36th projectile hit deals 666% damage on 66% chance;
 *      all other projectiles deal 6.6% damage
 * - Marksman: increase dmg dealt by 1/5 of distance between you and the target
 * - Defensive Stance
 * - Charge
 * - a way for Affixes to hold >=1 Hooks of different types (i.e. Attack Hook + Damage Hook)
 *   - adding hooks to entities at runtime
 *   - hooks that execute only n times
 *   - separate data per affix instance
 * - Deal with Devil - 7th Shot
 * - Deal with Devil - Immortality
 * -
 * -
 */
public class Affixes {
   /**
    * <b>Sharpened Blade:</b> deal +5 dmg on hit, lower by 1 with each hit, resets after killing the enemy
    */
   public static final Affix SHARPENED_BLADE = register(id("sharpened_blade"),
         new PostAttribute() {
            private int damageModifier = 0;
            private Entity previous;

            @Override
            public float postAttribute(float total, AttackHookContext context) {
               if (!context.target().isAlive() ||
                   !context.target().equals(previous)) {
                  damageModifier = 5;
               } else if (!context.world().isClient()) {
                  damageModifier--;
                  // if we exec this on server only we don't get tooltips on multi
                  // if we exec this on both we get -2 on single
               }
               previous = context.target();
               return total + damageModifier;
            }

            @Override
            public List<Text> advancedTooltip() {
               List<Text> tooltip = PostAttribute.super.advancedTooltip();
               tooltip.add(
                     translatable("affix.affixes.sharpened_blade.tooltip",
                           (damageModifier < 0) ? "" : "+", damageModifier));
               return tooltip;
            }
         });
   /**
    * <b>Learning from Pain:</b> increase dmg dealt by the (max health - current health)/2. If on &lt;1hp, deal 4x more damage
    */
   public static final Affix LEARNING_FROM_PAIN = register(
         id("learning_from_pain"),
         new PostAttribute() {
            private float increase;

            @Override
            public float postAttribute(float total, AttackHookContext context) {
               float health = context.attacker().getHealth();
               increase = (context.attacker().getMaxHealth() - health) / 2;
               total += increase;
               if (health <= 1)// health is a float
               {
                  total *= 4;
               }
               return total;

            }

            @Override
            public List<Text> advancedTooltip() {
               List<Text> tooltip = PostAttribute.super.advancedTooltip();
               tooltip.add(
                     translatable("affix.affixes.learning_from_pain.tooltip", increase));
               return tooltip;
            }
         });
   /**
    * <b>Friday the 13th:</b> every 13th strike deals 130% damage
    */
   public static final Affix FRIDAY_THE_13TH = register(id("friday_the_13th"),
         new PostEnchantments() {
            private int counter = 0;

            @Override
            public float postEnchantments(float total,
                                          AttackHookContext context) {
               if (!context.world().isClient()) {
                  if (counter == 13) {
                     counter = 0;
                     return total * 1.3F;
                  } else {
                     counter++;
                     return total;
                  }
               }
               return total;
            }

            @Override
            public List<Text> advancedTooltip() {
               List<Text> tooltips = PostEnchantments.super.advancedTooltip();
               tooltips.add(
                     translatable("affix.affixes.friday_the_13th.tooltip",
                           13 - counter));
               return tooltips;
            }
         });
   /**
    * <b>Chef's Knife:</b> regens 2 hunger on each successful hit ({@code target.damage} returns {@code true}).
    * {@link AttackHook#shouldTriggerWhenSweeping() Counts sweeping attacks.}
    */
   public static final Affix CHEFS_KNIFE = register(id("chefs_knife"),
         new PostHit() {
            @Override
            public void postHit(boolean isHit, float damage,
                                AttackHookContext context) {
               if (isHit) {
                  ((PlayerEntity) context.attacker()).getHungerManager()
                                                     .add(2, .2f);
                  if (context.target().isOnFire()) {
                     ((PlayerEntity) context.attacker()).getHungerManager()
                                                        .add(4, .45f);
                  }
               }
            }

            @Override
            public boolean shouldTriggerWhenSweeping() {
               return true;
            }
         });
   /**
    * <b>Healing Blade:</b> regens 2 hearts on each successful hit (both sides)
    */
   public static final Affix HEALING_BLADE = register(id("healing_blade"),
         (PostHit) (isHit, damage, context) -> {
            if (isHit) {
               context.attacker().heal(4);
               ((LivingEntity) context.target()).heal(4);
            }
         });
   /**
    * <b>Weathered Poison:</b> 30% chance to apply Poison 1 for 12s on target with each hit,
    * {@link AffixConfig#wpSelfPoisonChance % as defined in the config} chance to also apply on yourself
    */
   public static final Affix WEATHERED_POISON = register(id("weathered_poison"),
         (PostHit) (isHit, damage, context) -> {
            if (isHit) {
               final int result = RANDOM.nextBetween(1, 1000);
               int poisonOdds = 300;
               int duration = 12;
               if (result <= wpSelfPoisonChance ||
                   debugMode) {
                  context.attacker()
                         .addStatusEffect(
                               new StatusEffectInstance(POISON,
                                     duration * 20),
                               context.attacker());
               }
               if ((result <= poisonOdds || debugMode) &&
                   context.target() instanceof LivingEntity entity) {
                  entity.addStatusEffect(
                        new StatusEffectInstance(POISON,
                              duration * 20),
                        context.attacker());
               }
            }
         });
   /**
    * <b>Burning Passion:</b> deal +3 dmg if you're on fire, deal +2 dmg on top if target's also on fire.
    * {@link AttackHook#shouldTriggerWhenSweeping() Counts sweeping attacks.}
    */
   public static final Affix BURNING_PASSION = register(id("burning_passion"),
         new PostAttribute() {
            @Override
            public float postAttribute(float total, AttackHookContext context) {
               if (context.attacker().isOnFire()) {
                  total += 3;
                  if (context.target().isOnFire()) {
                     total += 2;
                  }
               }
               return total;
            }

            @Override
            public boolean shouldTriggerWhenSweeping() {
               return true;
            }
         });
   /**
    * <p><b>Deal with Devil - Dice</b>: random events per hit</p>
    * <b>Possible actions:</b><ul>
    * <li> apply random effect to attacker (lvl 3 duration 25s)</li>
    * <li> apply random effect to target (lvl 3 duration 25s)</li>
    * <li> add a random affix to this item</li>
    * <li> summon lightning at target</li>
    * <li> deal 5 generic damage</li>
    * <li> take 5 generic damage</li>
    * </ul>
    */
   public static final Affix DEVIL_DIE = register(id("devil_die"),
         new AttackHookAdapter() {
            final int singleEvents = 3;
            int randomEvent;
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
                  allEvents = statusEffects.length *
                              2;// doubled for attacker & target separately
                  allEvents += registrySize();
                  allEvents += singleEvents;
                  randomEvent = RANDOM.nextInt(allEvents);
               }
               didExecute = !didExecute;
            }

            @Override
            public float postAttribute(float total, AttackHookContext context) {
               if (!context.world().isClient) {
                  int affixPosInList = randomEvent - 2 * statusEffects.length;
                  int duration = 25;

                  if (randomEvent < statusEffects.length + singleEvents) {
                     context.attacker()
                            .addStatusEffect(
                                  new StatusEffectInstance(
                                        statusEffects[randomEvent],
                                        20 * duration, 2));
                  } else if (randomEvent - statusEffects.length <
                             statusEffects.length + singleEvents) {
                     ((LivingEntity) (context.target())).addStatusEffect(
                           new StatusEffectInstance(
                                 statusEffects[randomEvent
                                               - statusEffects.length],
                                 20 * duration, 2));
                  } else if (affixPosInList < registrySize() + singleEvents) {
                     final Affix affix = AffixRegistry.get(affixPosInList);
                     final IAffixable mainHandStack = ((IAffixable) (Object) context.affixedTool());
                     // this can trigger <=> (attacker's a living && attacker's main hand stack exists && attacker's main hand stack has >0 affixes bcuz THIS IS AN AFFIX)
                     mainHandStack.affixes$getAffixes()
                                  .stream()
                                  .filter(aff -> !aff.equals(id("devil_die")))
                                  .forEach(mainHandStack::affixes$removeAffix);
                     mainHandStack.affixes$addAffix(affix);
                  }
               }
               return total;
            }

            @Override
            public void postHit(boolean isHit,
                                float damage, AttackHookContext context) {
               switch (randomEvent) {
                  case 0 -> {
                     if (!context.world().isClient) {
                        EntityType.LIGHTNING_BOLT.spawn(
                              ((ServerWorld) context.world()),
                              context.target().getBlockPos(),
                              SpawnReason.EVENT);
                     }
                  }
                  case 1 -> {
                     if (context.target().isAlive()) {
                        context.target()
                               .damage(context.world()
                                              .getDamageSources()
                                              .generic(), 5);
                     }
                  }
                  case 3 -> context.attacker()
                                   .damage(context.world()
                                                  .getDamageSources()
                                                  .generic(), 5);
               }
            }
         });
   /**
    * <b>As The Day Ends:</b> increase dmg depending on time of night (max 8 at midnight +- 11s)
    */
   public static final Affix AS_THE_DAY_ENDS = register(id("as_the_day_ends"),
         new PostAttribute() {
            float timeDmg;

            @Override
            public float postAttribute(float total, AttackHookContext context) {
               timeDmg = timeBonus(context.world().getTimeOfDay());
               return total + timeDmg;
            }

            @Contract(pure = true)
            public float timeBonus(long timeOfDay) {
               int scale = 1100;// 1/10th of the nighttime - scales tick time into 0..10 scale
               int dayLength = 23500;
               float nightOffset = 12500.0f;
               float x = (timeOfDay % dayLength - nightOffset) / scale;
               int bonusDamage = 8;
               float formula = -x * x / 5 + 2 * x;
               boolean inBonusBounds = x > 4.8 && x < 5.2;
               boolean inBounds = !(x < 0 || x > 10);
               return inBonusBounds
                      ? bonusDamage
                      : inBounds
                        ? formula
                        : 0;
            }

            @Override
            public List<Text> advancedTooltip() {
               List<Text> tooltips = PostAttribute.super.advancedTooltip();
               tooltips.add(
                     translatable("affix.affixes.as_the_day_ends.tooltip",
                           timeDmg));
               return tooltips;
            }
         });
   /**
    * <b>Hunter's Mark:</b> marks an entity. Marked entity takes 2x more dmg.
    * If any other entity kills the marked entity, it gets marked.
    * You can only mark one entity at a time.
    */
   public static final Affix HUNTER_MARK = register(id("hunter_mark"),
         new AttackHookAdapter() {
            LivingEntity marked;

            @Override
            public float postEnchantments(float total,
                                          AttackHookContext context) {
               if (isMarked(context)) {
                  total *= hmMultiplier;
               }
               return total;
            }

            // check if target and marked are the one and the same + guardrails upon guardrails
            private boolean isMarked(AttackHookContext context) {
               return marked != null
                      && marked.isAlive()
                      && context.target().isAlive()
                      && context.target() instanceof LivingEntity target
                      && isMarkedBy(target, context.attacker())
                      && target.equals(marked);
            }

            // multiple entities marked by the same fucking guy at THE SAME TIME
            // fixed: reimplementing via AED automagically fixed this
            // todo: tell hunter that the mark was changed
            @Override
            public void postHit(boolean isHit,
                                float damage, AttackHookContext context) {
               LOGGER.info("hunter hit");
               if (context.target() instanceof LivingEntity target) {
                  LOGGER.info("hunter living");
                  if (!isMarked(context)) {
                     LOGGER.info("hunter marked");
                     if (!HunterMarkManager.isMarked(target)) {
                        LOGGER.info("hunter marked by {}",// this one
                              target.getName().getString());
                        marked = target;
                        setHunter(target, context.attacker());
                     }
                  } else if (!isMarkedBy(target, context.attacker())
                             && isHit
                             && target.isDead()) {
                     setHunter(context.attacker(),
                           (LivingEntity) getHunter(target));
                     marked = context.attacker();
                  }

               }
            }

            @Override
            public List<Text> advancedTooltip() {
               List<Text> texts = super.advancedTooltip();
               texts.add(translatable("affix.affixes.hunter_mark.tooltip",
                     marked == null ? "none" : marked.getName().getString()));
               return texts;
            }
         });
   /**
    * <b>Striking the Weaknesses:</b> applies Vulnerability I for 30s on 20% chance or
    * Vulnerability II for 45s at 35% chance if target has Bleed
    */
   public static final Affix STRIKING_THE_WEAKNESSES = register(
         id("striking_the_weaknesses"),
         (PostHit) (isHit, damage, context) -> {
            if (isHit && context.target() instanceof LivingEntity entity) {
               final int result = RANDOM.nextBetween(1, 20);
               if (entity.hasStatusEffect(BLEED)) {
                  int odds = 7;
                  if (result <= odds || debugMode) {
                     // amplifier := intended lvl - 1
                     entity.addStatusEffect(
                           new StatusEffectInstance(VULNERABILITY, 20 * 45, 1),
                           context.attacker());
                  }
               } else {
                  int odds = 4;
                  if (result <= odds || debugMode) {
                     entity.addStatusEffect(
                           new StatusEffectInstance(VULNERABILITY, 20 * 30),
                           context.attacker());
                  }
               }
            }
         });
   /**
    * <b>Puncturing Wounds:</b><br>
    * if target has Vulnerability &amp; Bleed: increases lvl by 1 &amp; decreases duration by 10% at 20% chance<br>
    * if target has Vulnerability &amp; no Bleed: applies Bleed II for 60s at 40% chance<br>
    * if target has Bleed: increases lvl by 1 &amp; decreases duration by 20% at 15% chance<br>
    * if target has no Bleed: applies Bleed I for 45s at 30% chance<br>
    */
   public static final Affix PUNCTURING_WOUNDS = register(
         id("puncturing_wounds"),// hehe 3556 dmg per hit
         (PostHit) (isHit, damage, c) -> {
            if (isHit && c.target() instanceof LivingEntity entity) {
               final int result = RANDOM.nextBetween(1, 20);
               if (entity.hasStatusEffect(VULNERABILITY)) {
                  if (entity.hasStatusEffect(BLEED)) {
                     int odds = 4;
                     if (result <= odds || debugMode) {
                        StatusEffectInstance statusEffect = entity.getStatusEffect(
                              BLEED);
                        final int duration = statusEffect.mapDuration(
                              d -> d - d / 10);
                        final int amp = statusEffect.getAmplifier() + 1;
                        entity.addStatusEffect(
                              new StatusEffectInstance(BLEED, duration, amp),
                              c.attacker());
                     }
                  } else {
                     int odds = 8;
                     if (result <= odds || debugMode) {
                        entity.addStatusEffect(
                              new StatusEffectInstance(BLEED, 20 * 60, 1),
                              c.attacker());
                     }
                  }
               } else {
                  if (entity.hasStatusEffect(BLEED)) {
                     int odds = 3;
                     if (result <= odds || debugMode) {
                        StatusEffectInstance statusEffect = entity.getStatusEffect(
                              BLEED);
                        final int duration = statusEffect.mapDuration(
                              d -> d - d / 5);
                        final int amp = statusEffect.getAmplifier() + 1;
                        entity.addStatusEffect(
                              new StatusEffectInstance(BLEED, duration, amp),
                              c.attacker());
                     }
                  } else {
                     int odds = 6;
                     if (result <= odds || debugMode) {
                        entity.addStatusEffect(
                              new StatusEffectInstance(BLEED, 20 * 45),
                              c.attacker());
                     }
                  }
               }
            }
         });
   /**
    * <b>Sky Fall:</b> increase dmg dealt by 1 per fallen block above 50 blocks
    */
   public static final Affix SKY_FALL = register(id("sky_fall"),
         (PostAttribute) (total, c) ->
               total + (!c.world().isClient() && c.attacker().fallDistance >= 50
                        ? c.attacker().fallDistance - 50
                        : 0));
   /**
    * <b>Fire Wall:</b> decreases damage taken by 40% if victim's on fire
    */
   public static final Affix FIRE_WALL = register(id("firewall"),
         (PostArmor) (c, amount) ->
               amount * (c.victim().isOnFire() ? .6f : 1));
   /**
    * <b>Boundary Of Death:</b> every 4th hit add 1-4 dmg to hit; if it rolls 4 then increase dmg by
    * {@link AffixConfig#bodDamageMultiplier % defined in config} and reroll dmg.
    */
   public static final Affix BOUNDARY_OF_DEATH = register(
         id("boundary_of_death"),
         new PostAttribute() {
            int counter = 1;

            @Override
            public float postAttribute(float total, AttackHookContext context) {
               if (!context.world().isClient()) {
                  if (counter == 4) {
                     counter = 1;
                     total = boundary(total);
                  } else {
                     counter++;
                  }
               }
               return total;
            }

            private float boundary(float total) {
               float x = total;
               int inc = RANDOM.nextBetween(1, 4);
               x += inc;
               if (inc == 4) {
                  x = boundary(x);
                  x *= bodDamageMultiplier;
               }
               return x;
            }

            @Override
            public List<Text> advancedTooltip() {
               final List<Text> tooltip = PostAttribute.super.advancedTooltip();
               tooltip.add(translatable(
                     "affix.affixes.boundary_of_death.tooltip", 4 - counter));
               return tooltip;
            }
         });
   /**
    * <b>Defensive Stance:</b> (x/5)% chance to apply Absorption III for 60s and Strength I for 50s to self
    * and apply Weakness II for 45s and Slowness I for 25s to the attacker
    */
   public static final Affix DEFENSIVE_STANCE = register(id("defensive_stance"),
         new DamageHookAdapter() {
            private int chance = 0;

            @Override
            public float postAbsorption(DamageHookContext context, float amount) {
               postDamage(context, amount);
               return amount;
            }

            @Override
            public void postDamage(DamageHookContext ctx, float amount) {
               onChance(500)
                     .debug()
                     .lucky(() -> {
                        if (ctx.victim() instanceof LivingEntity self) {
                           statusEffect(ABSORPTION)
                                 .amplifier(2)
                                 .duration(60 * 20)
                                 .apply(self);
                           statusEffect(STRENGTH)
                                 .duration(50 * 20)
                                 .apply(self);
                        }
                        if (ctx.source().getSource() instanceof LivingEntity attacker) {
                           statusEffect(WEAKNESS)
                                 .amplifier(1)
                                 .duration(45 * 20)
                                 .apply(attacker, ctx.victim());
                           statusEffect(SLOWNESS)
                                 .duration(25 * 20)
                                 .apply(attacker, ctx.victim());
                        }
                        chance /= 2;
                     })
                     .unlucky(() -> chance++)
                     .apply(chance);
            }

            @Override
            public List<Text> advancedTooltip() {
               List<Text> texts = super.advancedTooltip();
               texts.add(
                     translatable("affix.affixes.defensive_stance.tooltip",
                           chance / 5));
               return texts;
            }
         });
   /**
    * <b>Hard Skin:</b> for every 2hp dealt there's 25% chance to lower damage dealt by 1.5hp
    */
   public static final Affix HARD_SKIN = register(id("hard_skin"),
         (PostArmor) (context, amount) -> {
            float damage = amount;
            for (int i = 0; i <= (int) amount / 2; i++) {
               if (RANDOM.nextBetween(1, 4) == 4 || debugMode) {
                  damage -= 1.5f;
               }
            }
            return damage;
         });
   /**
    * <b>Pricky Armor:</b> applies Poison III for 5s and Bleed II for 30s to the attacker after being hit at 40% chance
    */
   public static final Affix PRICKY_ARMOR = register(id("pricky_armor"),
         (DamageHooks.PostDamage) (c, amount) -> {
            int result = RANDOM.nextBetween(1, 5);
            if (c.source().getAttacker() instanceof LivingEntity attacker) {
               int odds = 2;
               if (result <= odds || debugMode) {
                  attacker.addStatusEffect(
                        new StatusEffectInstance(POISON, 20 * 5, 2));
                  attacker.addStatusEffect(
                        new StatusEffectInstance(BLEED, 20 * 30, 1));
               }
            }
         });
   /**
    * <b>Weakening Hit:</b> applies Weakness I for 25s and Vulnerability I for 20s; also deal 10% more dmg
    */
   public static final Affix WEAKENING_HIT = register(id("weakening_hit"),
         (PostEnchantments) (total, context) -> {
            if (context.target() instanceof LivingEntity target) {
               target.addStatusEffect(
                     new StatusEffectInstance(WEAKNESS, 20 * 25),
                     context.attacker());
               target.addStatusEffect(
                     new StatusEffectInstance(VULNERABILITY, 20 * 20),
                     context.attacker());
            }
            return total * 1.1f;
         });
   /**
    * <b>Breakthrough:</b> triggers only on crits. multiplies damage by % based on enemy's armor prot
    */
   public static final Affix BREAKTHROUGH = register(id("breakthrough"),
         (PostCritical) (t, c) -> {
            var ref = new Object() {
               float sum = 0;
            };
            c.target().getArmorItems().forEach(is -> {
               if (is.getItem() instanceof ArmorItem armor) {
                  float prot = armor.getProtection() / 100f;
                  ref.sum += prot;
               }
            });
            t += t * ref.sum;
            return t;
         });
   /**
    * <b>Blood Marks:</b> Inflict Bleed II for 20s if victim is marked by the attacker
    */
   public static final Affix BLOOD_MARKS = register(id("blood_marks"),
         (PostHit) (isHit, damage, context) -> {
            if (isHit
                && context.target() instanceof LivingEntity target
                && (isMarkedBy(target, context.attacker()))) {
               LOGGER.info("thru affix data: {}",
                     isMarkedBy(target, context.attacker()));
               target.addStatusEffect(
                     new StatusEffectInstance(BLEED, 20 * 20, 1),
                     context.attacker());
            }
         });
   /**
    * <b>Mark Of The Beast:</b> inflict Strength II for 20s and reduce damage taken by 15% right after being marked
    */
   public static final Affix MARK_OF_THE_BEAST = register(
         id("mark_of_the_beast"),
         new DamageHookAdapter() {
            boolean triggered = false;

            @Override
            public float postArmor(DamageHookContext context, float amount) {
               return triggered ? amount * .85f : amount;
            }

            @Override
            public void postDamage(DamageHookContext context, float amount) {
               if (context.victim() instanceof LivingEntity target) {
                  if (!context.world().isClient()
                      && (isMarked(target))
                      && !triggered
                      && ((getHunter(target) != null
                           && getHunter(target).isAlive()))) {
                     triggered = true;
                     target.addStatusEffect(
                           new StatusEffectInstance(STRENGTH, 20 * 20, 1));
                  } else if (!isMarked(target)
                             || !getHunter(target).isAlive()) {
                     triggered = false;
                  }
               }
            }
         });
   /**
    * <b>Decaying Slash:</b> {on successful hit} inflicts Wither II for 20s
    * at chance depending on amount of enemies killed with this weapon.
    * {@link AttackHook#shouldTriggerWhenSweeping() Counts sweeping attacks.}
    */
   public static final Affix DECAYING_SLASH = register(id("decaying_slash"),
         new PostHit() {
            int corpses = 0;

            @Override
            public void postHit(boolean isHit, float damage,
                                AttackHookContext context) {
               if (context.affixedItemData().contains("corpses")) {
                  corpses = context.affixedItemData().getInt("corpses");
               }
               if (context.target() instanceof LivingEntity target) {
                  if (target.isDead()) {
                     corpses += 1;
                  } else if (isHit) {
                     final int result = RANDOM.nextBetween(1, 100);
                     if (result <= corpses) {
                        target.addStatusEffect(
                              new StatusEffectInstance(WITHER, 20 * 20, 1));
                     }
                     corpses = max(0, corpses - 1);
                  }
                  context.affixedItemData().putInt("corpses", corpses);
               }
            }

            @Override
            public boolean shouldTriggerWhenSweeping() {
               return true;
            }

            @Override
            public List<Text> advancedTooltip() {
               final List<Text> tooltip = PostHit.super.advancedTooltip();
               tooltip.add(translatable("affix.affixes.decaying_slash.tooltip", corpses));
               return tooltip;
            }
         });
   /**
    * <b>God Slayer:</b> 3x dmg if against a boss (in #c:bosses tag)
    */
   public static final Affix GODSLAYER = register(id("godslayer"),
         (PostEnchantments) (total, context) -> {
            var bossesTag = TagKey.of(RegistryKeys.ENTITY_TYPE,
                  new Identifier("c:bosses"));
            if (context.target().getType().isIn(bossesTag)) {
               return total * 3;
            }
            return total;
         });
   /**
    * <b>Barbed Shield:</b> reflects 15% damage blocked with shield; 20% chance to reflect 55% damage instead<br>
    * secret: .1% chance to inflict Bleed V for 5s
    */
   public static final Affix BARBED_SHIELD = register(id("barbed_shield"),
         (DamageHooks.PostShieldBlock) (context, amount) -> {
            final int result = RANDOM.nextBetween(1, 1000);
            final int odds = 200;
            if (result <= odds || debugMode) {
               context.source()
                      .getSource()
                      .damage(new DamageSource(
                            context.source().getTypeRegistryEntry(),
                            context.victim()), amount * .55f);
            } else {
               context.source()
                      .getSource()
                      .damage(new DamageSource(
                            context.source().getTypeRegistryEntry(),
                            context.victim()), amount * .15f);
            }
            final int secret = 1;
            if (result <= secret || debugMode) {
               if (context.source()
                          .getSource() instanceof LivingEntity attacker) {
                  attacker.addStatusEffect(
                        new StatusEffectInstance(BLEED, 20 * 5, 4));
               }
            }
         });
   /**
    * <b>Charging Strikes:</b> gives you 2 Charge with each successful hit.
    * {@link AttackHook#shouldTriggerWhenSweeping() Counts sweeping attacks.}
    */
   public static final Affix CHARGING_STRIKES = register(id("charging_strikes"),
         new PostHit() {
            int charge = 0;// todo server doesn't update the client

            @Override
            public void postHit(boolean isHit, float damage,
                                AttackHookContext context) {
               if (!context.world().isClient()) {
                  charge = getCharge(context.attacker());
                  if (isHit) {
                     charge += 2;
                     setCharge(context.attacker(), charge);
                  }
               }
            }

            @Override
            public boolean shouldTriggerWhenSweeping() {
               return true;
            }

            @Override
            public List<Text> advancedTooltip() {
               final List<Text> tooltip = PostHit.super.advancedTooltip();
               tooltip.add(
                     translatable("affix.affixes.charging_strikes.tooltip",
                           charge));
               return tooltip;
            }
         });
   /**
    * <b>Charged Slash:</b> use 100 Charge to increase damage dealt by 10% of enemy's health
    */
   public static final Affix CHARGED_SLASH = register(id("charged_slash"),
         new PostEnchantments() {
            int charge = 0;

            @Override
            public float postEnchantments(float total,
                                          AttackHookContext context) {
               charge = getCharge(context.attacker());
               if (context.target() instanceof LivingEntity target
                   && charge >= 100 && !context.world().isClient()) {
                  total += target.getHealth() * .1f;
                  charge -= 100;
                  setCharge(context.attacker(), charge);
               }
               return total;
            }

            @Override
            public List<Text> advancedTooltip() {
               final List<Text> tooltip = PostEnchantments.super.advancedTooltip();
               tooltip.add(
                     translatable("affix.affixes.charged_slash.tooltip",
                           charge));
               return tooltip;
            }
         });
   /**
    * <b>Techno Fort:</b> reduces damage taken by {@code 200 / victim's Charge} at the cost of 200 Charge. Can Overcharge.
    */
   public static final Affix TECHNO_FORT = register(id("techno_fort"),
         new PostArmor() {
            int charge = 0;

            @Override
            public float postArmor(DamageHookContext context, float amount) {
               if (context.victim() instanceof LivingEntity victim) {
                  charge = getCharge(victim);
                  if (charge > 300) {
                     if (!context.world().isClient()) {
                        amount *= (200f / charge);
                        int overCharge = (charge > 1000 ? charge / 100 : 0);
                        charge -= 200 + overCharge;
                        charge = max(charge, 0);
                        addCharge(victim, -200 - overCharge);
                        var source = (LivingEntity) context.source()
                                                           .getSource();
                        if (source != null) {
                           addCharge(source, 15 + overCharge);
                        }
                     }
                  }
               }
               return amount;
            }

            @Override
            public List<Text> advancedTooltip() {
               final List<Text> tooltip = PostArmor.super.advancedTooltip();
               tooltip.add(
                     translatable("affix.affixes.techno_fort.tooltip", charge));
               return tooltip;
            }
         });
   /**
    * <b>Burst Recharge:</b> Charges your weapons by {@code damage taken * 100 / max health} every time you get hit
    *
    * @implNote you don't get any Charge when you die - I still don't know why exactly
    */
   public static final Affix BURST_RECHARGE = register(id("burst_recharge"),
         (DamageHooks.PostDamage) (context, amount) -> {
            if (!context.world().isClient()) {
               LivingEntity victim = (LivingEntity) context.victim();
               int charge = getCharge(victim);
               charge += round(amount * 100 / victim.getMaxHealth());
               setCharge(victim, charge);
            }
         });
   /**
    * <b>Shorted Heart:</b> heals you 35% of damage taken at the cost of 400 Charge
    */
   public static final Affix SHORTED_HEART = register(id("shorted_heart"),
         (DamageHooks.PostDamage) (context, amount) -> {
            if (amount > 0) {
               if (!context.world().isClient()) {
                  LivingEntity victim = (LivingEntity) context.victim();
                  int charge = getCharge(victim);
                  if (charge > 500) {
                     addCharge(victim, -400);
                     victim.heal(amount * 35f / 100f);
                  }
               }
            }
         });
   /**
    * <b>Discharge:</b> {@code Charge / 100}% chance to multiply damage dealt by 10
    * (only if Charge &gt; 1000); spends almost all Charge no matter the result<br>
    * Also creates a small explosion because these affixes need some visuals they need to be more interesting
    * todo MORE SHIT ON THE SCREEN
    */
   public static final Affix DISCHARGE = register(id("discharge"),
         (PostEnchantments) (total, context) -> {
            final int charge = getCharge(context.attacker());
            if (!context.world().isClient()) {
               if (charge > 1000) {
                  int odds = charge / 100;
                  if (RANDOM.nextBetween(1, 100) <= odds) {
                     total *= 10;
                     context.world()
                            .createExplosion(null,
                                  null,
                                  new ExplosionBehavior(),
                                  context.target().getPos(),
                                  2.22f,
                                  false,
                                  World.ExplosionSourceType.NONE);
                  }
                  setCharge(context.attacker(), charge % 100);
               }
            }
            return total;
         });
   /**
    * <b>Chargeback:</b> takes away 10% of target's Charge and increases your Charge by 10%
    */
   public static final Affix CHARGEBACK = register(id("chargeback"),
         (PostHit) (isHit, damage, context) -> {
            if (!context.world().isClient()
                && isHit
                && context.target() instanceof LivingEntity target
                && getCharge(target) > 0) {
               setCharge(target, round(getCharge(target) * .9f));
               setCharge(context.attacker(),
                     round(getCharge(context.attacker()) * 1.1f));
            }
         });
   /**
    * <b>Charge Out:</b> multiplies damage dealt by {@code target's Charge/attacker's Charge}, then
    * gives 150 Charge to the target (200 if they have none) &amp; takes away 250 of attacker's Charge
    */
   public static final Affix CHARGE_OUT = register(id("charge_out"),
         new AttackHookAdapter() {
            @Override
            public float postAttackCooldown(float t,
                                            AttackHookContext context) {
               if (context.target() instanceof LivingEntity target) {
                  final LivingEntity attacker = context.attacker();
                  float targetCharge = (getCharge(target) + 1f);// to keep f(t)≥t
                  float attackerCharge = (getCharge(attacker) + 1f);// no a/0
                  return t * (targetCharge / attackerCharge);
               }
               return t;
            }

            @Override
            public void postHit(boolean isHit, float damage,
                                AttackHookContext ctx) {
               if (isHit
                   && !ctx.world().isClient()
                   && ctx.target() instanceof LivingEntity target
                   && getCharge(ctx.attacker()) >= 250) {
                  if (getCharge(target) > 0) {
                     setCharge(target, getCharge(target) + 150);
                  } else {
                     setCharge(target, 200);
                  }
                  setCharge(ctx.attacker(),
                        getCharge(ctx.attacker()) - 250);
               }
            }
         });
   /**
    * todo do sth here
    */
   public static final Affix CHAOS_CHARGE = register(id("chaos_charge"),
         new PostHit() {
            @Override
            public void postHit(boolean isHit, float damage, AttackHookContext context) {
               if (isHit) {
                  // take away target's charge & do sth w/ it???
               }
            }
         });
   /**
    * <b>Strike of Charge:</b> summons lightning at the enemy at the cost of 1250 Charge.
    * 4.55% chance to summon the lightning at the attacker instead.
    */
   public static final Affix STRIKE_OF_CHARGE = register(id("strike_of_charge"),
         (PostHit) (isHit, damage, ctx) -> {
            if (isHit
                && !ctx.world().isClient()// this thingy happens 4-6 times
                && getCharge(ctx.attacker()) > 1300) {
               onChance(1000)
                     .noDebug()
                     .lucky(() -> EntityType.LIGHTNING_BOLT.spawn(
                           (ServerWorld) ctx.world(),
                           ctx.attacker().getBlockPos(), SpawnReason.EVENT))
                     .unlucky(() -> EntityType.LIGHTNING_BOLT.spawn(
                           (ServerWorld) ctx.target().getWorld(),
                           ctx.target().getBlockPos(), SpawnReason.EVENT))
                     .apply(socSelfLightningChance);
               // final int chance = RANDOM.nextBetween(1, 10000);
               // if (chance <= socSelfLightningChance) {
               //    EntityType.LIGHTNING_BOLT.spawn((ServerWorld) ctx.world(),
               //          ctx.attacker().getBlockPos(), SpawnReason.EVENT);
               // } else {
               //    EntityType.LIGHTNING_BOLT.spawn((ServerWorld) ctx.target().getWorld(),
               //          ctx.target().getBlockPos(), SpawnReason.EVENT);
               // }
               setCharge(ctx.attacker(), getCharge(ctx.attacker()) - 1250);
            }
         });
   /**
    * <b>Charged Pixies:</b> summons Vexes for 60s that follow the player that summoned them
    */
   public static final Affix CHARGED_PIXIES = register(id("charged_pixies"),
         (DamageHooks.PostDamage) (ctx, damage) -> {
            if (ctx.victim() instanceof LivingEntity victim
                && !victim.getWorld().isClient()) {
               final int chance = RANDOM.nextBetween(1, 1000);
               if (chance <= 123 || debugMode) {
                  for (int i = 0; i < 3; i++) {
                     if (getCharge(victim) > 150) {
                        var blockPos = victim.getBlockPos()
                                             .add(-2 + RANDOM.nextInt(5),
                                                   1,
                                                   -2 + RANDOM.nextInt(5));
                        var vex = EntityType.VEX.create(victim.getWorld());
                        if (vex != null) {
                           vex.refreshPositionAndAngles(blockPos, 0.0f, 0.0f);
                           vex.initialize(
                                 ((ServerWorld) victim.getWorld()),
                                 victim.getWorld().getLocalDifficulty(blockPos),
                                 SpawnReason.MOB_SUMMONED, null, null);
                           // this WILL crash
                           // edit: this DID crash
                           // we need this tho or a way to do this
//                           vex.setOwner((MobEntity) victim);
                           vex.setLifeTicks(20 * 60);
                           vex.setBounds(blockPos);
                           vex.setCustomName(Text.literal("Charged Pixie"));
                           vex.setHealth(10f);
                           vex.setAttacker(
                                 (LivingEntity) ctx.source().getAttacker());
                           // todo pixie no hurt victim
                           ((ServerWorld) victim.getWorld())
                                 .spawnEntityAndPassengers(vex);
                           setCharge(victim, getCharge(victim) - 150);
                        }
                     }
                  }
               }
            }
         });
   /**
    * <b>Blood Rage:</b> multiplies damage dealt by the {.1 * lvl of Bleeding on the attacker}
    */
   public static final Affix BLOOD_RAGE = register(id("blood_rage"),
         (PostAttackCooldown) (total, ctx) -> total * (ctx.attacker()
                                                          .hasStatusEffect(BLEED)
                                                       ? (ctx.attacker()
                                                             .getStatusEffect(BLEED)
                                                             .getAmplifier() + 11) / 10f
                                                       : 1f));

   /**
    * <b>No Weaknesses!:</b> gives the attacker Speed II for 2 min and Regen I for 30s if they have Weakness
    * also gives target Slowness II for 45s under the same condition
    */
   public static final Affix NO_WEAKNESSES = register(id("no_weaknesses"),
         (PostHit) (isHit, damage, ctx) -> {
            if (isHit
                && !ctx.world().isClient()
                && ctx.attacker().hasStatusEffect(WEAKNESS)) {
               ctx.attacker()
                  .addStatusEffect(new StatusEffectInstance(SPEED, 20 * 120, 1));
               ctx.attacker()
                  .addStatusEffect(new StatusEffectInstance(REGENERATION, 20 * 30));
               if (ctx.target() instanceof LivingEntity target) {
                  target.addStatusEffect(new StatusEffectInstance(SLOWNESS, 20 * 45, 1));
               }
            }
         });
   /**
    * <b>Neutralizer:</b> if the attack crits and hits, removes a random effect from the target.
    * 50% chance it removes the effect from the attacker instead
    */
   public static final Affix NEUTRALIZER = register(id("neutralizer"),
         new AttackHookAdapter() {
            private boolean crit = false;

            @Override
            public float postCritical(float total, AttackHookContext context) {
               crit = true;
               return total;
            }

            @Override
            public void postHit(boolean isHit, float damage, AttackHookContext context) {
               if (crit && isHit) {
                  final LivingEntity[] affixTarget = {context.attacker()};// this is so stupid
                  if (context.target() instanceof LivingEntity target) {
                     onChance(10)
                           .noDebug()
                           .lucky(() -> affixTarget[0] = target)
                           .apply(5);
                  }
                  var effects = affixTarget[0].getStatusEffects()
                                              .stream()
                                              .map(StatusEffectInstance::getEffectType)
                                              .toList();
                  if (!effects.isEmpty()) {
                     affixTarget[0].removeStatusEffect(
                           effects.get(RANDOM.nextInt(effects.size())));
                  }
                  crit = false;
               }
            }
         });

   // todo more effect-afflicting affixes (alchemist build lmao)
   // todo armor benefitting from charge
   // todo overcharge as an actual mechanic, extracted from affixes with charge
   // todo affixes that work on/benefit from enemy having charge
   // todo do cool stuff with charge basically

   // todo affixData does not get copied after player death

   // todo make data in affixes its own api/encapsulated thing
   //  maybe as part of HookContext???

   /*
   currently we have 3 options for custom affix data:
   1. data on entity - persistent, affix-independent, item-independent
   2. data on item - persistent, affix-independent, entity-independent
   3. data on affix - synced between copies of the same affix (fixme!)
   Problem 1: how do we make 3. desync???
   Problem 2: client-server desync on all data types (fixme!)
   */

   private Affixes() {}

   /**
    * calling this function forces all the static fields to init which is the only reason it exists at all
    */
   public static void init() {
      // stupid init order
   }
}