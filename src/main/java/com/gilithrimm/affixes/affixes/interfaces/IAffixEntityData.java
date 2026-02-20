package com.gilithrimm.affixes.affixes.interfaces;

import net.minecraft.nbt.NbtElement;

/**
 * Facade to save persistent data using NBT underneath, with String as unique keys<br>
 * todo namespaces
 *
 * @implNote this is essentially {@link String}:{@link NbtElement} map
 */
public interface IAffixEntityData {
   /**
    * Grabs data saved under given key
    *
    * @param key key under which data was saved
    * @return nbt element saved under key
    */
   NbtElement affixes$getData(String key);

   /**
    * Saves data under specified key
    *
    * @param key  key under which to save data
    * @param data data to save
    */
   void affixes$putData(String key, NbtElement data);

   /**
    * Checks if there is any data under specified key
    *
    * @param key key under which to check for data
    * @return if there is any data under given key
    */
   boolean affixes$containsData(String key);
}
