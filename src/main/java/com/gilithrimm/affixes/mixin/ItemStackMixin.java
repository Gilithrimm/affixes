package com.gilithrimm.affixes.mixin;

import com.gilithrimm.affixes.affixes.Affix;
import com.gilithrimm.affixes.affixes.Affixes;
import com.gilithrimm.affixes.affixes.IAffixHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IAffixHolder {

   @Shadow
   private @Nullable NbtCompound nbt;

   @Override
   public boolean addAffix(Affix affix) {
      if (!hasNbt()) {
         getOrCreateNbt();
      }
      NbtList nbtList;
      //noinspection DataFlowIssue
      if (!nbt.contains("affixes")) {
         nbtList = new NbtList();
      } else {
         nbtList = nbt.getList("affixes", 8);
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
   public List<Affix> getAffixes() {
      if (hasNbt() && nbt.contains("affixes")) {
         return nbt.getList("affixes", 8).stream()
                   .map(nbtString -> Affixes.fromRegistry(Identifier.tryParse(nbtString.asString())))
                   .toList();
      }
      return List.of();
   }

   @Override
   public boolean removeAffix(Affix affix) {
      if (!hasNbt() || !nbt.contains("affixes")) {
         return false;
      }

      NbtList nbtList = nbt.getList("affixes", 8);
      var asString = NbtString.of(affix.toString());
      return nbtList.remove(asString);
   }

   @Override
   public boolean containsAffix(Affix affix) {
      if (!hasNbt() || !nbt.contains("affixes")) {
         return false;
      }

      NbtList nbtList = nbt.getList("affixes", 8);
      var asString = NbtString.of(affix.toString());
      return nbtList.contains(asString);
   }

   @Shadow
   public abstract boolean hasNbt();

   @Shadow
   public abstract NbtCompound getOrCreateNbt();

}
