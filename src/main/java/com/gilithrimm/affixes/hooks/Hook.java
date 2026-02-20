package com.gilithrimm.affixes.hooks;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Top-level interface of all hooks - points in Minecraft code where you can inject your own behaviour
 */
public interface Hook {

   /**
    * Tooltips with information specific to this hook (i.e. hits left before bonus happens or entity marked by the
    * {@link com.gilithrimm.affixes.affixes.Affixes#HUNTER_MARK Hunter's Mark})
    *
    * @return list of tooltips
    */
   default List<Text> advancedTooltip() {
      return new ArrayList<>();
   }

   /**
    * Called right at the beginning of an event (damage, attack or whatever you mixin)
    */
   default void setup() {}

   /**
    * should this affix be creative only, or should this be available in survival?
    *
    * @return should this affix be creative only, or should this be available in survival?
    */
   default boolean isCreativeOnly() {
      return false;
   }
}
