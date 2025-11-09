package com.gilithrimm.affixes.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

import static com.gilithrimm.affixes.AffixesMod.AFFIX_LIST;

public class AffixSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
   @Override
   public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context,
                                                        SuggestionsBuilder builder) {
      AFFIX_LIST.forEach(affix -> builder.suggest(affix.toString()));
      return builder.buildFuture();
   }
}
