package com.gertoxq.wynnbuild.config;

import com.gertoxq.wynnbuild.Cast;
import com.gertoxq.wynnbuild.screens.tome.TomeScreenHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static com.gertoxq.wynnbuild.Powder.DEFAULT_POWDER_LEVEL;
import static com.gertoxq.wynnbuild.client.WynnBuildClient.*;
import static com.gertoxq.wynnbuild.screens.itemmenu.SavedItemsScreen.getNotUsedName;

public class Manager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config/wynnbuild.json");
    private ConfigType config;

    public void loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                config = new ConfigType();
                saveConfig();
            } else {
                BufferedReader reader = Files.newBufferedReader(CONFIG_PATH);
                config = GSON.fromJson(reader, ConfigType.class);
                try {
                    cast = Cast.valueOf(config.getCast());
                    currentDupeMap = dupeMap.get(cast.name).getAsJsonObject().asMap();
                    castTreeObj = fullatree.get(cast.name).getAsJsonObject();
                } catch (Exception e) {
                    System.out.println("Invalid Cast value");
                }
                ATREE_IDLE = config.getAtreeIdleTime();
                tomeIds = config.getTomeIds().size() == 8 ? config.getTomeIds() : TomeScreenHandler.EMPTY_IDS;
                atreeSuffix = config.getAtreeEncoding();
                DEFAULT_POWDER_LEVEL = config.getDefaultPowderLevel();
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH.getParent());
            }
            BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH);
            GSON.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigType getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    public void setConfig(ConfigType config) {
        this.config = config;
        saveConfig();
    }

    public SavedItemType addSavedOrReturnExisting(SavedItemType savedItem) {
        List<SavedItemType> sameItems = getConfigManager().getConfig().getSavedItems().stream().filter(si -> Objects.equals(si.getHash(), savedItem.getHash())).toList();
        if (sameItems.isEmpty()) {
            savedItem.setName(getNotUsedName(savedItem.getName()));
            getConfigManager().getConfig().getSavedItems().add(savedItem);
            getConfigManager().saveConfig();
            return null;
        }
        return sameItems.getFirst();
    }
}
