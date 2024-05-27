package com.github.shinjoy991.balanced_enchantments.helpers;

import net.minecraft.nbt.CompoundNBT;

public class NbtFunc {
    public static CompoundNBT copyNBTWithoutUUID(CompoundNBT oldNbt) {
        CompoundNBT newNbt = new CompoundNBT();
        for (String key : oldNbt.getAllKeys()) {
            if (!key.equals("UUID")) { // Exclude UUID
                newNbt.put(key, oldNbt.get(key));
            }
        }
        return newNbt;
    }
}