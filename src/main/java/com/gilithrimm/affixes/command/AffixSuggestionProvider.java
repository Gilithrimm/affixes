package com.gilithrimm.affixes.command;

import com.gilithrimm.affixes.affixes.AffixRegistry;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

/**
 * text suggestions for the affix argument in commands (like in {@link AffixCommand /affix command})
 */
public class AffixSuggestionProvider
      implements SuggestionProvider<ServerCommandSource> {
   /**
    * i'm starting to hate javadoc, like why do you care about this function when no one besides you cares about this function???<br>
    * anyway this constructor is empty
    */
   public AffixSuggestionProvider() {}

   /**
    * generates suggestions for the {@link AffixCommand commands requiring affix}
    *
    * @param context context of the command, which we ignore
    * @param builder {@link SuggestionsBuilder suggestions builder}
    * @return suggestions for the command
    */
   @Override
   public CompletableFuture<Suggestions> getSuggestions(
         CommandContext<ServerCommandSource> context,
         SuggestionsBuilder builder) {
      AffixRegistry.stream()
                   .forEach(affix -> builder.suggest(affix.toString()));
      return builder.buildFuture();
   }
}
