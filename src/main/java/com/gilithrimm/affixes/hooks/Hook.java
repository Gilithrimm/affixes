package com.gilithrimm.affixes.hooks;

import net.minecraft.text.Text;

import java.util.List;

public interface Hook {
   default List<Text> advancedTooltip() {
      return List.of();
   }

   default void setup() {}

//   commented out cos instanceof exists
//   HookType type();
//
//   enum HookType {
//      ATTACK,
//      DAMAGE,
//      PROJECTILE,
//      ITEM
//   }
}
