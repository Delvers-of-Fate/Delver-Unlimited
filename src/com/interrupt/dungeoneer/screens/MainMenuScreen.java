package com.interrupt.dungeoneer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
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
import com.interrupt.dungeoneer.overlays.OptionsOverlay;
import com.interrupt.managers.StringManager;

import java.text.MessageFormat;

public class MainMenuScreen extends BaseScreen {
    private Texture menuTexture;
    private TextureRegion[] menuRegions;
    private Table fullTable = null;
    private Table buttonTable = null;
    private TextButton playButton;
    private TextButton deleteButton;
    private TextButton optionsButton;
    private TextButton exitButton;
    public static Progression[] progress = new Progression[3];
    public static Player[] saveGames = new Player[3];
    public static Integer selectedSave;
    private boolean isSelected = false;
    private Table selectedSaveTable = null;
    private boolean ignoreEscapeKey = false;
    Array<Table> saveSlotUi = new Array();
    Player errorPlayer = new Player();
    Array<Level> dungeonInfo = null;
    private int[] saveFiles = {0, 1, 2};
    Color fadeColor;
    boolean fadingOut;
    float fadeFactor;

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

        // reset stuff
        isSelected = false;
        selectedSaveTable = null;
        selectedSave = null;
    }

    public void makeContent() {
        this.gamepadEntries.clear();
        this.gamepadSelectionIndex = null;
        String paddedButtonText = " {0} ";
        this.playButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.playButton")), this.skin);
        this.playButton.setColor(Colors.PLAY_BUTTON);
        this.playButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                MainMenuScreen.this.playButtonEvent();
            }
        });
        this.deleteButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.eraseButton")), this.skin);
        this.deleteButton.setColor(Colors.PARALYZE);
        this.deleteButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                MainMenuScreen.this.deleteButtonEvent();
            }
        });
        this.optionsButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.optionsButton")), this.skin);
        this.optionsButton.setColor(Color.SKY);
        this.optionsButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                GameApplication.SetScreen(new OverlayWrapperScreen(new OptionsOverlay(false, true)));
            }
        });
        this.exitButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.exitButton")), this.skin);
        this.exitButton.setColor(Colors.ERASE_BUTTON);
        this.exitButton.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                GameApplication.SetScreen(new ConfirmExitScreen());
            }
        });
        this.fullTable.row();
        this.fullTable.add(StringManager.get("screens.MainMenuScreen.selectSaveSlot")).padTop(3F).padBottom(3F);
        this.fullTable.row();
        this.buttonTable.clearChildren();

        NinePatchDrawable fileSelectBg = new NinePatchDrawable(new NinePatch(this.skin.getRegion("save-select"), 1, 1, 1, 1));
        for (int i : saveFiles) {
            Player save = saveGames[i];
            String saveName = StringManager.get("screens.MainMenuScreen.newGameSaveSlot");
            if (this.saveGames[i] != null && this.saveGames[i] == this.errorPlayer) {
                saveName = StringManager.get("screens.MainMenuScreen.errorSaveSlot");
            } else if (this.saveGames[i] != null) {
                saveName = this.getSaveName(this.progress[i], this.saveGames[i].levelNum, this.saveGames[i].levelName);
            } else if (this.progress[i] != null) {
                saveName = this.getSaveName(this.progress[i], (Integer)null, (String)null);
            }

            if (save != null && save != this.errorPlayer) {
                saveName = MessageFormat.format(StringManager.get("screens.MainMenuScreen.infoSaveSlot"), saveName, save.level);
            }

            float fontScale = 1.0F;
            float rowHeight = 15.0F;
            Label fileTitle = new Label("" + (i + 1), this.skin);
            fileTitle.setFontScale(fontScale);
            final Table t = new Table(this.skin);
            t.add(fileTitle).size(20.0F, rowHeight * 2.0F).align(1);
            t.setBackground(fileSelectBg);
            t.center();
            Label locationLabel = new Label(saveName, this.skin);
            locationLabel.setFontScale(fontScale);
            Table t2 = new Table(this.skin);
            t2.align(8);
            if (this.progress[i] != null) {
                t2.add(locationLabel).align(8).size(220.0F, rowHeight).padTop(2.0F);
                t2.row();
                Image goldIcon = new Image(new TextureRegionDrawable(((TextureAtlas)TextureAtlas.cachedAtlases.get("item")).sprite_regions[89]));
                goldIcon.setAlign(8);
                Image skullIcon = new Image(new TextureRegionDrawable(((TextureAtlas)TextureAtlas.cachedAtlases.get("item")).sprite_regions[56]));
                skullIcon.setAlign(8);
                Image orbIcon = new Image(new TextureRegionDrawable(((TextureAtlas)TextureAtlas.cachedAtlases.get("item")).sprite_regions[59]));
                orbIcon.setAlign(8);
                Label goldLabel = new Label(this.progress[i].gold + "", this.skin);
                goldLabel.setFontScale(fontScale);
                Label deathLabel = new Label(this.progress[i].deaths + "", this.skin);
                deathLabel.setFontScale(fontScale);
                Label winsLabel = new Label(this.progress[i].wins + "", this.skin);
                winsLabel.setFontScale(fontScale);
                Table progressTable = new Table(this.skin);
                progressTable.add(goldIcon).width(20.0F).height(20.0F).align(8);
                progressTable.add(goldLabel).width(45.0F);
                progressTable.add(skullIcon).width(20.0F).height(20.0F).align(8);
                progressTable.add(deathLabel).padLeft(2.0F).width(45.0F);
                if (this.progress[i].wins > 0) {
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
                    MainMenuScreen.this.selectSaveButtonEvent(i, t, getTapCount());
                }
            });
            t.setColor(Color.GRAY);
            this.saveSlotUi.add(t);
            this.fullTable.add(t).height(46.0F).padTop(4.0F).colspan(2);
            this.fullTable.row();
            BaseScreen.GamepadEntry g = new BaseScreen.GamepadEntry(t, new BaseScreen.GamepadEntryListener(){

                @Override
                public void onPress() {
                    for (EventListener listener : MainMenuScreen.this.playButton.getListeners()) {
                        if (!(listener instanceof ClickListener)) continue;
                        ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.playButton.getX(), MainMenuScreen.this.playButton.getY());
                    }
                }
            }, new BaseScreen.GamepadEntryListener(){

                @Override
                public void onPress() {
                    if (!MainMenuScreen.this.deleteButton.isVisible()) {
                        return;
                    }
                    for (EventListener listener : MainMenuScreen.this.deleteButton.getListeners()) {
                        if (!(listener instanceof ClickListener)) continue;
                        ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.playButton.getX(), MainMenuScreen.this.playButton.getY());
                    }
                }
            });
            this.gamepadEntries.add(g);
        }
        this.buttonTable.add().width(2.0F);
        this.buttonTable.add(this.playButton).height(20.0F);
        this.buttonTable.add(this.deleteButton).height(20.0F);
        this.buttonTable.add().width(36.0F);
        this.buttonTable.add(this.exitButton).height(20.0F);
        this.buttonTable.add(this.optionsButton).height(20.0F);
        this.buttonTable.pack();

        // play button
        BaseScreen.GamepadEntry playEntry = new BaseScreen.GamepadEntry(this.playButton, new BaseScreen.GamepadEntryListener(){
            @Override
            public void onPress() {
                for (EventListener listener : MainMenuScreen.this.playButton.getListeners()) {
                    if (!(listener instanceof ClickListener)) continue;
                    ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.playButton.getX(), MainMenuScreen.this.playButton.getY());
                }
            }
        }, new BaseScreen.GamepadEntryListener(){

            @Override
            public void onPress() {
                for (EventListener listener : MainMenuScreen.this.playButton.getListeners()) {
                    if (!(listener instanceof ClickListener)) continue;
                    ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.playButton.getX(), MainMenuScreen.this.playButton.getY());
                }
            }
        });

        // options button
        BaseScreen.GamepadEntry optionsEntry = new BaseScreen.GamepadEntry(this.optionsButton, new BaseScreen.GamepadEntryListener(){
            @Override
            public void onPress() {
                for (EventListener listener : MainMenuScreen.this.optionsButton.getListeners()) {
                    if (!(listener instanceof ClickListener)) continue;
                    ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.optionsButton.getX(), MainMenuScreen.this.optionsButton.getY());
                }
            }
        }, new BaseScreen.GamepadEntryListener(){

            @Override
            public void onPress() {
                for (EventListener listener : MainMenuScreen.this.optionsButton.getListeners()) {
                    if (!(listener instanceof ClickListener)) continue;
                    ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.optionsButton.getX(), MainMenuScreen.this.optionsButton.getY());
                }
            }
        });

        // erase button
        BaseScreen.GamepadEntry eraseEntry = new BaseScreen.GamepadEntry(this.deleteButton, new BaseScreen.GamepadEntryListener(){
            @Override
            public void onPress() {
                for (EventListener listener : MainMenuScreen.this.deleteButton.getListeners()) {
                    if (!(listener instanceof ClickListener)) continue;
                    ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.deleteButton.getX(), MainMenuScreen.this.deleteButton.getY());
                }
            }
        }, new BaseScreen.GamepadEntryListener(){

            @Override
            public void onPress() {
                for (EventListener listener : MainMenuScreen.this.deleteButton.getListeners()) {
                    if (!(listener instanceof ClickListener)) continue;
                    ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.deleteButton.getX(), MainMenuScreen.this.deleteButton.getY());
                }
            }
        });

        // exit button
        BaseScreen.GamepadEntry exitEntry = new BaseScreen.GamepadEntry(this.exitButton, new BaseScreen.GamepadEntryListener(){
            @Override
            public void onPress() {
                for (EventListener listener : MainMenuScreen.this.exitButton.getListeners()) {
                    if (!(listener instanceof ClickListener)) continue;
                    ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.exitButton.getX(), MainMenuScreen.this.exitButton.getY());
                }
            }
        }, new BaseScreen.GamepadEntryListener(){

            @Override
            public void onPress() {
                for (EventListener listener : MainMenuScreen.this.exitButton.getListeners()) {
                    if (!(listener instanceof ClickListener)) continue;
                    ((ClickListener)listener).clicked(new InputEvent(), MainMenuScreen.this.exitButton.getX(), MainMenuScreen.this.exitButton.getY());
                }
            }
        });
        this.gamepadEntries.add(playEntry);
        this.gamepadEntries.add(eraseEntry);
        this.gamepadEntries.add(exitEntry);
        this.gamepadEntries.add(optionsEntry);

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
        } else if (levelNum == -1 && Game.gameData.tutorialLevel != null) {
            return Game.gameData.tutorialLevel.levelName;
        } else {
            return this.dungeonInfo != null && levelNum >= 0 && levelNum <= this.dungeonInfo.size ? levelName : "Floor " + levelNum + 1;
        }
    }

    public void tick(float delta) {
        super.tick(delta);
        // https://libgdx.badlogicgames.com/nightlies/docs/api/constant-values.html
        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            GameApplication.SetScreen(new OverlayWrapperScreen(new OptionsOverlay(false, true)));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (selectedSave != null) {
                GameApplication.SetScreen(new LoadingScreen(saveGames[selectedSave] == null ? "CREATING DUNGEON" : "LOADING", selectedSave));
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // fix for instant close
            if (ignoreEscapeKey) {
                GameApplication.SetScreen(new ConfirmExitScreen());
            } else {
                ignoreEscapeKey = true;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            GameApplication.SetScreen(new MainMenuScreen());
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) {
            if (selectedSave != null && saveGames[selectedSave] != null && !isSelected) {
                GameApplication.SetScreen(new ConfirmScreen(selectedSave));
            }
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

    public void selectSaveButtonEvent(int saveLoc, Table selected, int count) {
        this.gamepadSelectionIndex = saveLoc;
        this.isSelected = false;

        if (this.selectedSaveTable == selected) {
            selected = null; // unselect
            isSelected = true;
        }

        this.selectedSaveTable = selected;

        if (count > 1 && selectedSave != null) {
            GameApplication.SetScreen(new LoadingScreen(saveGames[selectedSave] == null ? StringManager.get("screens.MainMenuScreen.creatingDungeon") : StringManager.get("screens.MainMenuScreen.loadingSaveSlot"), selectedSave));
        }

        for (int i = 0; i < this.saveSlotUi.size; ++i) {
            ((Table)this.saveSlotUi.get(i)).setColor(Color.GRAY);
        }

        if (selected != null) {
            selected.setColor(Color.WHITE);
        }

        if (saveGames[saveLoc] != this.errorPlayer && !isSelected) {
            this.playButton.setVisible(true);
        } else {
            this.playButton.setVisible(false);
        }

        if (selected != null) {
            selectedSave = saveLoc;
        } else {
            selectedSave = null;
        }

        if (selectedSave != null && saveGames[selectedSave] != null && !isSelected) {
            this.deleteButton.setVisible(true);
        } else if (selectedSave != null && progress[selectedSave] != null && !isSelected) {
            this.deleteButton.setVisible(true);
        } else {
            this.deleteButton.setVisible(false);
        }

        Audio.playSound("/ui/ui_button_click.mp3", 0.3F);
    }

    public void playButtonEvent() {
        Audio.playSound("/ui/ui_button_click.mp3", 0.3F);
        this.fullTable.addAction(Actions.sequence(Actions.fadeOut(0.3F), Actions.delay(0.5F), Actions.addAction(new Action() {
            public boolean act(float v) {
                MainMenuScreen.this.fadingOut = true;
                return true;
            }
        }), Actions.delay(1.75F), Actions.addAction(new Action() {
            public boolean act(float v) {
                GameApplication.SetScreen(new LoadingScreen(MainMenuScreen.this.saveGames[MainMenuScreen.this.selectedSave] == null ? StringManager.get("screens.MainMenuScreen.creatingDungeon") : StringManager.get("screens.MainMenuScreen.loadingSaveSlot"), MainMenuScreen.this.selectedSave));
                return true;
            }
        })));
    }

    public void deleteButtonEvent() {
        this.saveGames[this.selectedSave] = null;
        this.progress[this.selectedSave] = null;
        this.deleteSavegame(this.selectedSave);
        this.selectedSave = null;
    }

    private void loadSavegames() {
        String baseSaveDir = "save/";
        FileHandle dir = Game.getFile(baseSaveDir);
        Gdx.app.log("DelverLifeCycle", "Getting savegames from " + dir.path());

        int i;
        FileHandle file;
        for(i = 0; i < this.saveGames.length; ++i) {
            file = Game.getFile(baseSaveDir + "/" + i + "/player.dat");
            if (file.exists()) {
                try {
                    this.saveGames[i] = (Player)Game.fromJson(Player.class, file);
                } catch (Exception var7) {
                    this.saveGames[i] = this.errorPlayer;
                }
            }
        }

        for(i = 0; i < this.saveGames.length; ++i) {
            file = Game.getFile(baseSaveDir + "/game_" + i + ".dat");
            if (file.exists()) {
                try {
                    this.progress[i] = (Progression)Game.fromJson(Progression.class, file);
                } catch (Exception var6) {
                    this.progress[i] = null;
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
            ;
        }

        try {
            baseSaveDir = "save/";
            file = Game.getFile(baseSaveDir + "/game_" + saveLoc + ".dat");
            Gdx.app.log("DelverLifeCycle", "Deleting progress " + file.path());
            file.delete();
        } catch (Exception var4) {
            ;
        }

        this.makeContent();
    }
}
