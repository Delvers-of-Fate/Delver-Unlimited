package com.interrupt.dungeoneer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.interrupt.dungeoneer.Art;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.GameApplication;
import com.interrupt.dungeoneer.GameManager;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Options;
import com.interrupt.dungeoneer.ui.UiSkin;
import com.interrupt.managers.StringManager;

public class OptionsScreen extends BaseScreen {
    private float fontSize = 1.0F;
    private Slider musicVolume;
    private Slider sfxVolume;
    private Slider particleDensity;
    private Label particleDensityValueLabel;
    private float particleDensityLastValue;
    private Slider fovSlider;
    private Label fovValueLabel;
    private float lastFov = 0.0F;
    private Slider gfxQuality;
    private Label gfxQualityValueLabel;
    private float gfxQualityLastValue;
    private Slider uiSize;
    private Label uiSizeValueLabel;
    private float uiSizeLastValue;
    private String[] graphicsLabelValues = new String[]{StringManager.get("screens.OptionsScreen.graphicsLow"), StringManager.get("screens.OptionsScreen.graphicsMedium"), StringManager.get("screens.OptionsScreen.graphicsHigh"), StringManager.get("screens.OptionsScreen.graphicsUltra")};
    private boolean doForcedValuesUpdate = true;
    private Table mainTable;
    private CheckBox fullscreenMode;
    private CheckBox showUI;
    private CheckBox headBob;
    private CheckBox shadows;
    Skin skin;
    BitmapFont font;
    boolean ignoreClosePress = false;

    public OptionsScreen() {
        this.screenName = "OptionsScreen";
        Game.loadOptions();
        Options options = Options.instance;
        if (Gdx.app.getType() == ApplicationType.Android) {
            Gdx.input.setCatchBackKey(true);
        }

        this.skin = UiSkin.getSkin();
        this.font = UiSkin.getFont();
        this.viewport.setWorldWidth((float)Gdx.graphics.getWidth() / this.uiScale);
        this.viewport.setWorldHeight((float)Gdx.graphics.getHeight() / this.uiScale);
        this.ui = new Stage(this.viewport);
        TextButton backBtn = new TextButton(StringManager.get("screens.OptionsScreen.backButton"), (TextButtonStyle)this.skin.get(TextButtonStyle.class));
        backBtn.setWidth(200.0F);
        backBtn.setHeight(50.0F);
        backBtn.setColor(Color.GREEN);
        backBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                OptionsScreen.this.saveAndClose();
            }
        });
        TextButton controlsBtn = new TextButton(StringManager.get("screens.OptionsScreen.inputButton"), (TextButtonStyle)this.skin.get(TextButtonStyle.class));
        controlsBtn.setWidth(200.0F);
        controlsBtn.setHeight(50.0F);
        controlsBtn.setColor(Color.BLUE);
        controlsBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                OptionsScreen.this.saveOptions();
                GameApplication.SetScreen(new OptionsInputScreen());
            }
        });
        TextButton modBtn = new TextButton(StringManager.get("screens.OptionsScreen.modBtn"), (TextButtonStyle)this.skin.get(TextButtonStyle.class));
        modBtn.setWidth(200.0F);
        modBtn.setHeight(50.0F);
        modBtn.setColor(Color.RED);
        modBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                OptionsScreen.this.saveOptions();
                GameApplication.SetScreen(new ModManagerScreen());
            }
        });
        this.mainTable = new Table();
        this.mainTable.setFillParent(true);
        this.mainTable.columnDefaults(0).align(8).padRight(4.0F);
        this.mainTable.columnDefaults(1).align(8).padLeft(4.0F).padRight(4.0F);
        this.mainTable.columnDefaults(2).align(16).padLeft(4.0F);
        Label header = new Label(StringManager.get("screens.OptionsScreen.headerLabel"), (LabelStyle)this.skin.get(LabelStyle.class));
        header.setFontScale(1.1F);
        this.mainTable.add(header);
        this.mainTable.row();
        this.musicVolume = new Slider(0.0F, 1.0F, 0.001F, false, (SliderStyle)this.skin.get(SliderStyle.class));
        this.musicVolume.setValue(options.musicVolume);
        this.mainTable.add(new Label(StringManager.get("screens.OptionsScreen.musicVolumeLabel"), (LabelStyle)this.skin.get(LabelStyle.class)));
        this.mainTable.add(this.musicVolume);
        this.mainTable.row();
        this.sfxVolume = new Slider(0.0F, 2.0F, 0.01F, false, (SliderStyle)this.skin.get(SliderStyle.class));
        this.sfxVolume.setValue(options.sfxVolume);
        this.mainTable.add(new Label(StringManager.get("screens.OptionsScreen.soundVolumeLabel"), (LabelStyle)this.skin.get(LabelStyle.class)));
        this.mainTable.add(this.sfxVolume);
        this.mainTable.row();
        float uiMin = 0.35F;
        float uiMax = 1.5F;
        this.uiSize = new Slider(uiMin, uiMax, 0.01F, false, (SliderStyle)this.skin.get(SliderStyle.class));
        options.uiSize = Math.max(Math.min(options.uiSize, uiMax), uiMin);
        this.uiSize.setValue(options.uiSize);
        this.mainTable.add(new Label(StringManager.get("screens.OptionsScreen.uiSizeLabel"), (LabelStyle)this.skin.get(LabelStyle.class)));
        this.mainTable.add(this.uiSize);
        this.uiSizeValueLabel = new Label("x.xxx", (LabelStyle)this.skin.get(LabelStyle.class));
        this.uiSizeValueLabel.setAlignment(16);
        this.mainTable.add(this.uiSizeValueLabel);
        this.mainTable.row();
        this.mainTable.add(new Label(StringManager.get("screens.OptionsScreen.showHudLabel"), (LabelStyle)this.skin.get(LabelStyle.class)));
        this.showUI = new CheckBox((String)null, (CheckBoxStyle)this.skin.get(CheckBoxStyle.class));
        this.showUI.setChecked(!Options.instance.hideUI);
        this.mainTable.add(this.showUI);
        this.mainTable.row();
        this.mainTable.add(new Label(StringManager.get("screens.OptionsScreen.headBobLabel"), (LabelStyle)this.skin.get(LabelStyle.class)));
        this.headBob = new CheckBox((String)null, (CheckBoxStyle)this.skin.get(CheckBoxStyle.class));
        this.headBob.setChecked(Options.instance.headBobEnabled);
        this.mainTable.add(this.headBob);
        this.mainTable.row();
        this.gfxQuality = new Slider(1.0F, 4.0F, 1.0F, false, (SliderStyle)this.skin.get(SliderStyle.class));
        this.gfxQuality.setValue((float)options.graphicsDetailLevel);
        this.mainTable.add(new Label(StringManager.get("screens.OptionsScreen.graphicsDetailLabel"), (LabelStyle)this.skin.get(LabelStyle.class)));
        this.mainTable.add(this.gfxQuality);
        this.gfxQualityValueLabel = new Label("xx.xx", (LabelStyle)this.skin.get(LabelStyle.class));
        this.gfxQualityValueLabel.setAlignment(16);
        this.mainTable.add(this.gfxQualityValueLabel).minSize(48.0F, 0.0F);
        this.mainTable.row();
        this.particleDensity = new Slider(0.0F, 1.0F, 0.1F, false, (SliderStyle)this.skin.get(SliderStyle.class));
        this.particleDensity.setValue(options.gfxQuality);
        this.mainTable.add(new Label(StringManager.get("screens.OptionsScreen.particleDensityLabel"), (LabelStyle)this.skin.get(LabelStyle.class)));
        this.mainTable.add(this.particleDensity);
        this.particleDensityValueLabel = new Label("x.x", (LabelStyle)this.skin.get(LabelStyle.class));
        this.particleDensityValueLabel.setAlignment(16);
        this.mainTable.add(this.particleDensityValueLabel);
        this.mainTable.row();
        this.mainTable.add(new Label(StringManager.get("screens.OptionsScreen.shadowsLabel"), (LabelStyle)this.skin.get(LabelStyle.class)));
        this.shadows = new CheckBox((String)null, (CheckBoxStyle)this.skin.get(CheckBoxStyle.class));
        this.shadows.setChecked(Options.instance.shadowsEnabled);
        this.mainTable.add(this.shadows);
        this.mainTable.row();
        this.fovSlider = new Slider(50.0F, 110.0F, 1.0F, false, (SliderStyle)this.skin.get(SliderStyle.class));
        this.fovSlider.setValue(options.fieldOfView);
        this.mainTable.add(new Label(StringManager.get("screens.OptionsScreen.fovLabel"), (LabelStyle)this.skin.get(LabelStyle.class)));
        this.mainTable.add(this.fovSlider);
        this.fovValueLabel = new Label("xxx", (LabelStyle)this.skin.get(LabelStyle.class));
        this.fovValueLabel.setAlignment(16);
        this.mainTable.add(this.fovValueLabel);
        this.mainTable.row();
        if (Gdx.app.getType() != ApplicationType.Android && Gdx.app.getType() != ApplicationType.iOS) {
            this.fullscreenMode = new CheckBox("", (CheckBoxStyle)this.skin.get(CheckBoxStyle.class));
            this.fullscreenMode.setChecked(Options.instance.fullScreen);
            this.fullscreenMode.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    DisplayMode desktopMode;
                    if (OptionsScreen.this.fullscreenMode.isChecked()) {
                        desktopMode = Gdx.app.getGraphics().getDisplayMode(Gdx.graphics.getMonitor());
                        Gdx.app.getGraphics().setFullscreenMode(desktopMode);
                    } else {
                        desktopMode = Gdx.app.getGraphics().getDisplayMode(Gdx.graphics.getMonitor());
                        Gdx.app.getGraphics().setWindowedMode(desktopMode.width, desktopMode.height);
                    }

                }
            });
            this.mainTable.add(new Label(StringManager.get("screens.OptionsScreen.fullscreenLabel"), (LabelStyle)this.skin.get(LabelStyle.class)));
            this.mainTable.add(this.fullscreenMode);
            this.mainTable.row();
        }

        Table buttonTable = new Table();
        buttonTable.add(backBtn);
        buttonTable.add(controlsBtn);
        buttonTable.add(modBtn);
        buttonTable.getCell(backBtn).padRight(4.0F);
        buttonTable.getCell(controlsBtn).padRight(4.0F);
        buttonTable.getCell(modBtn).padRight(4.0F);
        this.mainTable.add(buttonTable);
        this.mainTable.getCell(header).align(1).colspan(3).padBottom(8.0F);
        this.mainTable.getCell(buttonTable).colspan(2).padTop(8.0F);
        this.mainTable.setHeight(184.0F);
        this.mainTable.setWidth(270.0F);
        this.ui.addActor(this.mainTable);
        Gdx.input.setInputProcessor(this.ui);
        if (Gdx.input.isKeyPressed(131) || Gdx.input.isKeyPressed(4)) {
            this.ignoreClosePress = true;
        }

    }

    public void updateValues() {
        if (this.doForcedValuesUpdate || this.uiSizeLastValue != this.uiSize.getValue()) {
            this.uiSizeLastValue = this.uiSize.getValue();
            this.uiSizeValueLabel.setText(String.format("%5.0f", 100.0F * this.uiSizeLastValue) + "%");
            if (this.uiSize.getValue() == this.uiSize.getMinValue()) {
                this.uiSizeValueLabel.setText(StringManager.get("screens.OptionsScreen.uiSizeOffLabel"));
            }
        }

        if (Audio.music != null) {
            Audio.setMusicVolume(this.musicVolume.getValue());
        }

        if (this.doForcedValuesUpdate || this.gfxQualityLastValue != this.gfxQuality.getValue()) {
            this.gfxQualityLastValue = this.gfxQuality.getValue();

            try {
                this.gfxQualityValueLabel.setText(this.graphicsLabelValues[(int)this.gfxQualityLastValue - 1]);
            } catch (Exception var2) {
                this.gfxQualityValueLabel.setText("");
            }
        }

        if (this.doForcedValuesUpdate || this.particleDensityLastValue != this.particleDensity.getValue()) {
            this.particleDensityLastValue = this.particleDensity.getValue();
            this.particleDensityValueLabel.setText(String.format("%5.0f", 100.0F * this.particleDensityLastValue));
        }

        if (this.doForcedValuesUpdate || this.lastFov != this.fovSlider.getValue()) {
            this.lastFov = this.fovSlider.getValue();
            GameManager.renderer.camera.fieldOfView = this.lastFov;
            this.fovValueLabel.setText(String.format("%1.0f", this.lastFov));
        }

        Options.instance.graphicsDetailLevel = (int)this.gfxQuality.getValue();
        Options.instance.shadowsEnabled = this.shadows.isChecked();
        Options.instance.gfxQuality = this.particleDensity.getValue();
        this.doForcedValuesUpdate = false;
    }

    public void draw(float delta) {
        super.draw(delta);
        this.ui.draw();
    }

    public void show() {
        super.show();
        this.backgroundTexture = Art.loadTexture("splash/Delver-Menu-BG.png");
        this.backgroundTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }

    public void tick(float delta) {
        this.fontSize = (float)Math.min(curWidth, curHeight) / 10.0F;
        if (Gdx.app.getType() == ApplicationType.Android) {
            this.fontSize *= 1.15F;
        }

        this.fontSize = (float)((int)this.fontSize);
        if (!Gdx.input.isKeyPressed(131) && !Gdx.input.isKeyPressed(4)) {
            this.ignoreClosePress = false;
        } else if (!this.ignoreClosePress) {
            this.saveAndClose();
        }

        this.ui.act(delta);
        this.updateValues();
        super.tick(delta);
    }

    public void saveAndClose() {
        this.saveOptions();
        GameApplication.SetScreen(new MainMenuScreen());
    }

    public void saveOptions() {
        Options.instance.musicVolume = this.musicVolume.getValue();
        Options.instance.uiSize = this.uiSize.getValue();
        Options.instance.sfxVolume = this.sfxVolume.getValue();
        Options.instance.gfxQuality = this.particleDensity.getValue();
        Options.instance.fieldOfView = this.fovSlider.getValue();
        Options.instance.graphicsDetailLevel = (int)this.gfxQuality.getValue();
        Options.instance.hideUI = !this.showUI.isChecked();
        Options.instance.headBobEnabled = this.headBob.isChecked();
        Options.instance.shadowsEnabled = this.shadows.isChecked();
        if (this.fullscreenMode != null) {
            Options.instance.fullScreen = this.fullscreenMode.isChecked();
        }

        Game.saveOptions();
    }
}
