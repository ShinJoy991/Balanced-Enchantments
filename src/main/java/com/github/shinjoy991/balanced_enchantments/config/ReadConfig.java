package com.github.shinjoy991.balanced_enchantments.config;

import com.github.shinjoy991.balanced_enchantments.enchantments.BlockMaskSoul;
import com.github.shinjoy991.balanced_enchantments.enchantments.MagmaWalker;
import com.github.shinjoy991.balanced_enchantments.enchantments.SuperCharged;
import com.github.shinjoy991.balanced_enchantments.enchantments.Volley;
import com.github.shinjoy991.balanced_enchantments.enchantments.curses.CurseOfDurability;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

import static com.github.shinjoy991.balanced_enchantments.BalancedEnchantments.LOGGER;
import static com.github.shinjoy991.balanced_enchantments.config.CreateJson.configFile;
import static com.github.shinjoy991.balanced_enchantments.register.RegisterEnch.*;

public class ReadConfig {
    public static JsonObject jsonObject;
    public static ArrayList<String> keysList = new ArrayList<>();
    private static int errordelay = 300;

    public static void readJsonValue() {
        try {
            String jsonString = new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8);
            jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String memberName = entry.getKey();
                if (!memberName.equals("__comment")) {
                    keysList.add(memberName);
                }
            }
        } catch (IOException e) {
            LOGGER.error("[Balanced Enchantments] Error when reading Json: " + e);
        }
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
                LOGGER.error("[Balanced Enchantments] error {} in {}", subKey, key);
            } else
                errordelay++;
            if (getInt != 1) {
                return "null";
            }
            return -1;
        }
    }

    public static int reloadConfig() {
        String jsonString;
        try {
            jsonString = new String(Files.readAllBytes(configFile),
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