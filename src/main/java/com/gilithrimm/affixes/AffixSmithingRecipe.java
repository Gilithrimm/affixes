package com.gilithrimm.affixes;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.interfaces.IAffixable;
import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

import static com.gilithrimm.affixes.AffixesMod.id;
import static com.gilithrimm.affixes.affixes.interfaces.IAffixContainer.getStoredAffixes;
import static com.gilithrimm.affixes.affixes.interfaces.IAffixable.getAffixesFromStack;
import static net.minecraft.registry.Registry.register;

//hardcode 4 now
public class AffixSmithingRecipe implements SmithingRecipe {
   //tutorial expects codecs; tutorial is for newer ver

   public static final RecipeSerializer<AffixSmithingRecipe> SERIALIZER = register(Registries.RECIPE_SERIALIZER, id("affix_smithing"), new Serializer());
   final Ingredient template;//affix actually
   final Ingredient base;//item
   final Ingredient addition;//material
   private final Identifier id;


   public AffixSmithingRecipe(Identifier id, Ingredient template,
                              Ingredient base, Ingredient addition) {
      this.id = id;
      this.template = template;
      this.base = base;
      this.addition = addition;
   }

   public static void forceLoad() {}

   @Override
   public boolean testTemplate(ItemStack stack) {
      return this.template.test(stack);
   }

   @Override
   public boolean testBase(ItemStack stack) {
      return this.base.test(stack);
   }

   @Override
   public boolean testAddition(ItemStack stack) {
      return this.addition.test(stack);
   }

   @Override
   public boolean matches(Inventory inventory, World world) {
      return this.template.test(inventory.getStack(0))
             && this.base.test(inventory.getStack(1))
             && this.addition.test(inventory.getStack(2));
   }

   @Override
   public ItemStack craft(Inventory inventory,
                          DynamicRegistryManager registryManager) {
      //actual result
      ItemStack templateStack = inventory.getStack(0);
      ItemStack baseStack = inventory.getStack(1);
      if (testBase(baseStack)) {
         //1. test if same affix
         // if so: fail & return IS.Empty
         // else: go on
         Set<Affix> baseAffixes = getAffixesFromStack(baseStack);
         Set<Affix> templateAffixes = getStoredAffixes(templateStack);
         if (new HashSet<>(baseAffixes).containsAll(templateAffixes)) {
            return ItemStack.EMPTY;
         }

         ItemStack baseCopy = baseStack.copy();
         baseCopy.setCount(1);
         if (templateAffixes.stream()
                            .filter(affix -> !baseAffixes.contains(affix))
                            .allMatch(affix -> IAffixable.addAffix(baseCopy, affix))) {

            return baseCopy;
         }
      }
      return ItemStack.EMPTY;//fail??
   }

   @Override
   public ItemStack getOutput(DynamicRegistryManager registryManager) {
      //displayed result
      return ItemStack.EMPTY;//fuck you.
   }

   @Override
   public Identifier getId() {
      return id;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return SERIALIZER;
   }

   static class Serializer implements RecipeSerializer<AffixSmithingRecipe> {
      public AffixSmithingRecipe read(Identifier identifier,
                                      JsonObject jsonObject) {
         Ingredient ingredient = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "template"));
         Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "base"));
         Ingredient ingredient3 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "addition"));
         return new AffixSmithingRecipe(identifier, ingredient, ingredient2, ingredient3);
      }

      public AffixSmithingRecipe read(Identifier identifier,
                                      PacketByteBuf packetByteBuf) {
         Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
         Ingredient ingredient2 = Ingredient.fromPacket(packetByteBuf);
         Ingredient ingredient3 = Ingredient.fromPacket(packetByteBuf);
         return new AffixSmithingRecipe(identifier, ingredient, ingredient2, ingredient3);
      }

      public void write(PacketByteBuf packetByteBuf,
                        AffixSmithingRecipe recipe) {
         recipe.template.write(packetByteBuf);
         recipe.base.write(packetByteBuf);
         recipe.addition.write(packetByteBuf);
      }
   }
}
