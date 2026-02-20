package com.gilithrimm.affixes.command;

import com.gilithrimm.affixes.affixes.interfaces.IAffixEntityData;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static com.gilithrimm.affixes.AffixesMod.NBT_PREFIX;

/**
 * {@code /affix data} subcommand stuff
 */
public class AffixDataCommand {
   private static final DynamicCommandExceptionType FAILED_WRONG_TARGET = new DynamicCommandExceptionType(
         entityName -> Text.literal("%s is not living.".formatted(entityName)));
   private static final DynamicCommandExceptionType FAILED_NO_NBT = new DynamicCommandExceptionType(
         name -> Text.literal("%s has no such nbt data".formatted(name)));
   private static final Dynamic2CommandExceptionType FAILED_NO_KEY = new Dynamic2CommandExceptionType(
         (data, key) -> Text.literal("%s has no key %s".formatted(data, key)));
   private static final SimpleCommandExceptionType FAILED_NO_TARGET = new SimpleCommandExceptionType(
         Text.literal("No entity to refer to."));

   private AffixDataCommand() {}

   static int execGetEntity(
         CommandContext<ServerCommandSource> context) throws
         CommandSyntaxException {
      Entity entity = EntityArgumentType.getEntity(context, "target");
      String key = StringArgumentType.getString(context, "key");
      if (entity != null) {
         if (entity instanceof LivingEntity living) {
            if (((IAffixEntityData) living).affixes$containsData(key)) {
               var nbtElement = ((IAffixEntityData) living).affixes$getData(
                     key);
               context.getSource()
                      .sendFeedback(() -> Text.literal(
                            "Key: %s value: %s".formatted(key,
                                  nbtElement.asString())), false);
               return 0;
            }
            throw FAILED_NO_KEY.create(living.getName(), key);
         }
         throw FAILED_WRONG_TARGET.create(entity.getName());
      }
      throw FAILED_NO_TARGET.create();
   }

   static int execSetEntity(
         CommandContext<ServerCommandSource> context) throws
         CommandSyntaxException {
      Entity entity = EntityArgumentType.getEntity(context, "target");
      String key = StringArgumentType.getString(context, "key");
      NbtElement nbtElement = NbtElementArgumentType.getNbtElement(context,
            "value");
      if (entity != null) {
         if (entity instanceof LivingEntity living) {
            if (((IAffixEntityData) living).affixes$containsData(key)) {
               ((IAffixEntityData) living).affixes$putData(key, nbtElement);
               context.getSource()
                      .sendFeedback(() -> Text.literal(
                            "Successfully replaced data under key %s with %s".formatted(
                                  key, nbtElement.asString())), false);
               return 1;
            }
            throw FAILED_NO_KEY.create(living.getName(), key);
         }
         throw FAILED_WRONG_TARGET.create(entity.getName());
      }
      throw FAILED_NO_TARGET.create();
   }

   static int execGetItem(CommandContext<ServerCommandSource> context) throws
         CommandSyntaxException {
      Entity entity = context.getSource().getEntity();
      String key = StringArgumentType.getString(context, "key");
      if (entity != null) {
         ItemStack inHand = ((LivingEntity) entity).getMainHandStack();
         NbtCompound nbt = inHand.getNbt();
         if (inHand.hasNbt() && nbt.contains(NBT_PREFIX)) {
            if (nbt.getCompound(NBT_PREFIX).contains(key)) {
               context.getSource()
                      .sendFeedback(
                            () -> Text.literal(
                                  "Key: %s value: %s".formatted(key,
                                        nbt.getCompound(NBT_PREFIX)
                                           .get(key))), false);
               return 0;
            }
            throw FAILED_NO_KEY.create(inHand.getName().getString(), key);
         }
         throw FAILED_NO_NBT.create(inHand.getName().getString());
      }
      throw FAILED_NO_TARGET.create();
   }

   static int execSetItem(CommandContext<ServerCommandSource> context) throws
         CommandSyntaxException {
      Entity entity = context.getSource().getEntity();
      String key = StringArgumentType.getString(context, "key");
      NbtElement value = NbtElementArgumentType.getNbtElement(context, "value");
      if (entity != null) {
         ItemStack mainHandStack = ((LivingEntity) entity).getMainHandStack();
         NbtCompound nbt = mainHandStack.getNbt();
         if (mainHandStack.hasNbt()
             && nbt.contains(NBT_PREFIX)) {
            NbtElement prev = nbt.getCompound(NBT_PREFIX).put(key, value);
            if (prev != null) {
               context.getSource()
                      .sendFeedback(() -> Text.literal(
                            "Successfully replaced value %s for key %s with %s".formatted(
                                  prev, key, value)), true);
            } else {
               context.getSource()
                      .sendFeedback(() -> Text.literal(
                            "Successfully added value %s under key %s".formatted(
                                  value, key)), true);
            }
            return 1;
         }
         throw FAILED_NO_NBT.create(mainHandStack.getName().getString());
      }
      throw FAILED_NO_TARGET.create();
   }
}
