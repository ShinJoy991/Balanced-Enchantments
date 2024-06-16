package com.github.shinjoy991.balanced_enchantments.config;

import com.github.shinjoy991.balanced_enchantments.enchantments.*;
import com.github.shinjoy991.balanced_enchantments.enchantments.curses.CurseOfDurability;
import com.github.shinjoy991.balanced_enchantments.enchantments.curses.CurseOfUnstable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import static com.github.shinjoy991.balanced_enchantments.BalancedEnchantments.LOGGER;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.*;

public class ReadConfig {
    public static JsonObject jsonObject;
    public static ArrayList<String> keysList = new ArrayList<>();
    private static int errordelay = 300;

    public static String readJsonValue(Path configFile) {
        try {
            String jsonString = new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8);
            jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String memberName = entry.getKey();
                if (!memberName.equals("__comment")) {
                    keysList.add(memberName);
                }
            }
        } catch (IOException e) {
            LOGGER.error("[Balanced Enchantments] Error when reading Json" + e);
        }
        return null;
    }

    public static Object getConfig(String key, String subKey, int getInt) {
        try {
            JsonElement element = jsonObject.getAsJsonObject(key).get(subKey);
            if (getInt != 1) {
                return element.getAsString();
            }
            return element.getAsInt();
        } catch (Exception e) {
            if (errordelay > 300) {
                errordelay = 0;
                LOGGER.error(e);
                LOGGER.error("[Balanced Enchantments] error " + subKey + " in " + key);
            } else
                errordelay++;
            if (getInt != 1) {
                return "null";
            }
            return -1;
        }
    }

    public static int reloadConfig() {
        try {
            String jsonString = new String(Files.readAllBytes(CreateJson.configFile),
                    StandardCharsets.UTF_8);
            jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();

            if (getConfig("statusprotection", "enable", 0).toString().equalsIgnoreCase("true")) {
                StatusProtection.STATUS_COOLDOWN_TICKS = Math.max((Integer) getConfig(
                        "statusprotection", "initialcooldown", 1), 5);
                StatusProtection.STATUS_COOLDOWN_REDUCE_PER_LEVEL = Math.max((Integer) getConfig(
                        "statusprotection", "cooldownreduceperlvl", 1), 0);
                StatusProtection.STATUS_CHANCE = Math.max((Integer) getConfig(
                        "statusprotection", "initialchance", 1), 0);
                StatusProtection.STATUS_CHANCE_INCREASE_PER_LEVEL = Math.max((Integer) getConfig(
                        "statusprotection", "chanceincreaseperlvl", 1), 0);
                StatusProtection.STATUS_ALL_THRESHOLD = Math.max((Integer) getConfig(
                        "statusprotection", "allstatuslevel", 1), 0);
            }
            return 0;
        } catch (IOException e) {
            LOGGER.error("[Balanced Enchantments] Config reload error " + e);
            return 1;
        }
    }

    public static void initEnchantments() {
        if (getConfig("blockmasksoul", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            BLOCK_MASK_SOUL = ENCHANTMENTS
                    .register("block_mask_soul", () -> new BlockMaskSoul() {
                    });
        }
        if (getConfig("volley", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            VOLLEY = ENCHANTMENTS.register("volley", () -> new Volley() {
            });
        }
        if (getConfig("magmawalker", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            MAGMA_WALKER = ENCHANTMENTS.register("magma_walker", () -> new MagmaWalker() {
            });
        }
        if (getConfig("supercharged", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            SUPER_CHARGED = ENCHANTMENTS.register("super_charged", () -> new SuperCharged() {
            });
        }
        if (getConfig("herolanding", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            HERO_LANDING = ENCHANTMENTS.register("hero_landing", () -> new HeroLanding() {
            });
        }
        if (getConfig("cmoon", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            C_MOON = ENCHANTMENTS.register("c_moon", () -> new CMoon() {
            });
        }
        if (getConfig("statusprotection", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            STATUS_PROTECTION = ENCHANTMENTS.register("status_protection", () -> new StatusProtection() {
            });
            StatusProtection.STATUS_COOLDOWN_TICKS = Math.max((Integer) getConfig(
                    "statusprotection", "initialcooldown", 1), 5);
            StatusProtection.STATUS_COOLDOWN_REDUCE_PER_LEVEL = Math.max((Integer) getConfig(
                    "statusprotection", "cooldownreduceperlvl", 1), 0);
            StatusProtection.STATUS_CHANCE = Math.max((Integer) getConfig(
                    "statusprotection", "initialchance", 1), 0);
            StatusProtection.STATUS_CHANCE_INCREASE_PER_LEVEL = Math.max((Integer) getConfig(
                    "statusprotection", "chanceincreaseperlvl", 1), 0);
            StatusProtection.STATUS_ALL_THRESHOLD = Math.max((Integer) getConfig(
                    "statusprotection", "allstatuslevel", 1), 0);
        }
        if (getConfig("iceaspect", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            ICE_ASPECT = ENCHANTMENTS.register("ice_aspect", () -> new IceAspect() {
            });
        }
        if (getConfig("huntinginstinct", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            HUNTING_INSTINCT = ENCHANTMENTS.register("hunting_instinct",
                    () -> new HuntingInstinct() {
            });
        }
        if (getConfig("curseofdurability", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            CURSE_OF_DURABILITY = ENCHANTMENTS.register(
                    "curse_of_durability", () -> new CurseOfDurability() {
                    });
        }
        if (getConfig("curseofunstable", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            CURSE_OF_UNSTABLE = ENCHANTMENTS.register("curse_of_unstable",
                    () -> new CurseOfUnstable() {});
        }
    }
}