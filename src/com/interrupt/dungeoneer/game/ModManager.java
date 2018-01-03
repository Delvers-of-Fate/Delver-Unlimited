package com.interrupt.dungeoneer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.interrupt.api.steam.SteamApi;
import com.interrupt.api.steam.workshop.WorkshopModData;
import com.interrupt.dungeoneer.generator.GenTheme;
import com.interrupt.dungeoneer.gfx.TextureAtlas;
import com.interrupt.dungeoneer.gfx.animation.lerp3d.LerpedAnimationManager;
import com.interrupt.dungeoneer.gfx.shaders.ShaderData;
import com.interrupt.managers.*;
import net.cotd.delverunlimited.helper.Mod;
import net.cotd.delverunlimited.helper.ModInfo;
import net.cotd.delverunlimited.helper.ModSettings;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ModManager {
    public Array<String> modsFound = new Array(); // MODS LOAD FROM THIS ARRAY!!
    public Array<String> excludeFiles = new Array();
    public Array<Mod> modList = new Array<Mod>();

    public final String MOD_SETTINGS_FILE = "mod_settings.json";
    public final String MOD_INFO_FILE = "mod_info.json";
    public final String MOD_INFO_FILE_ALT = "modInfo.json";

    public ModManager() {
        this.modsFound.add("."); // base game

        if(new File("mods").exists()) { // does mods folder exist
            File[] directories = new File("mods").listFiles(File::isDirectory);
            for (File modFolder : directories) {
                try {
                    if (Files.newDirectoryStream(Paths.get(modFolder.toString())).iterator().hasNext()) { // is the mod folder empty?
                        addMod(modFolder);
                    } else {
                        Gdx.app.log("ModManager", modFolder.toString() + " was empty!");
                    }
                } catch (Exception ex) {
                    Gdx.app.error("ModManager", ex.getMessage());
                }
            }
        } else {
            Gdx.app.log("ModManager", "Couldn't find a /mods folder");
        }

        for (String mod : SteamApi.api.getWorkshopFolders()) {
            addMod(new File(mod));
        }

        // final calls
        for (Mod mod : modList) {
            if(mod.modState == ModSettings.ModState.Enabled) {
                this.modsFound.add(mod.modPath);
            }
        }
        this.loadExcludesList();
    }

    private void addMod(File modFolder) {
        String MOD_FOLDER_FULL = modFolder.toString() + File.separator;

        File modInfoFile = new File(MOD_FOLDER_FULL + MOD_INFO_FILE);
        File modSettingsFile = new File(MOD_FOLDER_FULL + MOD_SETTINGS_FILE);

        /* DOES MOD.JSON EXIST */
        if (!modInfoFile.exists()) {
            Gdx.app.log("ModManager", modFolder.toString() + " did not have a mod_info.json! Please advise the developer of the mod to add support for Delver-Unlimited!");

            /* Parse modInfo.json, created by DelvEdit */
            File modInfoAltFile = new File(MOD_FOLDER_FULL + MOD_INFO_FILE_ALT);
            if (modInfoAltFile.exists()) {
                Gdx.app.log("ModManager", modFolder.toString() + " has a " + MOD_INFO_FILE_ALT + "!");
                WorkshopModData workshopModData = Game.fromJson(WorkshopModData.class, new FileHandle(modInfoAltFile));

                ModInfo modInfo = new ModInfo(workshopModData.title, "Unknown", "Unknown", "Unknown", "https://steamcommunity.com/sharedfiles/filedetails/?id=" + workshopModData.workshopId);
                String modFile = Game.toJson(modInfo, Mod.class);
                try {
                    Files.write(Paths.get(MOD_FOLDER_FULL + MOD_INFO_FILE), modFile.getBytes());
                } catch (Exception ex) {
                    Gdx.app.error("ModManager", ex.getMessage());
                }
            } else {
                /* Generate mod info file */
                ModInfo modInfo = new ModInfo(modFolder.getName().trim(), "Unknown", "Generated mod info file!", "Unknown", null);
                String modFile = Game.toJson(modInfo, Mod.class);
                try {
                    Files.write(Paths.get(MOD_FOLDER_FULL + MOD_INFO_FILE), modFile.getBytes());
                } catch (Exception ex) {
                    Gdx.app.error("ModManager", ex.getMessage());
                }
            }
        }

        Gdx.app.log("ModManager", modFolder.getAbsolutePath());

        ModSettings settingsFile;
        if (modSettingsFile.exists()) {
            settingsFile = Game.fromJson(ModSettings.class, new FileHandle(MOD_FOLDER_FULL + MOD_SETTINGS_FILE));
        } else {
            settingsFile = new ModSettings(ModSettings.ModState.Enabled, modFolder.toString());
            String modFile = Game.toJson(settingsFile, Mod.class);

            try {
                Files.write(Paths.get(MOD_FOLDER_FULL + MOD_SETTINGS_FILE), modFile.getBytes());
            } catch (Exception ex) {
                Gdx.app.error("ModManager", ex.getMessage());
            }
        }

        ModInfo infoFile = Game.fromJson(ModInfo.class, new FileHandle(modInfoFile));
        Mod finishedMod = new Mod(infoFile.name, infoFile.author, infoFile.description, infoFile.version, infoFile.url, settingsFile.modState, settingsFile.modPath);

        if (finishedMod.modState == null) {
            finishedMod.modState = ModSettings.ModState.Enabled;
        }

        modList.add(finishedMod);
    }

    private void loadExcludesList() {
        for (String path : modsFound) {
            try {
                FileHandle modFile = Game.getInternal(path + "/data/excludes.dat");
                if (modFile.exists()) {
                    Array<String> excludes = (Array)Game.fromJson(Array.class, modFile);
                    if (excludes != null) {
                        this.excludeFiles.addAll(excludes);
                    }
                }
            } catch (Exception ex) {
                Gdx.app.error("NodManager", "Error loading mod file " + path + "/data/excludes.dat");
            }
        }

    }

    public EntityManager loadEntityManager(String[] filenames) {
        EntityManager entityManager = null;
        for (String filename : filenames) {
            for (String path : this.modsFound) {
                String filePath = path + "/data/" + filename;
                try {
                    FileHandle modFile = Game.getInternal(filePath);
                    if (!modFile.exists() || this.pathIsExcluded(filePath)) continue;
                    EntityManager thisModManager = Game.fromJson(EntityManager.class, modFile);
                    if (entityManager == null) {
                        entityManager = thisModManager;
                        continue;
                    }
                    if (thisModManager == null) continue;
                    entityManager.merge(thisModManager);
                }
                catch (Exception ex) {
                    Gdx.app.error("Delver", "Error loading mod file: " + filePath);
                }
            }
        }
        return entityManager;
    }

    public ItemManager loadItemManager(String[] filenames) {
        ItemManager itemManager = null;
        for (String filename : filenames) {
            for (String path : this.modsFound) {
                String filePath = path + "/data/" + filename;
                try {
                    FileHandle modFile = Game.getInternal(filePath);
                    if (!modFile.exists() || this.pathIsExcluded(filePath)) continue;
                    ItemManager thisModManager = Game.fromJson(ItemManager.class, modFile);
                    if (itemManager == null) {
                        itemManager = thisModManager;
                        continue;
                    }
                    if (thisModManager == null) continue;
                    itemManager.merge(thisModManager);
                }
                catch (Exception ex) {
                    Gdx.app.error("Delver", "Error loading mod file: " + filePath);
                }
            }
        }
        return itemManager;
    }

    public MonsterManager loadMonsterManager(String[] filenames) {
        MonsterManager monsterManager = null;
        for (String filename : filenames) {
            for (String path : this.modsFound) {
                String filePath = path + "/data/" + filename;
                try {
                    FileHandle modFile = Game.getInternal(filePath);
                    if (!modFile.exists() || this.pathIsExcluded(filePath)) continue;
                    MonsterManager thisModManager = Game.fromJson(MonsterManager.class, modFile);
                    if (monsterManager == null) {
                        monsterManager = thisModManager;
                        continue;
                    }
                    if (thisModManager == null) continue;
                    monsterManager.merge(thisModManager);
                }
                catch (Exception ex) {
                    Gdx.app.error("Delver", "Error loading mod file: " + filePath);
                }
            }
        }
        return monsterManager;
    }

    public TextureAtlas[] getTextureAtlases(String filename) {
        ArrayMap<String, TextureAtlas> combinedAtlases = new ArrayMap();

        for (String path : modsFound) {
            try {
                FileHandle modFile = Game.getInternal(path + "/data/" + filename);
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/" + filename)) {
                    TextureAtlas[] atlases = Game.fromJson(TextureAtlas[].class, modFile);

                    for(int i = 0; i < atlases.length; ++i) {
                        combinedAtlases.put(atlases[i].name, atlases[i]);
                    }
                }
            } catch (Exception ex) {
                Gdx.app.error("NodManager", "Error loading mod file " + path + "/" + filename);
            }
        }

        TextureAtlas[] atlasArray = new TextureAtlas[combinedAtlases.size];

        for(int i = 0; i < combinedAtlases.size; ++i) {
            atlasArray[i] = combinedAtlases.getValueAt(i);
        }

        return atlasArray;
    }

    public TileManager loadTileManager() {
        TileManager combinedTileManager = new TileManager();

        for (String path : modsFound) {
            try {
                FileHandle modFile = Game.getInternal(path + "/data/tiles.dat");
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/tiles.dat")) {
                    TileManager tileManager = Game.fromJson(TileManager.class, modFile);
                    if (tileManager.tileData != null) {
                        combinedTileManager.tileData.putAll(tileManager.tileData);
                    }

                    if (combinedTileManager.tiles != null) {
                        combinedTileManager.tiles.putAll(tileManager.tiles);
                    }
                }
            } catch (Exception ex) {
                Gdx.app.error("NodManager", "Error loading mod file " + path + "/data/tiles.dat");
            }
        }

        return combinedTileManager;
    }

    public ShaderManager loadShaderManager() {
        ShaderManager combinedShaders = new ShaderManager();
        ShaderManager.loaded = true;
        for (String path : this.modsFound) {
            try {
                FileHandle modFile = Game.getInternal(path + "/data/shaders.dat");
                if ((modFile.exists()) && (!pathIsExcluded(path + "/data/shaders.dat"))) {
                    ShaderData[] shaders = Game.fromJson(ShaderData[].class, modFile);
                    for (ShaderData sd : shaders) {
                        combinedShaders.shaders.put(sd.name, sd);
                    }
                }
            }
            catch (Exception ex)
            {
                Gdx.app.error("Delver", "Error loading mod file " + path + "/data/shaders.dat: " + ex.getMessage());
            }
        }
        return combinedShaders;
    }

    public LerpedAnimationManager loadAnimationManager() {
        LerpedAnimationManager animationManager = new LerpedAnimationManager();

        for (String path : modsFound) {
            try {
                FileHandle modFile = Game.getInternal(path + "/data/animations.dat");
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/animations.dat")) {
                    LerpedAnimationManager modManager = Game.fromJson(LerpedAnimationManager.class, modFile);
                    animationManager.animations.putAll(modManager.animations);
                }
            } catch (Exception ex) {
                Gdx.app.error("ModManager", "Error loading mod file " + path + "/data/animations.dat");
            }
        }

        animationManager.decorationCharge = animationManager.getAnimation("decorationCharge");
        return animationManager;
    }

    public HashMap<String, LocalizedString> loadLocalizedStrings() {
        HashMap<String, LocalizedString> combinedLocalizedStrings = new HashMap<>();

        for (String path : modsFound) {
            try {
                FileHandle modFile = Game.getInternal(path + "/data/strings.dat");
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/strings.dat")) {
                    HashMap<String, LocalizedString> localizedStrings = (HashMap)Game.fromJson(HashMap.class, modFile);
                    if (!localizedStrings.isEmpty()) {
                        combinedLocalizedStrings.putAll(localizedStrings);
                    }
                }
            } catch (Exception ex) {
                Gdx.app.error("ModManager", "Error loading mod file " + path + "/data/strings.dat");
            }
        }

        return combinedLocalizedStrings;
    }

    public GenTheme loadTheme(String filename) {
        GenTheme combinedTheme = new GenTheme();

        for (String path : modsFound) {
            FileHandle modFile = Game.getInternal(path + "/" + filename);
            if (modFile.exists() && !this.pathIsExcluded(path + "/" + filename)) {
                GenTheme theme = (GenTheme)Game.fromJson(GenTheme.class, modFile);
                if (theme.genInfos != null)
                {
                    if (combinedTheme.genInfos == null) {
                        combinedTheme.genInfos = new Array();
                    }
                    combinedTheme.genInfos.addAll(theme.genInfos);
                }
                if (theme.doors != null)
                {
                    if (combinedTheme.doors == null) {
                        combinedTheme.doors = new Array();
                    }
                    combinedTheme.doors.addAll(theme.doors);
                }
                if (theme.spawnLights != null)
                {
                    if (combinedTheme.spawnLights == null) {
                        combinedTheme.spawnLights = new Array();
                    }
                    combinedTheme.spawnLights.addAll(theme.spawnLights);
                }
                if (theme.exitUp != null) {
                    combinedTheme.exitUp = theme.exitUp;
                }
                if (theme.exitDown != null) {
                    combinedTheme.exitDown = theme.exitDown;
                }
                if (theme.decorations != null)
                {
                    if (combinedTheme.decorations == null) {
                        combinedTheme.decorations = new Array();
                    }
                    combinedTheme.decorations.addAll(theme.decorations);
                }
                if (theme.surprises != null)
                {
                    if (combinedTheme.surprises == null) {
                        combinedTheme.surprises = new Array();
                    }
                    combinedTheme.surprises.addAll(theme.surprises);
                }
                if (theme.defaultTextureAtlas != null) {
                    combinedTheme.defaultTextureAtlas = theme.defaultTextureAtlas;
                }
                if (theme.painter != null) {
                    combinedTheme.painter = theme.painter;
                }
                if (theme.texturePainters != null) {
                    combinedTheme.texturePainters = theme.texturePainters;
                }
                if (theme.chunkTiles != null) {
                    combinedTheme.chunkTiles = theme.chunkTiles;
                }
                if (theme.mapChunks != null) {
                    combinedTheme.mapChunks = theme.mapChunks;
                }
                if (theme.mapComplexity != null) {
                    combinedTheme.mapComplexity = theme.mapComplexity;
                }
                if (theme.lakes != null) {
                    combinedTheme.lakes = theme.lakes;
                }
            }
        }
        return combinedTheme;
    }

    public FileHandle findFile(String filename) {
        FileHandle foundHandle = null;

        for (String path : modsFound) {
            if (!this.pathIsExcluded(path + "/" + filename)) {
                FileHandle modFile = Game.getInternal(path + "/" + filename);
                if (modFile.exists()) {
                    foundHandle = modFile;
                }
            }
        }

        if (foundHandle == null) {
            Gdx.app.error("ModManager", "Could not find file in any mods: " + filename);
        }

        return foundHandle;
    }

    public boolean pathIsExcluded(String filename) {
        return this.excludeFiles.contains(filename, false);
    }
}
