package com.interrupt.dungeoneer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.Art;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.GameApplication;
import com.interrupt.dungeoneer.GameManager;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.game.Colors;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.game.Progression;
import com.interrupt.dungeoneer.gfx.TextureAtlas;
import com.interrupt.dungeoneer.overlays.ModsOverlay;
import com.interrupt.dungeoneer.overlays.OptionsOverlay;
import com.interrupt.dungeoneer.screens.BaseScreen.GamepadEntry;
import com.interrupt.dungeoneer.screens.BaseScreen.GamepadEntryListener;
import com.interrupt.dungeoneer.ui.UiSkin;
import com.interrupt.managers.StringManager;
import java.text.MessageFormat;
import java.util.Iterator;

public class MainMenuScreen extends BaseScreen {
    private Texture menuTexture;
    private TextureRegion[] menuRegions;
    private Table fullTable;
    private Table buttonTable;
    private TextButton playButton;
    private TextButton deleteButton;
    private TextButton optionsButton;
    public static Progression[] progress = new Progression[3];
    public static Player[] saveGames = new Player[3];
    public static Integer selectedSave;
    private boolean ignoreEscapeKey = false;
    private boolean refreshOnEscape = false;
    Array<Table> saveSlotUi = new Array();
    Player errorPlayer = new Player();
    Array<Level> dungeonInfo;
    Color fadeColor;
    boolean fadingOut;
    float fadeFactor;
    private int[] saveFiles = {0, 1, 2};

    public MainMenuScreen() {
        this.fadeColor = new Color(Color.BLACK);
        this.fadingOut = false;
        this.fadeFactor = 1.0F;
        this.dungeonInfo = Game.buildLevelLayout();
        if (splashScreenInfo != null) {
            this.splashLevel = splashScreenInfo.backgroundLevel;
        }

        this.screenName = "MainMenuScreen";
        this.menuTexture = Art.loadTexture("menu.png");
        this.menuRegions = new TextureRegion[this.menuTexture.getWidth() / 16 * (this.menuTexture.getHeight() / 16)];
        int count = 0;

        for(int y = 0; y < this.menuTexture.getHeight() / 16; ++y) {
            for(int x = 0; x < this.menuTexture.getWidth() / 16; ++x) {
                this.menuRegions[count++] = new TextureRegion(this.menuTexture, x * 16, y * 16, 16, 16);
            }
        }

        this.ui = new Stage(this.viewport);
        this.fullTable = new Table(this.skin);
        this.fullTable.setFillParent(true);
        this.fullTable.align(1);
        this.buttonTable = new Table();
        this.ui.addActor(this.fullTable);
        Gdx.input.setInputProcessor(this.ui);
    }

    public void makeContent() {
        this.gamepadEntries.clear();
        this.gamepadSelectionIndex = null;
        this.refreshOnEscape = false;
        String paddedButtonText = " {0} ";
        this.playButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.playButton")), this.skin);
        this.playButton.setColor(Colors.PLAY_BUTTON);
        this.playButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                MainMenuScreen.this.playButtonEvent(false);
            }
        });
        this.deleteButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.eraseButton")), this.skin);
        this.deleteButton.setColor(Colors.ERASE_BUTTON);
        this.deleteButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                MainMenuScreen.this.deleteButtonEvent(false);
            }
        });
        this.optionsButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.optionsButton")), this.skin);
        this.optionsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                GameApplication.SetScreen(new OverlayWrapperScreen(new OptionsOverlay(false, true)));
            }
        });
        TextButton modsButton = new TextButton(MessageFormat.format(paddedButtonText, "Mods"), this.skin);
        modsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                GameApplication.SetScreen(new OverlayWrapperScreen(new ModsOverlay()));
            }
        });
        this.fullTable.padTop(42.0F);
        this.fullTable.clearChildren();
        this.fullTable.add(StringManager.get("screens.MainMenuScreen.selectSaveSlot")).align(8).padTop(22.0F).padBottom(6.0F);
        this.fullTable.add(this.deleteButton).align(16).padTop(10.0F);
        this.fullTable.row();
        this.buttonTable.clearChildren();
        NinePatchDrawable fileSelectBg = new NinePatchDrawable(new NinePatch(this.skin.getRegion("save-select"), 6, 6, 6, 6));

        for (int i : saveFiles) {
            String saveName = StringManager.get("screens.MainMenuScreen.newGameSaveSlot");
            if (saveGames[i] != null && saveGames[i] == this.errorPlayer) {
                saveName = StringManager.get("screens.MainMenuScreen.errorSaveSlot");
            } else if (saveGames[i] != null) {
                saveName = this.getSaveName(progress[i], saveGames[i].levelNum, saveGames[i].levelName);
            } else if (progress[i] != null) {
                saveName = this.getSaveName(progress[i], null, null);
            }

            float fontScale = 1.0F;
            float rowHeight = 15.0F;
            Label fileTitle = new Label("" + (i + 1), this.skin);
            fileTitle.setFontScale(fontScale);
            fileTitle.setColor(Color.GRAY);
            final Table t = new Table(this.skin);
            t.add(fileTitle).size(20.0F, rowHeight * 2.0F).align(1);
            t.setBackground(fileSelectBg);
            t.center();
            Label locationLabel = new Label(saveName, this.skin);
            locationLabel.setFontScale(fontScale);
            Table t2 = new Table(this.skin);
            t2.align(8);
            if (progress[i] != null) {
                Label playtimeLabel = new Label(progress[i].getPlaytime(), this.skin);
                playtimeLabel.setAlignment(16);
                Table topRow = new Table();
                topRow.add(locationLabel);
                topRow.add(playtimeLabel).expand().align(16);
                t2.add(topRow).width(220.0F).padTop(2.0F);
                t2.row();
                TextureAtlas itemAtlas = TextureAtlas.cachedAtlases.get("item");
                Image goldIcon = new Image(new TextureRegionDrawable(itemAtlas.getSprite(89)));
                goldIcon.setAlign(8);
                Image skullIcon = new Image(new TextureRegionDrawable(itemAtlas.getSprite(56)));
                skullIcon.setAlign(8);
                Image orbIcon = new Image(new TextureRegionDrawable(itemAtlas.getSprite(59)));
                orbIcon.setAlign(8);
                Label goldLabel = new Label(progress[i].gold + "", this.skin);
                goldLabel.setFontScale(fontScale);
                Label deathLabel = new Label(progress[i].deaths + "", this.skin);
                deathLabel.setFontScale(fontScale);
                Label winsLabel = new Label(progress[i].wins + "", this.skin);
                winsLabel.setFontScale(fontScale);
                Table progressTable = new Table(this.skin);
                progressTable.add(goldIcon).width(20.0F).height(20.0F).align(8);
                progressTable.add(goldLabel).width(45.0F);
                progressTable.add(skullIcon).width(20.0F).height(20.0F).align(8);
                progressTable.add(deathLabel).padLeft(2.0F).width(45.0F);
                if (progress[i].wins > 0) {
                    progressTable.add(orbIcon).width(20.0F).height(20.0F).align(8);
                    progressTable.add(winsLabel).padLeft(2.0F).width(45.0F);
                }

                progressTable.pack();
                t2.add(progressTable).align(8);
            } else {
                t2.add(locationLabel).align(8).size(220.0F, rowHeight * 2.0F).padTop(2.0F).padBottom(4.0F);
            }

            t2.pack();
            t.add(t2);
            t.pack();
            t.addAction(Actions.sequence(Actions.fadeOut(1.0E-4F), Actions.delay(((float)i + 1.0F) * 0.1F), Actions.fadeIn(0.2F)));
            t.setTouchable(Touchable.enabled);
            t.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    MainMenuScreen.this.selectSaveButtonEvent(i, t);
                }
            });
            t.setColor(Color.GRAY);
            this.saveSlotUi.add(t);
            this.fullTable.add(t).height(46.0F).padTop(4.0F).colspan(2);
            this.fullTable.row();

            /**
            GamepadEntry g = new GamepadEntry(this, t, new GamepadEntryListener() {
                public void onPress() {
                    Iterator var1 = MainMenuScreen.this.playButton.getListeners().iterator();

                    while(var1.hasNext()) {
                        EventListener listener = (EventListener)var1.next();
                        if (listener instanceof ClickListener) {
                            ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.playButton.getX(), MainMenuScreen.this.playButton.getY());
                        }
                    }

                }
            }, new GamepadEntryListener() {
                public void onPress() {
                    if (MainMenuScreen.this.deleteButton.isVisible()) {
                        Iterator var1 = MainMenuScreen.this.deleteButton.getListeners().iterator();

                        while(var1.hasNext()) {
                            EventListener listener = (EventListener)var1.next();
                            if (listener instanceof ClickListener) {
                                ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.playButton.getX(), MainMenuScreen.this.playButton.getY());
                            }
                        }

                    }
                }
            });
            this.gamepadEntries.add(g);
             **/
        }

        Table playButtonTable = new Table();
        playButtonTable.add(this.playButton).align(8).height(20.0F).expand();
        this.buttonTable.add(playButtonTable).align(8).fillX().expand();
        if (this.hasMods()) {
            this.buttonTable.add(modsButton).align(16).height(20.0F);
        }

        this.buttonTable.add(this.optionsButton).align(16).height(20.0F);
        this.buttonTable.pack();
        /**
        GamepadEntry optionsEntry = new GamepadEntry(this, this.optionsButton, new GamepadEntryListener() {
            public void onPress() {
                Iterator var1 = MainMenuScreen.this.optionsButton.getListeners().iterator();

                while(var1.hasNext()) {
                    EventListener listener = (EventListener)var1.next();
                    if (listener instanceof ClickListener) {
                        ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.optionsButton.getX(), MainMenuScreen.this.optionsButton.getY());
                    }
                }

            }
        }, new GamepadEntryListener() {
            public void onPress() {
                Iterator var1 = MainMenuScreen.this.optionsButton.getListeners().iterator();

                while(var1.hasNext()) {
                    EventListener listener = (EventListener)var1.next();
                    if (listener instanceof ClickListener) {
                        ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.optionsButton.getX(), MainMenuScreen.this.optionsButton.getY());
                    }
                }

            }
        });
         **/
        //this.gamepadEntries.add(optionsEntry);
        this.fullTable.row();
        this.fullTable.add(this.buttonTable).colspan(2).height(30.0F).fill(true, false).align(1);
        this.fullTable.row();
        this.fullTable.add().colspan(2).height(60.0F);
        this.fullTable.pack();
        this.fullTable.padTop(42.0F);
        this.fullTable.addAction(Actions.sequence(Actions.fadeOut(1.0E-4F), Actions.fadeIn(0.2F)));
        this.playButton.setVisible(false);
        this.deleteButton.setVisible(false);
    }

    public void showModal(String message, String yesText, String noText, ClickListener yesListener, int width) {
        Table full = this.fullTable;
        this.fullTable.padTop(0.0F);
        full.clear();
        Label text = new Label(message, UiSkin.getSkin());
        text.setWrap(true);
        NinePatchDrawable background = new NinePatchDrawable(new NinePatch(UiSkin.getSkin().getRegion("window"), 11, 11, 11, 11));
        Table t = new Table(UiSkin.getSkin());
        t.setBackground(background);
        t.add(text).colspan(2).align(8).width((float)width).expand();
        t.row();
        final TextButton yesButton = new TextButton(" " + yesText + " ", UiSkin.getSkin());
        final TextButton noButton = new TextButton(" " + noText + " ", UiSkin.getSkin());
        if (yesListener != null) {
            yesButton.addListener(yesListener);
        }

        noButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                MainMenuScreen.this.makeContent();
            }
        });
        yesButton.setColor(Color.RED);
        t.add(noButton).align(8).expand().padTop(8.0F);
        t.add(yesButton).align(16).expand().padTop(8.0F);
        t.row();
        full.add(t);
        /**
        GamepadEntry yesEntry = new GamepadEntry(this, yesButton, new GamepadEntryListener() {
            public void onPress() {
                Iterator var1 = yesButton.getListeners().iterator();

                while(var1.hasNext()) {
                    EventListener listener = (EventListener)var1.next();
                    if (listener instanceof ClickListener) {
                        ((ClickListener)listener).clicked(new InputEvent(), yesButton.getX(), yesButton.getY());
                    }
                }

            }
        }, new GamepadEntryListener() {
            public void onPress() {
                Iterator var1 = yesButton.getListeners().iterator();

                while(var1.hasNext()) {
                    EventListener listener = (EventListener)var1.next();
                    if (listener instanceof ClickListener) {
                        ((ClickListener)listener).clicked(new InputEvent(), yesButton.getX(), yesButton.getY());
                    }
                }

            }
        });
        GamepadEntry noEntry = new GamepadEntry(this, noButton, new GamepadEntryListener() {
            public void onPress() {
                Iterator var1 = noButton.getListeners().iterator();

                while(var1.hasNext()) {
                    EventListener listener = (EventListener)var1.next();
                    if (listener instanceof ClickListener) {
                        ((ClickListener)listener).clicked(new InputEvent(), noButton.getX(), noButton.getY());
                    }
                }

            }
        }, new GamepadEntryListener() {
            public void onPress() {
                Iterator var1 = noButton.getListeners().iterator();

                while(var1.hasNext()) {
                    EventListener listener = (EventListener)var1.next();
                    if (listener instanceof ClickListener) {
                        ((ClickListener)listener).clicked(new InputEvent(), noButton.getX(), noButton.getY());
                    }
                }

            }
        });
        this.gamepadEntries.clear();
        this.gamepadEntries.add(noEntry);
        this.gamepadEntries.add(yesEntry);
         **/
        this.gamepadSelectionIndex = null;
        this.refreshOnEscape = true;
    }

    public void show() {
        super.show();
        if (Game.instance != null) {
            Game.instance.clearMemory();
        }

        this.loadSavegames();
        this.makeContent();
        this.ignoreEscapeKey = Gdx.input.isKeyPressed(131);
        this.backgroundTexture = Art.loadTexture("splash/Delver-Menu-BG.png");
        this.backgroundTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        if (splashScreenInfo.music != null) {
            Audio.playMusic(splashScreenInfo.music, true);
        }

    }

    public void draw(float delta) {
        super.draw(delta);
        this.renderer = GameManager.renderer;
        this.ui.draw();
        if (this.fadeFactor < 1.0F) {
            this.renderer.drawFlashOverlay(this.fadeColor.set(0.0F, 0.0F, 0.0F, 1.0F - this.fadeFactor));
        }

    }

    private String getSaveName(Progression p, Integer levelNum, String levelName) {
        if (p != null && p.won) {
            return StringManager.get("screens.MainMenuScreen.finishedSaveSlot");
        } else if (levelNum == null) {
            return StringManager.get("screens.MainMenuScreen.deadSaveSlot");
        } else {
            return levelNum == -1 && Game.gameData.tutorialLevel != null ? Game.gameData.tutorialLevel.levelName : levelName;
        }
    }

    public void tick(float delta) {
        super.tick(delta);
        if (Gdx.input.isKeyJustPressed(131)) {
            if (!this.ignoreEscapeKey) {
                if (this.refreshOnEscape) {
                    this.refreshOnEscape = false;
                    this.makeContent();
                } else {
                    Gdx.app.exit();
                }
            }
        } else {
            this.ignoreEscapeKey = false;
        }

        this.ui.act(delta);
        if (this.fadingOut) {
            this.fadeFactor -= delta * 0.5F;
            if (this.fadeFactor < 0.0F) {
                this.fadeFactor = 0.0F;
            }

            Audio.setMusicVolume(Math.min(1.0F, 1.0F * this.fadeFactor));
        }

    }

    public void selectSaveButtonEvent(int saveLoc, Table selected) {
        this.gamepadSelectionIndex = saveLoc;

        for(int i = 0; i < this.saveSlotUi.size; ++i) {
            this.saveSlotUi.get(i).setColor(Color.GRAY);
        }

        if (selected != null) {
            selected.setColor(Color.WHITE);
        }

        if (saveGames[saveLoc] != this.errorPlayer) {
            this.playButton.setVisible(true);
        } else {
            this.playButton.setVisible(false);
        }

        selectedSave = saveLoc;
        this.deleteButton.setVisible(saveGames[selectedSave] != null || progress[selectedSave] != null);
        Audio.playSound("/ui/ui_button_click.mp3", 0.3F);
    }

    public void playButtonEvent(boolean force) {
        Audio.playSound("/ui/ui_button_click.mp3", 0.3F);
        if (!force) {
            Progression p = progress[selectedSave];
            if (p != null) {
                Array<String> missing = p.checkForMissingMods();
                if (missing.size > 0) {
                    ClickListener playListener = new ClickListener() {
                        public void clicked(InputEvent event, float x, float y) {
                            MainMenuScreen.this.playButtonEvent(true);
                        }
                    };
                    String message = StringManager.get("screens.MainMenuScreen.missingModsWarning");
                    String missingModsString = "";

                    for(int i = 0; i < missing.size; ++i) {
                        String m = missing.get(i);
                        int maxModLength = 30;
                        if (m.length() > maxModLength) {
                            m = m.substring(0, maxModLength - 3) + "...";
                        }

                        missingModsString = missingModsString + m + "\n";
                    }

                    this.showModal(MessageFormat.format(message, missingModsString), StringManager.get("screens.MainMenuScreen.playButton"), StringManager.get("screens.MainMenuScreen.cancelButton"), playListener, 260);
                    return;
                }
            }
        }

        this.fullTable.addAction(Actions.sequence(Actions.fadeOut(0.3F), Actions.delay(0.5F), Actions.addAction(new Action() {
            public boolean act(float v) {
                MainMenuScreen.this.fadingOut = true;
                return true;
            }
        }), Actions.delay(1.75F), Actions.addAction(new Action() {
            public boolean act(float v) {
                GameApplication.SetScreen(new LoadingScreen(saveGames[selectedSave] == null ? StringManager.get("screens.MainMenuScreen.creatingDungeon") : StringManager.get("screens.MainMenuScreen.loadingSaveSlot"), selectedSave));
                return true;
            }
        })));
    }

    public void deleteButtonEvent(boolean force) {
        if (!force) {
            ClickListener eraseListener = new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    MainMenuScreen.this.deleteButtonEvent(true);
                }
            };
            this.showModal(StringManager.get("screens.MainMenuScreen.eraseSaveWarning"), StringManager.get("screens.MainMenuScreen.eraseButton"), StringManager.get("screens.MainMenuScreen.cancelButton"), eraseListener, 220);
        } else {
            saveGames[selectedSave] = null;
            progress[selectedSave] = null;
            this.deleteSavegame(selectedSave);
            selectedSave = null;
        }
    }

    private void loadSavegames() {
        String baseSaveDir = "save/";
        FileHandle dir = Game.getFile(baseSaveDir);
        Gdx.app.log("DelverLifeCycle", "Getting savegames from " + dir.path());

        int i;
        FileHandle file;
        for(i = 0; i < saveGames.length; ++i) {
            file = Game.getFile(baseSaveDir + "/" + i + "/player.dat");
            if (file.exists()) {
                try {
                    saveGames[i] = Game.fromJson(Player.class, file);
                } catch (Exception var7) {
                    saveGames[i] = this.errorPlayer;
                }
            }
        }

        for(i = 0; i < saveGames.length; ++i) {
            file = Game.getFile(baseSaveDir + "/game_" + i + ".dat");
            if (file.exists()) {
                try {
                    progress[i] = Game.fromJson(Progression.class, file);
                } catch (Exception var6) {
                    progress[i] = null;
                }
            }
        }

    }

    private void deleteSavegame(int saveLoc) {
        String baseSaveDir;
        FileHandle file;
        try {
            baseSaveDir = "save/";
            file = Game.getFile(baseSaveDir + "/" + saveLoc + "/");
            Gdx.app.log("DelverLifeCycle", "Deleting savegame " + file.path());
            file.deleteDirectory();
        } catch (Exception var5) {
        }

        try {
            baseSaveDir = "save/";
            file = Game.getFile(baseSaveDir + "/game_" + saveLoc + ".dat");
            Gdx.app.log("DelverLifeCycle", "Deleting progress " + file.path());
            file.delete();
        } catch (Exception var4) {
        }

        this.makeContent();
    }

    private boolean hasMods() {
        if (Game.modManager == null) {
            return false;
        } else {
            return Game.modManager.modsFound != null && Game.modManager.hasExtraMods();
        }
    }
}
