package com.github.shinjoy991.balanced_enchantments;

import com.github.shinjoy991.balanced_enchantments.config.CreateJson;
import com.github.shinjoy991.balanced_enchantments.config.ReadConfig;
import com.github.shinjoy991.balanced_enchantments.register.RegisterBlock;
import com.github.shinjoy991.balanced_enchantments.register.RegisterEnch;
import com.github.shinjoy991.balanced_enchantments.register.RegisterEvent;
import com.github.shinjoy991.balanced_enchantments.register.RegisterItem;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.github.shinjoy991.balanced_enchantments.config.ReadConfig.initEnchantments;

@Mod("balanced_enchantments")
public class BalancedEnchantments {
    public static final String MOD_ID = "balanced_enchantments";
    public static final Logger LOGGER = LogManager.getLogger();

    public BalancedEnchantments() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
        CreateJson.createJsonConfigFile();
        ReadConfig.readJsonValue();
        initEnchantments();
        RegisterBlock.BLOCKS.register(bus);
        RegisterEnch.ENCHANTMENTS.register(bus);
        RegisterItem.ITEMS.register(bus);
        RegisterEvent.registermodevents();
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("BalancedEnchantments PREINIT setting up");
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("BalancedEnchantments hello from server starting");
    }
}