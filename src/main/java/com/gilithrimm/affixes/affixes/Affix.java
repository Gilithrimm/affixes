package com.gilithrimm.affixes.affixes;

import com.gilithrimm.affixes.hooks.AttackHookAdapter;
import com.gilithrimm.affixes.hooks.Hook;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.gilithrimm.affixes.AffixesMod.MOD_ID;

public record Affix(Identifier id, Hook hook) {
   public static final Affix NO_AFFIX = new Affix(new Identifier(MOD_ID, "no_affix"), new AttackHookAdapter());

   @Override
   public @NotNull String toString() {
      return id.toString();
   }

   public boolean idEquals(Identifier other) {
      return id.equals(other);
   }

   public @NotNull List<Text> tooltip(boolean isAdvanced) {
      List<Text> tooltip = new ArrayList<>();
      final String transDesc = id.toTranslationKey("affix", "desc");
      tooltip.add(translation(Formatting.DARK_RED));
      if (isAdvanced) {
         tooltip.add(Text.translatable(transDesc).formatted(Formatting.GRAY));
      }
      tooltip.addAll(hook.advancedTooltip()
                         .stream()
                         .map(t -> t
                               .copy()//clean cast ig
                               .formatted(Formatting.DARK_GRAY))
                         .toList());
      return tooltip;
   }

   public Text translation(Formatting... formattings) {
      return Text.translatable(id().toTranslationKey("affix")).formatted(formattings);
   }
}
