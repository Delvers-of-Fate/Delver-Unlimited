package com.interrupt.dungeoneer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.interrupt.api.steam.SteamApi;
import com.interrupt.dungeoneer.entities.Stairs;
import com.interrupt.dungeoneer.entities.triggers.TriggeredWarp;
import com.interrupt.dungeoneer.game.GameData;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.screens.*;
import com.interrupt.managers.StringManager;
import net.cotd.delverunlimited.Config;

public class GameApplication
        extends com.badlogic.gdx.Game
{
    protected GameManager gameManager = null;
    public GameInput input = new GameInput();
    public GameScreen mainScreen;
    public GameOverScreen gameoverScreen;
    public LevelChangeScreen levelChangeScreen;
    public SplashScreen mainMenuScreen;
    public static GameApplication instance;
    public static boolean editorRunning = false;

    public void create()
    {
        instance = this;
        Gdx.app.log("DelverLifeCycle", "LibGdx Create");

        Gdx.app.setLogLevel(2);

        this.gameManager = new GameManager(this);
        Gdx.input.setInputProcessor(this.input);
        this.gameManager.init();

        this.mainMenuScreen = new SplashScreen();
        this.mainScreen = new GameScreen(this.gameManager, this.input);
        this.gameoverScreen = new GameOverScreen(this.gameManager);
        this.levelChangeScreen = new LevelChangeScreen(this.gameManager);

        // skip main menu and intro videos
        if (Config.skipIntro) {
            if (Config.forceSave != 3) {
                setScreen(new LoadingScreen(MainMenuScreen.saveGames[Config.forceSave] == null ? StringManager.get("screens.MainMenuScreen.creatingDungeon") : StringManager.get("screens.MainMenuScreen.loadingSaveSlot"), Config.forceSave));
            } else {
                setScreen(new MainMenuScreen());
            }
        } else {
            setScreen(new IntroScreen());
        }
    }

    public void createFromEditor(Level level)
    {
        instance = this;
        Gdx.app.log("DelverLifeCycle", "LibGdx Create From Editor");

        this.gameManager = new GameManager(this);
        Gdx.input.setInputProcessor(this.input);
        this.gameManager.init();

        com.interrupt.dungeoneer.game.Game.inEditor = true;
        this.mainMenuScreen = new SplashScreen();
        this.mainScreen = new GameScreen(level, this.gameManager, this.input);
        this.gameoverScreen = new GameOverScreen(this.gameManager);
        this.levelChangeScreen = new LevelChangeScreen(this.gameManager);

        setScreen(this.mainScreen);
    }

    public void dispose()
    {
        Gdx.app.log("DelverLifeCycle", "Goodbye");
        this.mainScreen.dispose();
        if (!Config.skipSteam) {
            SteamApi.api.dispose();
        }
    }

    public static void ShowMainScreen()
    {
        Gdx.input.setInputProcessor(instance.input);
        instance.setScreen(instance.mainScreen);
    }

    public static void ShowGameOverScreen(boolean escaped)
    {
        if (escaped)
        {
            GameData gameData = (GameData)com.interrupt.dungeoneer.game.Game.fromJson(GameData.class, com.interrupt.dungeoneer.game.Game.findInternalFileInMods("data/game.dat"));
            Level endingLevel = gameData.endingLevel;
            if ((endingLevel != null) && ((GameManager.getGame().level.levelFileName == null) || (!GameManager.getGame().level.levelFileName.equals(endingLevel.levelFileName))))
            {
                TriggeredWarp warp = new TriggeredWarp();
                warp.generated = endingLevel.generated;
                warp.levelToLoad = endingLevel.levelFileName;
                warp.levelTheme = endingLevel.theme;
                warp.fogColor = endingLevel.fogColor;
                warp.fogEnd = endingLevel.fogEnd;
                warp.fogStart = endingLevel.fogStart;
                warp.fogEnd = endingLevel.viewDistance;
                warp.levelName = endingLevel.levelName;
                warp.spawnMonsters = endingLevel.spawnMonsters;
                warp.objectivePrefabToSpawn = endingLevel.objectivePrefab;
                warp.skyLightColor = endingLevel.skyLightColor;

                GameManager.getGame().player.makeEscapeEffects = false;
                GameManager.getGame().warpToLevel("ending", warp);

                Audio.playMusic(endingLevel.music, true);
                return;
            }
        }
        GameManager.getGame().gameOver = true;
        instance.gameoverScreen.gameOver = (!escaped);
        instance.setScreen(instance.gameoverScreen);
    }

    public static void ShowLevelChangeScreen(Stairs stair)
    {
        instance.levelChangeScreen.stair = stair;
        instance.levelChangeScreen.triggeredWarp = null;
        instance.mainScreen.saveOnPause = false;

        instance.setScreen(instance.levelChangeScreen);
    }

    public static void ShowLevelChangeScreen(TriggeredWarp warp)
    {
        instance.levelChangeScreen.triggeredWarp = warp;
        instance.levelChangeScreen.stair = null;
        instance.mainScreen.saveOnPause = false;

        instance.setScreen(instance.levelChangeScreen);
    }

    public static void SetScreen(Screen newScreen)
    {
        instance.setScreen(newScreen);
    }

    public static void SetSaveLocation(int saveLoc)
    {
        instance.mainScreen.saveLoc = saveLoc;
    }

    public static void ShowMainMenuScreen()
    {
        instance.mainScreen.didStart = false;
        instance.setScreen(new MainMenuScreen());
    }
}
