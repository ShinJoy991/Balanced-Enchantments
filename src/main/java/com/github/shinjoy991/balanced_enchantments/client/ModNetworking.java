package com.github.shinjoy991.balanced_enchantments.client;

import com.github.shinjoy991.balanced_enchantments.BalancedEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BalancedEnchantments.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPackets() {
        int id = 0;
        INSTANCE.registerMessage(id++, ArrowParticlePacket.class, ArrowParticlePacket::toBytes, ArrowParticlePacket::new, ArrowParticlePacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}