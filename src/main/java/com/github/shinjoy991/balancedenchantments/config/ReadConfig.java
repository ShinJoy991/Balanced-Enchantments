package com.github.shinjoy991.balancedenchantments.config;

import com.github.shinjoy991.balancedenchantments.enchantments.BlockMaskSoul;
import com.github.shinjoy991.balancedenchantments.enchantments.MagmaWalker;
import com.github.shinjoy991.balancedenchantments.enchantments.SuperCharged;
import com.github.shinjoy991.balancedenchantments.enchantments.Volley;
import com.github.shinjoy991.balancedenchantments.enchantments.curses.CurseOfDurability;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import static com.github.shinjoy991.balancedenchantments.BalancedEnchantments.LOGGER;
import static com.github.shinjoy991.balancedenchantments.register.RegisterEnch.*;

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
            return 10;
        }
    }

    public static int reloadConfig() {
        String jsonString;
        try {
            jsonString = new String(Files.readAllBytes(CreateJson.configFile),
                    StandardCharsets.UTF_8);
            jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
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
        if (getConfig("curseofdurability", "enable", 0)
                .toString().equalsIgnoreCase("true")) {
            CURSE_OF_DURABILITY = ENCHANTMENTS.register(
                    "curse_of_durability", () -> new CurseOfDurability() {
                    });
        }
    }
}