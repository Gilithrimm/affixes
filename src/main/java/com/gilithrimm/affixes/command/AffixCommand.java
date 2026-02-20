package com.gilithrimm.affixes.command;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.AffixRegistry;
import com.gilithrimm.affixes.affixes.interfaces.IAffixable;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * {@code /affix} command stuff.
 */
public class AffixCommand {
   private static final DynamicCommandExceptionType FAILED_ENTITY_EXCEPTION = new DynamicCommandExceptionType(
         entityName -> Text.translatable("commands.affix.failed.entity",
               entityName)
   );
   private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION = new DynamicCommandExceptionType(
         entityName -> Text.translatable("commands.affix.failed.itemless",
               entityName)
   );
   private static final DynamicCommandExceptionType FAILED_AFFIX_CONTAINED_EXCEPTION = new DynamicCommandExceptionType(
         itemName -> Text.translatable("commands.affix.failed.affix", itemName)
   );
   private static final DynamicCommandExceptionType FAILED_AFFIX_NOT_CONTAINED_EXCEPTION = new DynamicCommandExceptionType(
         itemName -> Text.translatable("commands.affix.failed.no_affix",
               itemName)
   );
   private static final SimpleCommandExceptionType FAILED_GENERAL = new SimpleCommandExceptionType(
         Text.translatable("command.failed"));

   private AffixCommand() {}

   /**
    * registers {@code /affix} command with all arguments required
    */
   public static void init() {
      CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> dispatcher.register(
                  literal("affix")
                        .requires(src -> src.hasPermissionLevel(2))
                        .then(literal("add")
                              .then(argument("targets",
                                    EntityArgumentType.entities())
                                    .then(argument("affix",
                                          IdentifierArgumentType.identifier())
                                          .suggests(
                                                new AffixSuggestionProvider())
                                          .executes(AffixCommand::execAdd))))
                        .then(literal("remove")
                              .then(argument("targets",
                                    EntityArgumentType.entities())
                                    .then(argument("affix",
                                          IdentifierArgumentType.identifier())
                                          .suggests(
                                                new AffixSuggestionProvider())
                                          .executes(AffixCommand::execRemove))))
                        .then(literal("data")
                              .then(literal("entity")
                                    .then(argument("target",
                                          EntityArgumentType.entity())
                                          .then(argument("key",
                                                StringArgumentType.string())
                                                .then(literal("get")
                                                      .executes(
                                                            AffixDataCommand::execGetEntity))
                                                .then(literal("set")
                                                      .then(argument("value",
                                                            NbtElementArgumentType.nbtElement())
                                                            .executes(
                                                                  AffixDataCommand::execSetEntity))))))
                              .then(literal("item")
                                    .then(argument("key",
                                          StringArgumentType.string())
                                          .then(literal("get")
                                                .executes(
                                                      AffixDataCommand::execGetItem))
                                          .then(literal("set")
                                                .then(argument("value",
                                                      NbtElementArgumentType.nbtElement())
                                                      .executes(
                                                            AffixDataCommand::execSetItem)))))))
      );
   }

   private static int execAdd(
         CommandContext<ServerCommandSource> context) throws
         CommandSyntaxException {
      final var affix = AffixRegistry.fromRegistry(
            IdentifierArgumentType.getIdentifier(context, "affix"));
      final var targets = EntityArgumentType.getEntities(context, "targets");
      int i = countAdditions(targets, affix);
      if (i == 0) {
         throw FAILED_GENERAL.create();
      } else {
         if (targets.size() == 1) {
            context.getSource().sendFeedback(
                  () -> Text.translatable("commands.affix.add.single",
                        affix.translation(),
                        targets.iterator().next().getDisplayName()), true
            );
         } else {
            context.getSource().sendFeedback(
                  () -> Text.translatable("commands.affix.add.multiple",
                        affix.translation(), targets.size()), true
            );
         }
      }
      return i;
   }

   private static int execRemove(
         CommandContext<ServerCommandSource> context) throws
         CommandSyntaxException {
      //get args
      final var affix = AffixRegistry.fromRegistry(
            IdentifierArgumentType.getIdentifier(context, "affix"));
      final var targets = EntityArgumentType.getEntities(context, "targets");

      int i = countRemovals(targets, affix);

      if (i == 0) {
         throw FAILED_GENERAL.create();
      } else {
         if (targets.size() == 1) {
            context.getSource().sendFeedback(
                  () -> Text.translatable("commands.affix.remove.single",
                        affix.translation(),
                        targets.iterator().next().getDisplayName()), true
            );
         } else {
            context.getSource().sendFeedback(
                  () -> Text.translatable("commands.affix.remove.multiple",
                        affix.translation(), targets.size()), true
            );
         }
      }
      return i;
   }

   private static int countAdditions(Collection<? extends Entity> targets,
                                     Affix affix) throws
         CommandSyntaxException {
      int i = 0;
      for (Entity e : targets) {
         if (e instanceof LivingEntity living) {
            final ItemStack inHand = living.getMainHandStack();
            if (!inHand.isEmpty()) {
               if (!((IAffixable) (Object) inHand).affixes$addAffix(affix) &&
                   targets.size() == 1) {
                  throw FAILED_AFFIX_CONTAINED_EXCEPTION.create(
                        inHand.getName().getString());
               }
               i++;
            } else if (targets.size() == 1) {
               throw FAILED_ITEMLESS_EXCEPTION.create(e.getName().getString());
            }
         } else if (targets.size() == 1) {
            throw FAILED_ENTITY_EXCEPTION.create(e.getName().getString());
         }
      }
      return i;
   }

   private static int countRemovals(Collection<? extends Entity> targets,
                                    Affix affix) throws CommandSyntaxException {
      int i = 0;
      for (Entity e : targets) {
         if (e instanceof LivingEntity living) {
            final ItemStack inHand = living.getMainHandStack();
            if (!inHand.isEmpty()) {
               if (!((IAffixable) (Object) inHand).affixes$removeAffix(affix) &&
                   targets.size() == 1) {
                  throw FAILED_AFFIX_NOT_CONTAINED_EXCEPTION.create(
                        inHand.getName().getString());
               }
               i++;
            } else if (targets.size() == 1) {
               throw FAILED_ITEMLESS_EXCEPTION.create(e.getName().getString());
            }
         } else if (targets.size() == 1) {
            throw FAILED_ENTITY_EXCEPTION.create(e.getName().getString());
         }
      }
      return i;
   }
}
