package com.github.shinjoy991.balancedenchantments.register;

import com.github.shinjoy991.balancedenchantments.enchantments.SuperCharged;
import com.github.shinjoy991.balancedenchantments.events.VolleyBowEvents;
import net.minecraftforge.common.MinecraftForge;

import static com.github.shinjoy991.balancedenchantments.config.ReadConfig.getConfig;

public class RegisterEvent {
    public static void registermodevents() {
        boolean allowVolley = getConfig("volley", "enable", 0).toString().equalsIgnoreCase("true");
        boolean allowSuperCharged =
                getConfig("supercharged", "enable", 0).toString().equalsIgnoreCase("true");
        if (allowVolley)
            MinecraftForge.EVENT_BUS.register(VolleyBowEvents.class);
        if (allowSuperCharged)
            MinecraftForge.EVENT_BUS.register(SuperCharged.class);
    }
}
