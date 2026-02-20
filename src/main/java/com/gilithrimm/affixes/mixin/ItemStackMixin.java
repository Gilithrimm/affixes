package com.gilithrimm.affixes.mixin;

import com.gilithrimm.affixes.AffixesMod;
import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.AffixRegistry;
import com.gilithrimm.affixes.affixes.interfaces.IAffixContainer;
import com.gilithrimm.affixes.affixes.interfaces.IAffixable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gilithrimm.affixes.AffixesMod.NBT_STORE_PREFIX;

/**
 * mixin for {@link ItemStack} implementing {@link IAffixable} &amp; {@link IAffixContainer}
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IAffixable, IAffixContainer {
   // nbt
   @Shadow
   private @Nullable NbtCompound nbt;

   private ItemStackMixin() {}

   @Override
   public boolean affixes$addAffix(Affix affix) {
      if (!hasNbt()) {
         getOrCreateNbt();
      }
      NbtList nbtList;
      // noinspection DataFlowIssue
      if (!nbt.contains("affixes")) {
         nbtList = new NbtList();
      } else {
         nbtList = nbt.getList("affixes", NbtElement.STRING_TYPE);
      }
      var asString = NbtString.of(affix.toString());
      if (!nbtList.contains(asString)) {
         nbtList.add(asString);
         nbt.put("affixes", nbtList);
         return true;
      }
      return false;
   }

   @Override
   public List<Affix> affixes$getAffixes() {
      return hasNbt() && nbt.contains("affixes")
             ? nbt.getList("affixes", NbtElement.STRING_TYPE)
                  .stream()
                  .map(NbtElement::asString)
                  .map(Identifier::tryParse)
                  .map(AffixRegistry::fromRegistry)
                  .toList()
             : List.of();
   }

   @Override
   public boolean affixes$removeAffix(Affix affix) {
      if (!affixes$containsAffix(affix)) {
         return false;
      }
      NbtList nbtList = nbt.getList("affixes", NbtElement.STRING_TYPE);
      var asString = NbtString.of(affix.toString());
      return nbtList.remove(asString);
   }

   @Override
   public boolean affixes$containsAffix(Affix affix) {
      if (!hasNbt() || !nbt.contains("affixes")) {
         return false;
      }

      NbtList nbtList = nbt.getList("affixes", NbtElement.STRING_TYPE);
      var asString = NbtString.of(affix.toString());
      return nbtList.contains(asString);
   }

   /**
    * shadowed from {@link ItemStack}. ignore.
    *
    * @return boolean
    */
   @Shadow
   public abstract boolean hasNbt();

   /**
    * shadowed from {@link ItemStack}. ignore.
    *
    * @return nbt compound
    */
   @Shadow
   public abstract NbtCompound getOrCreateNbt();

   // affix container thingy

   @Override
   public boolean affixes$storeAffix(Affix affix) {
      if (!hasNbt()) {
         getOrCreateNbt();
      }
      NbtList nbtList;
      if (!nbt.contains(NBT_STORE_PREFIX)) {
         nbtList = new NbtList();
      } else {
         nbtList = nbt.getList(NBT_STORE_PREFIX, NbtElement.STRING_TYPE);
      }
      NbtString nbtString = NbtString.of(affix.toString());
      if (!nbtList.contains(nbtString)) {
         nbtList.add(nbtString);
         nbt.put(NBT_STORE_PREFIX, nbtList);
         return true;
      }
      return false;
   }

   @Override
   public Set<Affix> affixes$getStoredAffixes() {
      return affixes$hasStoredAffixes()
             ? nbt.getList(NBT_STORE_PREFIX, NbtElement.STRING_TYPE)
                  .stream()
                  .map(NbtElement::asString)
                  .map(Identifier::tryParse)
                  .map(AffixRegistry::fromRegistry)
                  .collect(Collectors.toSet())
             : Set.of();
   }

   @Override
   public boolean affixes$hasStoredAffixes() {
      return hasNbt() && nbt.contains(AffixesMod.NBT_STORE_PREFIX);
   }
}
