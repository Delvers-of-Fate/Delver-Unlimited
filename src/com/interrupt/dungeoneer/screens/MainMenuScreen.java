package com.interrupt.dungeoneer.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
import com.interrupt.managers.StringManager;
import net.cotd.delverunlimited.Config;

import java.text.MessageFormat;

public class MainMenuScreen
        extends BaseScreen
{
    private Texture menuTexture;
    private TextureRegion[] menuRegions;
    private Table fullTable = null;
    private Table buttonTable = null;
    private Button[] startButtons = new Button[3];
    private TextButton playButton;
    private TextButton deleteButton;
    private TextButton optionsButton;
    private TextButton exitButton;
    public static Progression[] progress = new Progression[3];
    public static Player[] saveGames = new Player[3];
    public static Integer selectedSave;
    private Table selectedSaveTable = null;
    private float fontSize = 1.0F;
    Array<Table> saveSlotUi = new Array();
    Player errorPlayer = new Player();
    Array<Level> dungeonInfo = null;
    Color fadeColor = new Color(Color.BLACK);
    boolean fadingOut = false;
    float fadeFactor = 1.0F;
    private int[] saveFiles = {0, 1, 2};
    private boolean escapePressed = false;
    private boolean isSelected = false;
    public MainMenuScreen()
    {
        this.dungeonInfo = Game.buildLevelLayout();
        if (SplashScreen.splashScreenInfo != null) {
            this.splashLevel = SplashScreen.splashScreenInfo.backgroundLevel;
        }
        this.screenName = "MainMenuScreen";

        this.menuTexture = Art.loadTexture("menu.png");
        this.menuRegions = new TextureRegion[this.menuTexture.getWidth() / 16 * (this.menuTexture.getHeight() / 16)];
        int count = 0;
        for (int y = 0; y < this.menuTexture.getHeight() / 16; y++) {
            for (int x = 0; x < this.menuTexture.getWidth() / 16; x++) {
                this.menuRegions[(count++)] = new TextureRegion(this.menuTexture, x * 16, y * 16, 16, 16);
            }
        }
        this.viewport.setWorldWidth(Gdx.graphics.getWidth() / this.uiScale);
        this.viewport.setWorldHeight(Gdx.graphics.getHeight() / this.uiScale);
        this.ui = new Stage(this.viewport);

        this.fullTable = new Table(this.skin);
        this.fullTable.setFillParent(true);
        this.fullTable.align(Align.top);

        this.buttonTable = new Table();

        this.ui.addActor(this.fullTable);

        Gdx.input.setInputProcessor(this.ui);

        isSelected = false;
        selectedSaveTable = null;
        selectedSave = null;
    }

    private void makeContent()
    {
        String paddedButtonText = " {0} ";

        this.fullTable.clearChildren();

        this.playButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.playButton")), this.skin);
        this.playButton.setColor(Colors.PLAY_BUTTON);
        this.playButton.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                MainMenuScreen.this.playButtonEvent();
            }
        });
        this.deleteButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.eraseButton")), this.skin);
        this.deleteButton.setColor(Colors.PARALYZE);
        this.deleteButton.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                GameApplication.SetScreen(new ConfirmScreen(selectedSave));
            }
        });
        this.optionsButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.optionsButton")), this.skin);
        this.optionsButton.setColor(Color.SKY);
        this.optionsButton.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                GameApplication.SetScreen(new OptionsScreen());
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
            if ((saveGames[i] != null) && (saveGames[i] == this.errorPlayer)) {
                saveName = StringManager.get("screens.MainMenuScreen.errorSaveSlot");
            } else if (saveGames[i] != null) {
                saveName = getSaveName(progress[i], saveGames[i].levelNum);
            } else if (progress[i] != null) {
                saveName = getSaveName(progress[i], null);
            }
            if ((save != null) && (save != this.errorPlayer)) {
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
            if (progress[i] != null) {
                t2.add(locationLabel).align(8).size(220.0F, rowHeight).padTop(2.0F);
                t2.row();

                Image goldIcon = new Image(new TextureRegionDrawable(TextureAtlas.cachedAtlases.get("item").sprite_regions[89]));
                goldIcon.setAlign(8);

                Image skullIcon = new Image(new TextureRegionDrawable(TextureAtlas.cachedAtlases.get("item").sprite_regions[56]));
                skullIcon.setAlign(8);

                Image orbIcon = new Image(new TextureRegionDrawable(TextureAtlas.cachedAtlases.get("item").sprite_regions[59]));
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
                progressTable.add(orbIcon).width(20.0F).height(20.0F).align(8);
                progressTable.add(winsLabel).padLeft(2.0F).width(45.0F);
                progressTable.pack();

                t2.add(progressTable).align(8);
            } else {
                t2.add(locationLabel).align(8).size(220.0F, rowHeight * 2.0F).padTop(2.0F).padBottom(4.0F);
            }
            t2.pack();

            t.add(t2);
            t.pack();

            if(!Config.skipIntro) {
                t.addAction(Actions.sequence(Actions.fadeOut(1.0E-4F), Actions.delay((i + 1.0F) * 0.1F), Actions.fadeIn(0.2F)));
            }

            t.setTouchable(Touchable.enabled);
            t.addListener(new ClickListener()
            {
                public void clicked(InputEvent event, float x, float y)
                {
                        MainMenuScreen.this.selectSaveButtonEvent(i, t, getTapCount());
                }
            });
            t.setColor(Color.GRAY);
            this.saveSlotUi.add(t);

            this.fullTable.add(t).padTop(4.0F);
            this.fullTable.row();
        }
        this.buttonTable.add().width(2.0F);
        this.buttonTable.add(this.playButton).height(20.0F);
        this.buttonTable.add(this.deleteButton).height(20.0F);
        this.buttonTable.add().width(36.0F);
        this.buttonTable.add(this.exitButton).height(20.0F);
        this.buttonTable.add(this.optionsButton).height(20.0F);
        this.buttonTable.pack();

        this.fullTable.row();
        this.fullTable.add(this.buttonTable).height(30.0F).fill(true, false);
        this.fullTable.row();

        this.fullTable.pack();

        if(!Config.skipIntro) {
            this.fullTable.addAction(Actions.sequence(Actions.fadeOut(1.0E-4F), Actions.fadeIn(0.2F)));
        }

        fullTable.add("Delver version 14");
        fullTable.row();
        fullTable.add("Delver-Unlimited version " + Config.OfflineVer);

        this.playButton.setVisible(false);
        this.deleteButton.setVisible(false);
    }

    public void show()
    {
        super.show();
        if (Game.instance != null) {
            Game.instance.clearMemory();
        }
        loadSavegames();

        makeContent();

        this.backgroundTexture = Art.loadTexture("splash/Delver-Menu-BG.png");
        this.backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        if (SplashScreen.splashScreenInfo.music != null) {
            Audio.playMusic(SplashScreen.splashScreenInfo.music, true);
        }
        this.buttonOrder.add(this.startButtons[0]);
        this.buttonOrder.add(this.startButtons[1]);
        this.buttonOrder.add(this.startButtons[2]);
    }

    public void draw(float delta)
    {
        super.draw(delta);

        this.renderer = GameManager.renderer;
        this.gl = this.renderer.getGL();

        this.ui.draw();

        this.renderer.uiBatch.setProjectionMatrix(this.renderer.camera2D.combined);
        this.renderer.uiBatch.begin();

        this.renderer.uiBatch.end();
        if (this.fadeFactor < 1.0F) {
            this.renderer.drawFlashOverlay(this.fadeColor.set(0.0F, 0.0F, 0.0F, 1.0F - this.fadeFactor));
        }

    }

    private String getSaveName(Progression p, Integer levelNum)
    {
        if ((p != null) && (p.won)) {
            return StringManager.get("screens.MainMenuScreen.finishedSaveSlot");
        }
        if (levelNum == null) {
            return StringManager.get("screens.MainMenuScreen.deadSaveSlot");
        }
        if ((this.dungeonInfo == null) || (levelNum < 0) || (levelNum > this.dungeonInfo.size)) {
            return "Floor " + levelNum + 1;
        }
        return this.dungeonInfo.get(levelNum).levelName;
    }

    public void tick(float delta)
    {
        this.fontSize = (Math.min(curWidth, curHeight) / 12.0F);
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            this.fontSize *= 1.15F;
        }

        // https://libgdx.badlogicgames.com/nightlies/docs/api/constant-values.html
        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            GameApplication.SetScreen(new OptionsScreen());
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if(selectedSave != null) {
                GameApplication.SetScreen(new LoadingScreen(saveGames[selectedSave] == null ? "CREATING DUNGEON" : "LOADING", selectedSave));
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // fix for instant close
            if(escapePressed) {
                GameApplication.SetScreen(new ConfirmExitScreen());
            } else {
                escapePressed = true;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            GameApplication.SetScreen(new MainMenuScreen());
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL)) {
            if(selectedSave != null && saveGames[selectedSave] != null && !isSelected) {
                GameApplication.SetScreen(new ConfirmScreen(selectedSave));
            }
        }

        this.ui.act(delta);
        if (this.fadingOut)
        {
            this.fadeFactor -= delta * 0.5F;
            if (this.fadeFactor < 0.0F) {
                this.fadeFactor = 0.0F;
            }
            Audio.setMusicVolume(1.0F * this.fadeFactor);
        }
        super.tick(delta);
    }

    private void selectSaveButtonEvent(int saveLoc, Table selected, int count)
    {

        isSelected = false;

        if(this.selectedSaveTable == selected) {
            // unselect
            selected = null;
            isSelected = true;
        }

        this.selectedSaveTable = selected;

        if(count > 1 && selectedSave != null) {
            GameApplication.SetScreen(new LoadingScreen(saveGames[selectedSave] == null ? StringManager.get("screens.MainMenuScreen.creatingDungeon") : StringManager.get("screens.MainMenuScreen.loadingSaveSlot"), selectedSave));
        }

        for (int i = 0; i < this.saveSlotUi.size; i++) {
            this.saveSlotUi.get(i).setColor(Color.GRAY);
        }
        if (selected != null) {
            selected.setColor(Color.WHITE);
        }
        if (saveGames[saveLoc] != this.errorPlayer && !isSelected) {
            this.playButton.setVisible(true);
        } else {
            this.playButton.setVisible(false);
        }

        if(selected != null) {
            selectedSave = saveLoc;
        } else {
            selectedSave = null;
        }

        if(selectedSave != null && saveGames[selectedSave] != null && !isSelected) {
            this.deleteButton.setVisible(true);
        } else if (selectedSave != null && progress[selectedSave] != null && !isSelected) {
            this.deleteButton.setVisible(true);
        } else {
            this.deleteButton.setVisible(false);
        }

        if (this.gamepadSelectionIndex != null) {
            this.gamepadSelectionIndex = 3;
        }
        if (!this.buttonOrder.contains(this.playButton, true)) {
            this.buttonOrder.add(this.playButton);
        }
        if (!this.buttonOrder.contains(this.deleteButton, true)) {
            this.buttonOrder.add(this.deleteButton);
        }

    }

    private void playButtonEvent()
    {
        this.fullTable.addAction(Actions.sequence(Actions.fadeOut(0.3F), Actions.delay(0.5F), Actions.addAction(new Action()
        {
            public boolean act(float v)
            {
                if(!Config.skipIntro) {
                    MainMenuScreen.this.fadingOut = true;
                } else {
                    GameApplication.SetScreen(new LoadingScreen(saveGames[selectedSave] == null ? StringManager.get("screens.MainMenuScreen.creatingDungeon") : StringManager.get("screens.MainMenuScreen.loadingSaveSlot"), selectedSave));
                }

                return true;
            }
        }), Actions.delay(1.75F), Actions.addAction(new Action()
        {
            public boolean act(float v)
            {
                GameApplication.SetScreen(new LoadingScreen(saveGames[selectedSave] == null ? StringManager.get("screens.MainMenuScreen.creatingDungeon") : StringManager.get("screens.MainMenuScreen.loadingSaveSlot"), selectedSave));
                return true;
            }
        })));
    }

    private void loadSavegames()
    {
        String baseSaveDir = "save/";
        FileHandle dir = Game.getFile(baseSaveDir);

        Gdx.app.log("DelverLifeCycle", "Getting savegames from " + dir.path());
        for (int i = 0; i < saveGames.length; i++)
        {
            FileHandle file = Game.getFile(baseSaveDir + "/" + i + "/player.dat");
            if (file.exists()) {
                try
                {
                    saveGames[i] = Game.fromJson(Player.class, file);
                }
                catch (Exception ex)
                {
                    saveGames[i] = this.errorPlayer;
                }
            }
        }
        for (int i = 0; i < saveGames.length; i++)
        {
            FileHandle file = Game.getFile(baseSaveDir + "/game_" + i + ".dat");
            if (file.exists()) {
                try
                {
                    progress[i] = Game.fromJson(Progression.class, file);
                }
                catch (Exception ex)
                {
                    progress[i] = null;
                }
            }
        }
    }

}
