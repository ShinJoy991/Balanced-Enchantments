package com.github.shinjoy991.balanced_enchantments.register;

import com.github.shinjoy991.balanced_enchantments.enchantments.HeroLanding;
import com.github.shinjoy991.balanced_enchantments.enchantments.HuntingInstinct;
import com.github.shinjoy991.balanced_enchantments.enchantments.StatusProtection;
import com.github.shinjoy991.balanced_enchantments.enchantments.SuperCharged;
import com.github.shinjoy991.balanced_enchantments.events.VolleyBowEvents;
import net.minecraftforge.common.MinecraftForge;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.getConfig;

public class RegisterEvent {
    public static void registermodevents() {
        boolean allowVolley = getConfig("volley", "enable", 0).toString().equalsIgnoreCase("true");
        boolean allowSuperCharged =
                getConfig("supercharged", "enable", 0).toString().equalsIgnoreCase("true");
        boolean allowHeroLanding =
                getConfig("herolanding", "enable", 0).toString().equalsIgnoreCase(
                "true");
        boolean allowStatusProtection =
                getConfig("statusprotection", "enable", 0).toString().equalsIgnoreCase(
                        "true");
        boolean allowHuntingInstinct =
                getConfig("huntinginstinct", "enable", 0).toString().equalsIgnoreCase(
                        "true");

        if (allowVolley)
            MinecraftForge.EVENT_BUS.register(VolleyBowEvents.class);
        if (allowSuperCharged)
            MinecraftForge.EVENT_BUS.register(SuperCharged.class);
        if (allowHeroLanding)
            MinecraftForge.EVENT_BUS.register(HeroLanding.class);
        if (allowStatusProtection)
            MinecraftForge.EVENT_BUS.register(StatusProtection.class);
        if (allowHuntingInstinct)
            MinecraftForge.EVENT_BUS.register(HuntingInstinct.class);
    }
}