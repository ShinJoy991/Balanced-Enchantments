package com.github.shinjoy991.balanced_enchantments.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.world.World;

@SuppressWarnings("resources")
public class ClientParticlesHandler {

    public static void mightyForceArrow(int entityId, Integer color) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientWorld world = minecraft.level;
        if (world != null) {
            Entity entity = world.getEntity(entityId);
            if (entity instanceof AbstractArrowEntity) {
                AbstractArrowEntity arrow = (AbstractArrowEntity) entity;
                addParticles(arrow, world, color);
            }
        }
    }


    private static void addParticles(AbstractArrowEntity arrow, World world, Integer color) {
        double x = arrow.getX();
        double y = arrow.getY();
        double z = arrow.getZ();
        int count = 10;
        RedstoneParticleData particleData = getParticleData(color);

        for (int i = 0; i < count; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetY = (world.random.nextDouble() - 0.5) * 0.1;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.1;
            world.addParticle(particleData, x + offsetX, y + offsetY, z + offsetZ, 0, 0, 0);
        }
    }

    private static RedstoneParticleData getParticleData(Integer color) {
        switch (color) {
            case 1:
                return new RedstoneParticleData(0.0F, 0.5F, 0.0F, 1.0F);
            case 2:
                return new RedstoneParticleData(0.0F, 1.0F, 0.0F, 1.0F);
            case 3:
                return new RedstoneParticleData(1.0F, 1.0F, 0.0F, 1.0F);
            case 4:
                return new RedstoneParticleData(1.0F, 0.5F, 0.0F, 1.0F);
            case 5:
                return new RedstoneParticleData(1.0F, 0.0F, 0.0F, 1.0F);
            default:
                return new RedstoneParticleData(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
