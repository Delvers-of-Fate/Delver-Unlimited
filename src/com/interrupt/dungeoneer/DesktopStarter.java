package com.interrupt.dungeoneer;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.interrupt.api.steam.SteamApi;
import com.interrupt.dungeoneer.steamapi.SteamDesktopApi;
import net.cotd.delverunlimited.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DesktopStarter
{

    private static String jarFileName = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName()
            .replaceAll("%20", " ") // space
            .replaceAll("%23", "#")
            .replaceAll("%25", "%")
            .replaceAll("%5b", "[")
            .replaceAll("%5d", "]")
            .replaceAll("%7b", "{")
            .replaceAll("%7d", "}");

    public static void main(String[] args)
    {

        checkArgs(args);

        if(Config.isRelease) {
            System.out.println("Running Delver-Unlimited v" + Config.OfflineVer + " by the CotD team");
        } else {
            System.out.println("Running Delver-Unlimited v" + Config.OfflineVer + " NON-RELEASE by the CotD team");
            System.out.println("Test build given out by the developer, and should not be redistributed!");
        }

        System.out.println("This is a modded version of Delver! Do NOT report bugs to the developers until you're sure that it isn't caused by the mod!");

        // does folder exist?
        Path settingsDir = Paths.get("save");
        if(!java.nio.file.Files.isDirectory(settingsDir)) {
            System.out.println("Settings: directory not found!");
            try {
                java.nio.file.Files.createDirectories(settingsDir);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Settings: directory created!");
            }
        }


        Graphics.DisplayMode defaultMode = LwjglApplicationConfiguration.getDesktopDisplayMode();

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Delver";
        config.fullscreen = false;

        if(Config.width != 0) {
            config.width = Config.width;
        } else {
            config.width = defaultMode.width;
        }

        if(Config.height != 0) {
            config.height = Config.height;
        } else {
            config.height = defaultMode.height;
        }

        if(Config.borderless) {
            // enforce fullscreen res
            config.width = defaultMode.width;
            config.height = defaultMode.height;

            config.x = 2;
            config.y = 3;

            // because there's no real borderless mode in lwjgl
            System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");

        }

        // set title for macOS
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Delver");

        config.vSyncEnabled = Config.useVsync;
        // config.useGL30 = true; // new stuff?
        config.samples = Config.msaaSamples; // enable MSAA
        config.stencil = 8;

        config.addIcon("icon-16.png", Files.FileType.Internal);  // 16x16 icon (Windows)
        config.addIcon("icon-32.png", Files.FileType.Internal);  // 32x32 icon (Windows + Linux)
        config.addIcon("icon-128.png", Files.FileType.Internal); // 128x128 icon (mac OS)

        new LwjglApplication(new GameApplication(), config);

        if(!Config.skipSteam) {
            SteamApi.api = new SteamDesktopApi();
            SteamApi.api.init();
        }
    }

    /**
     * Takes care about the command line arguments that end-users may pass.
     *
     * @param theArgs the command line arguments passed when calling the application
     */
    private static void checkArgs(String theArgs[]) {
        for (String arg : theArgs) {
            // debugging on
            if (arg.toLowerCase().equals("--debug")) {
                com.interrupt.dungeoneer.game.Game.isDebugMode = true;

            } else if (arg.toLowerCase().equals("--vsync")) {
                Config.useVsync = true;

            } else if (arg.toLowerCase().contains("--maxfps=")) {
                Config.maxFPS = Integer.parseInt(arg.substring(9));

            } else if (arg.toLowerCase().contains("--width=")) {
                Config.width = Integer.parseInt(arg.substring(8));

            } else if (arg.toLowerCase().contains("--height=")) {
                Config.height = Integer.parseInt(arg.substring(9));

            } else if (arg.toLowerCase().contains("--borderless")) {
                Config.borderless = true;

            } else if (arg.toLowerCase().contains("--msaa=")) {
                Config.msaaSamples = Integer.parseInt(arg.substring(7));

            } else if (arg.toLowerCase().equals("--steam")) {
                Config.skipSteam = true;

            } else if (arg.toLowerCase().equals("--drawdebugboxes")) {
                com.interrupt.dungeoneer.game.Game.drawDebugBoxes = true;

            } else if (arg.toLowerCase().equals("--skipintro")) {
                Config.skipIntro = true;

            } else if (arg.toLowerCase().contains("--save=")) {
                Config.forceSave = Integer.parseInt(arg.substring(7));

            } else if (arg.toLowerCase().equals("--help")) {
                System.out.println();
                System.out.println("Usage: java -jar " + jarFileName + " [ARGS]");
                System.out.println();
                System.out.println("--debug                Enables debugging mode, also known as developer mode.");
                System.out.println("--vsync                Enables vsync.");
                System.out.println("--maxfps=<int>         Sets max fps.");
                System.out.println("--width=<int>          Set custom width.");
                System.out.println("--height<int>          Set custom height.");
                System.out.println("--borderless           Enable borderless mode.");
                System.out.println("--msaa=<int>           Number of msaa samples.");
                System.out.println("--steam                Disable Steamworks addon.");
                System.out.println("--drawdebugboxes       Draws debug boxes?");
                System.out.println("--skipintro            Skips the intro videos.");
                System.out.println("--save=<int>           Skips the main menu and goes strait to the selected save file, must be used with the above.");
                System.out.println("--help                 Displays this message.");
                System.out.println();

                // unknown command, right?
            } else {
                System.out.println("Unknown command '" + arg + "'");
            }
        }

        if(com.interrupt.dungeoneer.game.Game.isDebugMode) {
            for (String theArg : theArgs) {
                System.out.println("Arg: " + theArg);
            }
        }
    }

}
