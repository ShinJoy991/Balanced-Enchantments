package com.github.shinjoy991.balanced_enchantments.helpers;

import com.github.shinjoy991.balanced_enchantments.client.ArrowParticlePacket;
import com.github.shinjoy991.balanced_enchantments.client.ModNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber
public class ServerTick {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                for (ServerLevel world : server.getAllLevels()) {
                    for (Entity entity : world.getEntities().getAll()) {
                        if (entity instanceof AbstractArrow arrow) {
                            if (!arrow.isOnGround() && arrow.getDeltaMovement().length() != 0) {
                                int color = 0;
                                if (arrow.getPersistentData().getInt("BEColorArrow") != 0)
                                    color = arrow.getPersistentData().getInt("BEColorArrow");

                                if (color != 0) {
                                    ModNetworking.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> arrow),
                                            new ArrowParticlePacket(arrow.getId(), color));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
