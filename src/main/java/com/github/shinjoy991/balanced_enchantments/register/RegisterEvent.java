package com.github.shinjoy991.balanced_enchantments.register;

import com.github.shinjoy991.balanced_enchantments.enchantments.SuperCharged;
import com.github.shinjoy991.balanced_enchantments.events.VolleyBowEvents;
import net.minecraftforge.common.MinecraftForge;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;

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
