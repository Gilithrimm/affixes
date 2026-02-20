package com.gilithrimm.affixes.hooks;

//todo fix signatures
// also make mixins
public interface ProjectileHitHook extends Hook {
   float postVelocity(float velocity);

   float postDamage(float velocity);

   float postPiercing(float velocity);

   float postCritical(float velocity);

   float postFire(float velocity);

   float onDamage(float velocity);

   float postPunch(float velocity);

   float postArrowEffects(float velocity);

   float postChanneling(float velocity);
}
