//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALAudio;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SnapshotArray;
import java.awt.Canvas;
import java.io.File;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class LwjglApplication implements Application {
    protected final LwjglGraphics graphics;
    protected OpenALAudio audio;
    protected final LwjglFiles files;
    protected final LwjglInput input;
    protected final LwjglNet net;
    protected final ApplicationListener listener;
    protected Thread mainLoopThread;
    protected boolean running;
    protected final Array<Runnable> runnables;
    protected final Array<Runnable> executedRunnables;
    protected final SnapshotArray<LifecycleListener> lifecycleListeners;
    protected int logLevel;
    protected String preferencesdir;
    protected FileType preferencesFileType;
    ObjectMap<String, Preferences> preferences;

    public LwjglApplication(ApplicationListener listener, String title, int width, int height) {
        this(listener, createConfig(title, width, height));
    }

    public LwjglApplication(ApplicationListener listener) {
        this(listener, (String)null, 640, 480);
    }

    public LwjglApplication(ApplicationListener listener, LwjglApplicationConfiguration config) {
        this(listener, config, new LwjglGraphics(config));
    }

    public LwjglApplication(ApplicationListener listener, Canvas canvas) {
        this(listener, new LwjglApplicationConfiguration(), new LwjglGraphics(canvas));
    }

    public LwjglApplication(ApplicationListener listener, LwjglApplicationConfiguration config, Canvas canvas) {
        this(listener, config, new LwjglGraphics(canvas, config));
    }

    public LwjglApplication(ApplicationListener listener, LwjglApplicationConfiguration config, LwjglGraphics graphics) {
        this.running = true;
        this.runnables = new Array();
        this.executedRunnables = new Array();
        this.lifecycleListeners = new SnapshotArray(LifecycleListener.class);
        this.logLevel = 2;
        this.preferences = new ObjectMap();
        LwjglNativesLoader.load();
        if (config.title == null) {
            config.title = listener.getClass().getSimpleName();
        }

        this.graphics = graphics;
        if (!LwjglApplicationConfiguration.disableAudio) {
            try {
                this.audio = new OpenALAudio(config.audioDeviceSimultaneousSources, config.audioDeviceBufferCount, config.audioDeviceBufferSize);
            } catch (Throwable var5) {
                this.log("LwjglApplication", "Couldn't initialize audio, disabling audio", var5);
                LwjglApplicationConfiguration.disableAudio = true;
            }
        }

        this.files = new LwjglFiles();
        this.input = new LwjglInput();
        this.net = new LwjglNet();
        this.listener = listener;
        this.preferencesdir = config.preferencesDirectory;
        this.preferencesFileType = config.preferencesFileType;
        Gdx.app = this;
        Gdx.graphics = graphics;
        Gdx.audio = this.audio;
        Gdx.files = this.files;
        Gdx.input = this.input;
        Gdx.net = this.net;
        this.initialize();
    }

    private static LwjglApplicationConfiguration createConfig(String title, int width, int height) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = title;
        config.width = width;
        config.height = height;
        config.vSyncEnabled = true;
        return config;
    }

    private void initialize() {
        this.mainLoopThread = new Thread("LWJGL Application") {
            public void run() {
                LwjglApplication.this.graphics.setVSync(LwjglApplication.this.graphics.config.vSyncEnabled);

                try {
                    LwjglApplication.this.mainLoop();
                } catch (Throwable var2) {
                    if (LwjglApplication.this.audio != null) {
                        LwjglApplication.this.audio.dispose();
                    }

                    Gdx.input.setCursorCatched(false);
                    if (var2 instanceof RuntimeException) {
                        throw (RuntimeException)var2;
                    } else {
                        throw new GdxRuntimeException(var2);
                    }
                }
            }
        };
        this.mainLoopThread.start();
    }

    void mainLoop() {
        SnapshotArray lifecycleListeners = this.lifecycleListeners;

        try {
            this.graphics.setupDisplay();
        } catch (LWJGLException var13) {
            throw new GdxRuntimeException(var13);
        }

        this.listener.create();
        this.graphics.resize = true;
        int lastWidth = this.graphics.getWidth();
        int lastHeight = this.graphics.getHeight();
        this.graphics.lastTime = System.nanoTime();
        boolean wasActive = true;

        int i;
        int frameRate;
        while(this.running) {
            Display.processMessages();
            if (Display.isCloseRequested()) {
                this.exit();
            }

            boolean isActive = Display.isActive();
            LifecycleListener[] listeners;
            int n;
            if (wasActive && !isActive) {
                wasActive = false;
                synchronized(lifecycleListeners) {
                    listeners = (LifecycleListener[])lifecycleListeners.begin();
                    i = 0;
                    n = lifecycleListeners.size;

                    while(true) {
                        if (i >= n) {
                            lifecycleListeners.end();
                            break;
                        }

                        listeners[i].pause();
                        ++i;
                    }
                }

                this.listener.pause();
            }

            if (!wasActive && isActive) {
                wasActive = true;
                synchronized(lifecycleListeners) {
                    listeners = (LifecycleListener[])lifecycleListeners.begin();
                    i = 0;
                    n = lifecycleListeners.size;

                    while(true) {
                        if (i >= n) {
                            lifecycleListeners.end();
                            break;
                        }

                        listeners[i].resume();
                        ++i;
                    }
                }

                this.listener.resume();
            }

            boolean shouldRender = false;
            if (this.graphics.canvas != null) {
                frameRate = this.graphics.canvas.getWidth();
                i = this.graphics.canvas.getHeight();
                if (lastWidth != frameRate || lastHeight != i) {
                    lastWidth = frameRate;
                    lastHeight = i;
                    Gdx.gl.glViewport(0, 0, frameRate, i);
                    this.listener.resize(frameRate, i);
                    shouldRender = true;
                }
            } else {
                this.graphics.config.x = Display.getX();
                this.graphics.config.y = Display.getY();
                if (this.graphics.resize || Display.wasResized() || (int)((float)Display.getWidth() * Display.getPixelScaleFactor()) != this.graphics.config.width || (int)((float)Display.getHeight() * Display.getPixelScaleFactor()) != this.graphics.config.height) {
                    this.graphics.resize = false;
                    this.graphics.config.width = (int)((float)Display.getWidth() * Display.getPixelScaleFactor());
                    this.graphics.config.height = (int)((float)Display.getHeight() * Display.getPixelScaleFactor());
                    Gdx.gl.glViewport(0, 0, this.graphics.config.width, this.graphics.config.height);
                    if (this.listener != null) {
                        this.listener.resize(this.graphics.config.width, this.graphics.config.height);
                    }

                    this.graphics.requestRendering();
                }
            }

            if (this.executeRunnables()) {
                shouldRender = true;
            }

            if (!this.running) {
                break;
            }

            this.input.update();
            shouldRender |= this.graphics.shouldRender();
            this.input.processEvents();
            if (this.audio != null) {
                this.audio.update();
            }

            if (!isActive && this.graphics.config.backgroundFPS == -1) {
                shouldRender = false;
            }

            frameRate = isActive ? this.graphics.config.foregroundFPS : this.graphics.config.backgroundFPS;
            if (shouldRender) {
                this.graphics.updateTime();
                ++this.graphics.frameId;
                this.listener.render();
                Display.update(false);
            } else {
                if (frameRate == -1) {
                    frameRate = 10;
                }

                if (frameRate == 0) {
                    frameRate = this.graphics.config.backgroundFPS;
                }

                if (frameRate == 0) {
                    frameRate = 30;
                }
            }

            if (frameRate > 0) {
                Display.sync(frameRate);
            }
        }

        synchronized(lifecycleListeners) {
            LifecycleListener[] listeners = (LifecycleListener[])lifecycleListeners.begin();
            frameRate = 0;
            i = lifecycleListeners.size;

            while(true) {
                if (frameRate >= i) {
                    lifecycleListeners.end();
                    break;
                }

                listeners[frameRate].pause();
                listeners[frameRate].dispose();
                ++frameRate;
            }
        }

        this.listener.pause();
        this.listener.dispose();
        Display.destroy();
        if (this.audio != null) {
            this.audio.dispose();
        }

        if (this.graphics.config.forceExit) {
            System.exit(-1);
        }

    }

    public boolean executeRunnables() {
        Array var1 = this.runnables;
        synchronized(this.runnables) {
            int i = this.runnables.size - 1;

            while(true) {
                if (i < 0) {
                    this.runnables.clear();
                    break;
                }

                this.executedRunnables.add(this.runnables.get(i));
                --i;
            }
        }

        if (this.executedRunnables.size == 0) {
            return false;
        } else {
            do {
                ((Runnable)this.executedRunnables.pop()).run();
            } while(this.executedRunnables.size > 0);

            return true;
        }
    }

    public ApplicationListener getApplicationListener() {
        return this.listener;
    }

    public Audio getAudio() {
        return this.audio;
    }

    public Files getFiles() {
        return this.files;
    }

    public LwjglGraphics getGraphics() {
        return this.graphics;
    }

    public Input getInput() {
        return this.input;
    }

    public Net getNet() {
        return this.net;
    }

    public ApplicationType getType() {
        return ApplicationType.Desktop;
    }

    public int getVersion() {
        return 0;
    }

    public void stop() {
        this.running = false;

        try {
            this.mainLoopThread.join();
        } catch (Exception var2) {
            ;
        }

    }

    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public long getNativeHeap() {
        return this.getJavaHeap();
    }

    public Preferences getPreferences(String name) {
        if (this.preferences.containsKey(name)) {
            return (Preferences)this.preferences.get(name);
        } else {
            Preferences prefs = new LwjglPreferences(new LwjglFileHandle(new File(this.preferencesdir, name), this.preferencesFileType));
            this.preferences.put(name, prefs);
            return prefs;
        }
    }

    public Clipboard getClipboard() {
        return new LwjglClipboard();
    }

    public void postRunnable(Runnable runnable) {
        Array var2 = this.runnables;
        synchronized(this.runnables) {
            this.runnables.add(runnable);
            Gdx.graphics.requestRendering();
        }
    }

    public void debug(String tag, String message) {
        if (this.logLevel >= 3) {
            System.out.println(tag + ": " + message);
        }

    }

    public void debug(String tag, String message, Throwable exception) {
        if (this.logLevel >= 3) {
            System.out.println(tag + ": " + message);
            exception.printStackTrace(System.out);
        }

    }

    public void log(String tag, String message) {
        if (this.logLevel >= 2) {
            System.out.println(tag + ": " + message);
        }

    }

    public void log(String tag, String message, Throwable exception) {
        if (this.logLevel >= 2) {
            System.out.println(tag + ": " + message);
            exception.printStackTrace(System.out);
        }

    }

    public void error(String tag, String message) {
        if (this.logLevel >= 1) {
            System.err.println(tag + ": " + message);
        }

    }

    public void error(String tag, String message, Throwable exception) {
        if (this.logLevel >= 1) {
            System.err.println(tag + ": " + message);
            exception.printStackTrace(System.err);
        }

    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    public void exit() {
        this.postRunnable(new Runnable() {
            public void run() {
                LwjglApplication.this.running = false;
            }
        });
    }

    public void addLifecycleListener(LifecycleListener listener) {
        SnapshotArray var2 = this.lifecycleListeners;
        synchronized(this.lifecycleListeners) {
            this.lifecycleListeners.add(listener);
        }
    }

    public void removeLifecycleListener(LifecycleListener listener) {
        SnapshotArray var2 = this.lifecycleListeners;
        synchronized(this.lifecycleListeners) {
            this.lifecycleListeners.removeValue(listener, true);
        }
    }
}
