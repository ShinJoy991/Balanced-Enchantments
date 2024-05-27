package com.github.shinjoy991.balanced_enchantments.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.shinjoy991.balanced_enchantments.BalancedEnchantments.LOGGER;

public class CreateJson {
    private static final Gson GSON =
            new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    public static Path configPath = FMLPaths.CONFIGDIR.get().resolve("balanced_enchantments");
    public static Path configFile = configPath.resolve("balanced_enchantments_config.json");

    public static void createJsonConfigFile() {
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath);
            } catch (IOException e) {
                LOGGER.error("[Balanced Enchantments] Failed to create directory: {}", configPath
                        , e);
                return;
            }
        }
        if (Files.exists(configFile)) {
            LOGGER.info("[Balanced Enchantments] Config file already exists, skipping creation: " +
                    "{}", configFile);
            return;
        }
        Map<String, Object> jsonData = new LinkedHashMap<>();
        List<String> comments = new ArrayList<>();
        comments.add("This is config section for Balanced Enchantments mod");
        comments.add("Pretty easy, change it to match your desire");
        comments.add("Use integer numbers and true/false to prevent errors");
        jsonData.put("__comment", comments);
        Map<String, Object> data1 = new LinkedHashMap<>();
        data1.put("enable", "true");
        data1.put("maxlevel", "2");
        data1.put("onlyhoe", "true");
        data1.put("tickduration", "20");
        data1.put("chance", "20");
        jsonData.put("blockmasksoul", data1);
        Map<String, Object> data2 = new LinkedHashMap<>();
        data2.put("enable", "true");
        data2.put("maxlevel", "3");
        data2.put("anglebetween", "10");
        data2.put("removesubarrow", "false");
        data2.put("matchinfinity", "false");
        jsonData.put("volley", data2);
        Map<String, Object> data3 = new LinkedHashMap<>();
        data3.put("enable", "true");
        data3.put("maxlevel", "3");
        data3.put("radiusperlvl", "1");
        data3.put("minduration", "20");
        data3.put("maxduration", "100");
        jsonData.put("magmawalker", data3);
        Map<String, Object> data4 = new LinkedHashMap<>();
        data4.put("enable", "true");
        data4.put("maxlevel", "4");
        data4.put("dmgaddperlvl", "4");
        data4.put("weaknesslvl", "1");
        data4.put("weaknessduration", "60");
        data4.put("cooldown", "100");
        jsonData.put("supercharged", data4);
        Map<String, Object> data5 = new LinkedHashMap<>();
        data5.put("enable", "true");
        data5.put("minrandom", "-10");
        data5.put("maxrandom", "10");
        jsonData.put("curseofdurability", data5);
        try (FileWriter writer = new FileWriter(configFile.toFile())) {
            GSON.toJson(jsonData, writer);
        } catch (IOException exception) {
            LOGGER.error("[Balanced Enchantments] Failed to write config file: {}", configFile,
                    exception);
        }
    }
}