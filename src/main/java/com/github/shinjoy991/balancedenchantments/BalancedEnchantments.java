package com.github.shinjoy991.balancedenchantments;

import com.github.shinjoy991.balancedenchantments.config.CreateJson;
import com.github.shinjoy991.balancedenchantments.register.RegisterBlock;
import com.github.shinjoy991.balancedenchantments.register.RegisterEnch;
import com.github.shinjoy991.balancedenchantments.register.RegisterEvent;
import com.github.shinjoy991.balancedenchantments.register.RegisterItem;
//import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.github.shinjoy991.balancedenchantments.config.ReadConfig.initEnchantments;
import static com.github.shinjoy991.balancedenchantments.config.ReadConfig.readJsonValue;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("balancedenchantments")
public class BalancedEnchantments {
    public static final String MOD_ID = "balancedenchantments";
    public static final Logger LOGGER = LogManager.getLogger();

    public BalancedEnchantments() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        //MixinExtrasBootstrap.init();
        CreateJson.CreateJsonConfigFile();
        readJsonValue(CreateJson.configFile);
        initEnchantments();
        RegisterBlock.BLOCKS.register(bus);
        RegisterEnch.ENCHANTMENTS.register(bus);
        RegisterItem.ITEMS.register(bus);
        RegisterEvent.registermodevents();
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("BalancedEnchantments PREINIT setting up...");
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("BalancedEnchantments hello from server starting");
    }
}