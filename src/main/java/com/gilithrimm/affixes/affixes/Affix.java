package com.gilithrimm.affixes.affixes;

import com.gilithrimm.affixes.AffixesMod;
import com.gilithrimm.affixes.hooks.Hook;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.gilithrimm.affixes.config.AffixConfig.showAdditionalInfo;
import static com.gilithrimm.affixes.config.AffixConfig.showDescriptions;

/**
 * An affix is essentially a piece of code with an {@link Identifier identifier} attached
 *
 * @param id   unique identifier
 * @param hook the trigger or the 'piece of code' as mentioned before (see {@link Hook})
 * @since the beginning of God, the Universe and all the other silly stuff
 */
public record Affix(Identifier id, Hook hook) {
   /**
    * Debug Affix - indicates errors during loading of an affix (i.e. id with no affix mapped)
    */
   public static final Affix NO_AFFIX = new Affix(AffixesMod.id("no_affix"),
         new Hook() {});

   /**
    * Indicates whether some other object is "equal to" this one by comparing
    * the Identifiers (if the provided object is instance of {@link Affix} or {@link Identifier}).
    *
    * @param o the reference object with which to compare.
    * @return {@inheritDoc}
    */
   @Override
   public boolean equals(Object o) {
      if (o instanceof Identifier oid) return id.equals(oid);
      if (!(o instanceof Affix affix)) return false;
      return Objects.equals(id, affix.id);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public @NotNull String toString() {
      return id.toString();
   }

   /**
    * Generates tooltip text that should appear when hovering over item with this affix in an inventory screen
    *
    * @param isAdvanced is 'Advanced Tooltips' on
    * @param isStorage  is this affix stored on the item or actively used
    * @return tooltip text to add to tooltip for this item
    */
   public @NotNull List<Text> tooltip(boolean isAdvanced, boolean isStorage) {
      List<Text> tooltip = new ArrayList<>();
      final String transDesc = id.toTranslationKey("affix", "desc");
      tooltip.add(translation(Formatting.DARK_RED));
      if (isAdvanced && showDescriptions) {
         tooltip.add(Text.translatable(transDesc).formatted(Formatting.RED));
      }
      tooltip.addAll(hook.advancedTooltip()
                         .stream()
                         .filter(a -> showAdditionalInfo && !isStorage)
                         .map(t -> t
                               .copy()// clean cast ig
                               .formatted(Formatting.DARK_GRAY))
                         .toList());
      return tooltip;// todo desync between client and server - tooltips don't work in multiplayer
   }

   /**
    * Translates affix name for this affix &amp; applies formatting to it
    *
    * @param formatting formatting to apply to the affix name
    * @return translated &amp; formatted name of the affix
    */
   public Text translation(Formatting... formatting) {
      return Text.translatable(id().toTranslationKey("affix"))
                 .formatted(formatting);
   }
}
