package com.github.shinjoy991.balancedenchantments.helpers;

import com.google.common.collect.ArrayListMultimap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

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