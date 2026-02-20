package com.gilithrimm.affixes.affixes.interfaces;

import net.minecraft.entity.Entity;

public interface IOwner {
   Entity getOwner();

   void setOwner(Entity owner);
}
