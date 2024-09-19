package com.github.shinjoy991.balanced_enchantments.helpers;

import com.github.shinjoy991.balanced_enchantments.client.ArrowParticlePacket;
import com.github.shinjoy991.balanced_enchantments.client.ModNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Iterator;

@Mod.EventBusSubscriber
public class ServerTick {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {

                for (ServerWorld world : server.getAllLevels()) {
                    for (Iterator<Entity> it = world.getEntities().iterator(); it.hasNext(); ) {
                        Entity entity = it.next();
                        if (entity instanceof ArrowEntity) {
                            ArrowEntity arrow = (ArrowEntity) entity;
                            if (!arrow.isOnGround() && !(arrow.getDeltaMovement().length() == 0)) {
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
