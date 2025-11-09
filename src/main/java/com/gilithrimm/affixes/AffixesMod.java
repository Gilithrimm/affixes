package com.gilithrimm.affixes;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.Affixes;
import com.gilithrimm.affixes.command.AffixCommand;
import com.gilithrimm.affixes.effects.Vulnerable;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class AffixesMod implements ModInitializer {
   public static final String MOD_ID = "affixes";
   public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

   /**
    * temporary since we don't yet have proper setup<br>
    * edit: temp my ass this stays here
    */
   public static final Set<Affix> AFFIX_LIST = new HashSet<>();

   public static final TagKey<Item> DAMAGE_AFFIX_APPLICABLE = TagKey.of(RegistryKeys.ITEM,
         new Identifier(MOD_ID, "damage_affix_applicable"));

   @Override
   public void onInitialize() {
      LOGGER.error(DAMAGE_AFFIX_APPLICABLE.toString());
      Registry.register(Registries.STATUS_EFFECT,
            new Identifier(MOD_ID, "vulnerable"),
            new Vulnerable(StatusEffectCategory.HARMFUL, Colors.RED));
      Affixes.init();
      AffixItems.init();
      AffixCommand.init();
   }
}
