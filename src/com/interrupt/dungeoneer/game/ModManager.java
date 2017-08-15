package com.interrupt.dungeoneer.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.interrupt.api.steam.SteamApi;
import com.interrupt.dungeoneer.generator.GenTheme;
import com.interrupt.dungeoneer.gfx.TextureAtlas;
import com.interrupt.dungeoneer.gfx.animation.lerp3d.LerpedAnimationManager;
import com.interrupt.managers.EntityManager;
import com.interrupt.managers.ItemManager;
import com.interrupt.managers.MonsterManager;
import com.interrupt.managers.TileManager;
import net.cotd.delverunlimited.helper.Mod;
import net.cotd.delverunlimited.helper.ModInfo;
import net.cotd.delverunlimited.helper.ModSettings;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

public class ModManager {
    public Array<String> modsFound = new Array(); // MODS LOAD FROM THIS ARRAY!!
    public Array<String> excludeFiles = new Array();
    public Array<Mod> modList = new Array<Mod>();
    public Array<ModInfo> modInfo = new Array<ModInfo>();
    public String MOD_SETTINGS_FILE = "mod_settings.json";
    public String MOD_INFO_FILE = "mod_info.json";

    public ModManager() {
        this.modsFound.add(".");

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
        if(!modInfoFile.exists() && !modInfoFile.isDirectory()) {
            Gdx.app.log("ModManager", modFolder.toString() + " did not have a mod_info.json and will not load! Notify the developer of the mod to add support for Delver-Unlimited!");
            return;
        }

        Gdx.app.log("ModManager.addMod()", modFolder.getAbsolutePath());

        ModSettings settingsFile = null;
        if(modSettingsFile.exists()) {
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

        if(finishedMod.modState == null) {
            finishedMod.modState = ModSettings.ModState.Enabled;
        }

        modList.add(finishedMod);
    }

    private void loadExcludesList() {
        Iterator var1 = this.modsFound.iterator();

        while(var1.hasNext()) {
            String path = (String)var1.next();

            try {
                FileHandle modFile = Game.getInternal(path + "/data/excludes.dat");
                if (modFile.exists()) {
                    Array<String> excludes = (Array)Game.fromJson(Array.class, modFile);
                    if (excludes != null) {
                        this.excludeFiles.addAll(excludes);
                    }
                }
            } catch (Exception var5) {
                Gdx.app.error("Delver", "Error loading mod file " + path + "/data/excludes.dat");
            }
        }

    }

    public EntityManager loadEntityManager() {
        EntityManager entityManager = null;
        Iterator var2 = this.modsFound.iterator();

        while(var2.hasNext()) {
            String path = (String)var2.next();

            try {
                FileHandle modFile = Game.getInternal(path + "/data/entities.dat");
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/entities.dat")) {
                    EntityManager thisModManager = (EntityManager)Game.fromJson(EntityManager.class, modFile);
                    if (entityManager == null) {
                        entityManager = thisModManager;
                    } else if (thisModManager != null) {
                        entityManager.merge(thisModManager);
                    }
                }
            } catch (Exception var6) {
                Gdx.app.error("Delver", "Error loading mod file " + path + "/data/entities.dat");
            }
        }

        return entityManager;
    }

    public ItemManager loadItemManager() {
        ItemManager itemManager = null;
        Iterator var2 = this.modsFound.iterator();

        while(var2.hasNext()) {
            String path = (String)var2.next();

            try {
                FileHandle modFile = Game.getInternal(path + "/data/items.dat");
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/items.dat")) {
                    ItemManager thisModManager = (ItemManager)Game.fromJson(ItemManager.class, modFile);
                    if (itemManager == null) {
                        itemManager = thisModManager;
                    } else if (thisModManager != null) {
                        itemManager.merge(thisModManager);
                    }
                }
            } catch (Exception var6) {
                Gdx.app.error("Delver", "Error loading mod file " + path + "/data/items.dat");
            }
        }

        return itemManager;
    }

    public MonsterManager loadMonsterManager() {
        MonsterManager monsterManager = null;
        Iterator var2 = this.modsFound.iterator();

        while(var2.hasNext()) {
            String path = (String)var2.next();

            try {
                FileHandle modFile = Game.getInternal(path + "/data/monsters.dat");
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/monsters.dat")) {
                    MonsterManager thisModManager = (MonsterManager)Game.fromJson(MonsterManager.class, modFile);
                    if (monsterManager == null) {
                        monsterManager = thisModManager;
                    } else if (thisModManager != null) {
                        monsterManager.merge(thisModManager);
                    }
                }
            } catch (Exception var6) {
                Gdx.app.error("Delver", "Error loading mod file " + path + "/data/monsters.dat");
            }
        }

        return monsterManager;
    }

    public TextureAtlas[] getTextureAtlases(String filename) {
        ArrayMap<String, TextureAtlas> combinedAtlases = new ArrayMap();
        Iterator var3 = this.modsFound.iterator();

        while(var3.hasNext()) {
            String path = (String)var3.next();

            try {
                FileHandle modFile = Game.getInternal(path + "/data/" + filename);
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/" + filename)) {
                    TextureAtlas[] atlases = (TextureAtlas[])Game.fromJson(TextureAtlas[].class, modFile);

                    for(int i = 0; i < atlases.length; ++i) {
                        combinedAtlases.put(atlases[i].name, atlases[i]);
                    }
                }
            } catch (Exception var8) {
                Gdx.app.error("Delver", "Error loading mod file " + path + "/" + filename);
            }
        }

        TextureAtlas[] atlasArray = new TextureAtlas[combinedAtlases.size];

        for(int i = 0; i < combinedAtlases.size; ++i) {
            atlasArray[i] = (TextureAtlas)combinedAtlases.getValueAt(i);
        }

        return atlasArray;
    }

    public TileManager loadTileManager() {
        TileManager combinedTileManager = new TileManager();
        Iterator var2 = this.modsFound.iterator();

        while(var2.hasNext()) {
            String path = (String)var2.next();

            try {
                FileHandle modFile = Game.getInternal(path + "/data/tiles.dat");
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/tiles.dat")) {
                    TileManager tileManager = (TileManager)Game.fromJson(TileManager.class, modFile);
                    if (tileManager.tileData != null) {
                        combinedTileManager.tileData.putAll(tileManager.tileData);
                    }

                    if (combinedTileManager.tiles != null) {
                        combinedTileManager.tiles.putAll(tileManager.tiles);
                    }
                }
            } catch (Exception var6) {
                Gdx.app.error("Delver", "Error loading mod file " + path + "/data/tiles.dat");
            }
        }

        return combinedTileManager;
    }

    public LerpedAnimationManager loadAnimationManager() {
        LerpedAnimationManager animationManager = new LerpedAnimationManager();
        Iterator var2 = this.modsFound.iterator();

        while(var2.hasNext()) {
            String path = (String)var2.next();

            try {
                FileHandle modFile = Game.getInternal(path + "/data/animations.dat");
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/animations.dat")) {
                    LerpedAnimationManager modManager = (LerpedAnimationManager)Game.fromJson(LerpedAnimationManager.class, modFile);
                    animationManager.animations.putAll(modManager.animations);
                }
            } catch (Exception var6) {
                Gdx.app.error("Delver", "Error loading mod file " + path + "/data/animations.dat");
            }
        }

        animationManager.decorationCharge = animationManager.getAnimation("decorationCharge");
        return animationManager;
    }

    public HashMap<String, LocalizedString> loadLocalizedStrings() {
        HashMap<String, LocalizedString> combinedLocalizedStrings = new HashMap();
        Iterator var2 = this.modsFound.iterator();

        while(var2.hasNext()) {
            String path = (String)var2.next();

            try {
                FileHandle modFile = Game.getInternal(path + "/data/strings.dat");
                if (modFile.exists() && !this.pathIsExcluded(path + "/data/strings.dat")) {
                    HashMap<String, LocalizedString> localizedStrings = (HashMap)Game.fromJson(HashMap.class, modFile);
                    if (!localizedStrings.isEmpty()) {
                        combinedLocalizedStrings.putAll(localizedStrings);
                    }
                }
            } catch (Exception var6) {
                Gdx.app.error("Delver", "Error loading mod file " + path + "/data/strings.dat");
            }
        }

        return combinedLocalizedStrings;
    }

    public GenTheme loadTheme(String filename) {
        GenTheme combinedTheme = new GenTheme();
        Iterator var3 = this.modsFound.iterator();

        while(var3.hasNext()) {
            String path = (String)var3.next();
            FileHandle modFile = Game.getInternal(path + "/" + filename);
            if (modFile.exists() && !this.pathIsExcluded(path + "/" + filename)) {
                GenTheme theme = (GenTheme)Game.fromJson(GenTheme.class, modFile);
                if (theme.genInfos != null) {
                    if (combinedTheme.genInfos == null) {
                        combinedTheme.genInfos = new Array();
                    }

                    combinedTheme.genInfos.addAll(theme.genInfos);
                }

                if (theme.doors != null) {
                    if (combinedTheme.doors == null) {
                        combinedTheme.doors = new Array();
                    }

                    combinedTheme.doors.addAll(theme.doors);
                }

                if (theme.spawnLights != null) {
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

                if (theme.decorations != null) {
                    if (combinedTheme.decorations == null) {
                        combinedTheme.decorations = new Array();
                    }

                    combinedTheme.decorations.addAll(theme.decorations);
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
            }
        }

        return combinedTheme;
    }

    public FileHandle findFile(String filename) {
        FileHandle foundHandle = null;
        Iterator var3 = this.modsFound.iterator();

        while(var3.hasNext()) {
            String path = (String)var3.next();
            if (!this.pathIsExcluded(path + "/" + filename)) {
                FileHandle modFile = Game.getInternal(path + "/" + filename);
                if (modFile.exists()) {
                    foundHandle = modFile;
                }
            }
        }

        if (foundHandle == null) {
            Gdx.app.error("Delver", "Could not find file in any mods: " + filename);
        }

        return foundHandle;
    }

    public boolean pathIsExcluded(String filename) {
        return this.excludeFiles.contains(filename, false);
    }
}
