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

/**
 * Recipe type for affixing items via smithing table, with minimal support for json
 */
//hardcode 4 now
public class AffixSmithingRecipe implements SmithingRecipe {
   //tutorial expects codecs; tutorial is for newer ver

   /**
    * Serializer for this recipe type.
    * <br>
    * it do be doin' somthin', i bet
    */
   public static final RecipeSerializer<AffixSmithingRecipe> SERIALIZER = register(Registries.RECIPE_SERIALIZER, id("affix_smithing"), new Serializer());
   private final Ingredient source;//affix actually
   private final Ingredient base;//item
   private final Ingredient cost;//material
   private final Identifier id;


   /**
    * constructor for this recipe type thing
    *
    * @param id     id of the recipe
    * @param source {@link Ingredient ingredient} used as a source of the affix
    * @param base   {@link Ingredient ingredient} used as a base, usually your eq
    * @param cost   {@link Ingredient ingredient} used as a cost of affixing the item
    */
   public AffixSmithingRecipe(Identifier id, Ingredient source,
         Ingredient base, Ingredient cost) {
      this.id = id;
      this.source = source;
      this.base = base;
      this.cost = cost;
   }

   /**
    * calling this function forces all the static fields to init which is the only reason it exists at all
    */
   public static void forceLoad() {}

   @Override
   public boolean testTemplate(ItemStack stack) {
      return this.source.test(stack);
   }

   @Override
   public boolean testBase(ItemStack stack) {
      return this.base.test(stack);
   }

   @Override
   public boolean testAddition(ItemStack stack) {
      return this.cost.test(stack);
   }

   @Override
   public boolean matches(Inventory inventory, World world) {
      return this.source.test(inventory.getStack(0))
             && this.base.test(inventory.getStack(1))
             && this.cost.test(inventory.getStack(2));
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
         Ingredient ingredient = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "source"));
         Ingredient ingredient2 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "base"));
         Ingredient ingredient3 = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "cost"));
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
         recipe.source.write(packetByteBuf);
         recipe.base.write(packetByteBuf);
         recipe.cost.write(packetByteBuf);
      }
   }
}
