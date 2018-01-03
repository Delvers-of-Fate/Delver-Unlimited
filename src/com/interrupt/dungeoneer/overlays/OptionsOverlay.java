package com.interrupt.dungeoneer.overlays;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.GameApplication;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Options;
import com.interrupt.dungeoneer.screens.ModManagerScreen;
import com.interrupt.dungeoneer.ui.UiSkin;
import com.interrupt.managers.StringManager;

public class OptionsOverlay
        extends WindowOverlay
{
    private Slider musicVolume;
    private Slider sfxVolume;
    private Slider gfxQuality;
    private Label gfxQualityValueLabel;
    private float gfxQualityLastValue;
    private Slider uiSize;
    private Label uiSizeValueLabel;
    private float uiSizeLastValue;
    private Label musicVolumeValueLabel;
    private Label sfxVolumeValueLabel;
    private float sfxVolumeLastValue;
    private float musicVolumeLastValue;
    private CheckBox fullscreenMode;
    private String[] graphicsLabelValues = { StringManager.get("screens.OptionsScreen.graphicsLow"), StringManager.get("screens.OptionsScreen.graphicsMedium"), StringManager.get("screens.OptionsScreen.graphicsHigh"), StringManager.get("screens.OptionsScreen.graphicsUltra") };
    private boolean doForcedValuesUpdate = true;
    private Table mainTable;
    private CheckBox showUI;
    private CheckBox headBob;

    public OptionsOverlay()
    {
        this.animateBackground = false;
    }

    public OptionsOverlay(boolean dimScreen, boolean showBackground)
    {
        this.animateBackground = false;
        this.dimScreen = dimScreen;
        this.showBackground = showBackground;
    }

    public Table makeContent()
    {
        Options.loadOptions();
        Options options = Options.instance;

        this.skin = UiSkin.getSkin();

        TextButton backBtn = new TextButton(StringManager.get("screens.OptionsScreen.backButton"), (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        backBtn.setWidth(200.0F);
        backBtn.setHeight(50.0F);
        backBtn.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                OptionsOverlay.this.saveAndClose();
                Audio.playSound("/ui/ui_button_click.mp3", 0.1F);
            }
        });
        TextButton controlsBtn = new TextButton(StringManager.get("screens.OptionsScreen.inputButton"), (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        controlsBtn.setWidth(200.0F);
        controlsBtn.setHeight(50.0F);
        controlsBtn.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                OptionsOverlay.this.saveOptions();
                OverlayManager.instance.replaceCurrent(new OptionsInputOverlay(OptionsOverlay.this.dimScreen, OptionsOverlay.this.showBackground));
            }
        });
        TextButton graphicsBtn = new TextButton(StringManager.getOrDefaultTo("screens.OptionsScreen.graphicsButton", "Graphics"), (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        graphicsBtn.padRight(6.0F).padLeft(6.0F);
        graphicsBtn.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                OptionsOverlay.this.saveOptions();
                OverlayManager.instance.replaceCurrent(new OptionsGraphicsOverlay(OptionsOverlay.this.dimScreen, OptionsOverlay.this.showBackground));
            }
        });
        TextButton modBtn = new TextButton(StringManager.getOrDefaultTo("screens.OptionsScreen.modBtn", "Mods"), (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        modBtn.setWidth(200.0F);
        modBtn.setHeight(50.0F);
        modBtn.setColor(Color.RED);
        modBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                OptionsOverlay.this.saveOptions();
                GameApplication.SetScreen(new ModManagerScreen());
            }
        });


        this.mainTable = new Table();
        this.mainTable.setFillParent(true);
        this.mainTable.columnDefaults(0).align(8).padRight(4.0F);
        this.mainTable.columnDefaults(1).align(8).padLeft(4.0F).padRight(4.0F);
        this.mainTable.columnDefaults(2).align(16).padLeft(4.0F);

        Label header = new Label(StringManager.get("screens.OptionsScreen.headerLabel"), (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        header.setFontScale(1.1F);
        this.mainTable.add(header);
        this.mainTable.row();

        this.musicVolume = new Slider(0.0F, 1.0F, 0.01F, false, (Slider.SliderStyle)this.skin.get(Slider.SliderStyle.class));
        this.musicVolume.setValue(options.musicVolume);

        Label musicVolumeLabel = new Label(StringManager.get("screens.OptionsScreen.musicVolumeLabel"), (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        this.mainTable.add(musicVolumeLabel);
        this.mainTable.add(this.musicVolume);

        addGamepadButtonOrder(this.musicVolume, musicVolumeLabel);

        this.musicVolumeValueLabel = new Label("x.xxx", (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        this.musicVolumeValueLabel.setAlignment(16);
        this.mainTable.add(this.musicVolumeValueLabel);

        this.mainTable.row();

        this.sfxVolume = new Slider(0.0F, 2.0F, 0.01F, false, (Slider.SliderStyle)this.skin.get(Slider.SliderStyle.class));
        this.sfxVolume.setValue(options.sfxVolume);

        Label sfxVolumeLabel = new Label(StringManager.get("screens.OptionsScreen.soundVolumeLabel"), (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        this.mainTable.add(sfxVolumeLabel);
        this.mainTable.add(this.sfxVolume);

        addGamepadButtonOrder(this.sfxVolume, sfxVolumeLabel);

        this.sfxVolumeValueLabel = new Label("x.xxx", (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        this.sfxVolumeValueLabel.setAlignment(16);
        this.mainTable.add(this.sfxVolumeValueLabel);

        this.mainTable.row();

        this.gfxQuality = new Slider(1.0F, 4.0F, 1.0F, false, (Slider.SliderStyle)this.skin.get(Slider.SliderStyle.class));
        this.gfxQuality.setValue(options.graphicsDetailLevel);

        Label graphicsDetailLabel = new Label(StringManager.get("screens.OptionsScreen.graphicsDetailLabel"), (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        this.mainTable.add(graphicsDetailLabel);
        this.mainTable.add(this.gfxQuality);

        addGamepadButtonOrder(this.gfxQuality, graphicsDetailLabel);

        this.gfxQualityValueLabel = new Label("xx.xx", (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        this.gfxQualityValueLabel.setAlignment(16);
        this.mainTable.add(this.gfxQualityValueLabel).minSize(48.0F, 0.0F);
        this.mainTable.row();

        float uiMin = 0.5F;
        float uiMax = 1.5F;
        this.uiSize = new Slider(uiMin, uiMax, 0.01F, false, (Slider.SliderStyle)this.skin.get(Slider.SliderStyle.class));
        options.uiSize = Math.max(Math.min(options.uiSize, uiMax), uiMin);
        this.uiSize.setValue(options.uiSize);

        Label uiSizeLabel = new Label(StringManager.get("screens.OptionsScreen.uiSizeLabel"), (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        this.mainTable.add(uiSizeLabel);
        this.mainTable.add(this.uiSize);

        this.uiSizeValueLabel = new Label("x.xxx", (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        this.uiSizeValueLabel.setAlignment(16);
        this.mainTable.add(this.uiSizeValueLabel);
        this.mainTable.row();

        addGamepadButtonOrder(this.uiSize, uiSizeLabel);

        Label showHudLabel = new Label(StringManager.get("screens.OptionsScreen.showHudLabel"), (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        this.mainTable.add(showHudLabel);

        this.showUI = new CheckBox(null, (CheckBox.CheckBoxStyle)this.skin.get(CheckBox.CheckBoxStyle.class));
        this.showUI.setChecked(!Options.instance.hideUI);

        addGamepadButtonOrder(this.showUI, showHudLabel);

        this.mainTable.add(this.showUI);
        this.mainTable.row();

        Label headBobLabel = new Label(StringManager.get("screens.OptionsScreen.headBobLabel"), (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        this.mainTable.add(headBobLabel);

        this.headBob = new CheckBox(null, (CheckBox.CheckBoxStyle)this.skin.get(CheckBox.CheckBoxStyle.class));
        this.headBob.setChecked(Options.instance.headBobEnabled);

        addGamepadButtonOrder(this.headBob, headBobLabel);

        this.mainTable.add(this.headBob);
        this.mainTable.row();
        if ((Gdx.app.getType() != Application.ApplicationType.Android) && (Gdx.app.getType() != Application.ApplicationType.iOS))
        {
            this.fullscreenMode = new CheckBox("", (CheckBox.CheckBoxStyle)this.skin.get(CheckBox.CheckBoxStyle.class));
            this.fullscreenMode.setChecked(Options.instance.fullScreen);

            this.fullscreenMode.addListener(new ClickListener()
            {
                public void clicked(InputEvent event, float x, float y)
                {
                    if (OptionsOverlay.this.fullscreenMode.isChecked())
                    {
                        Graphics.DisplayMode desktopMode = Gdx.app.getGraphics().getDisplayMode(Gdx.graphics.getMonitor());
                        Gdx.app.getGraphics().setFullscreenMode(desktopMode);
                    }
                    else
                    {
                        Graphics.DisplayMode desktopMode = Gdx.app.getGraphics().getDisplayMode(Gdx.graphics.getMonitor());
                        Gdx.app.getGraphics().setWindowedMode(desktopMode.width, desktopMode.height);
                    }
                }
            });
            Label fullScreenLabel = new Label(StringManager.get("screens.OptionsScreen.fullscreenLabel"), (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
            this.mainTable.add(fullScreenLabel);
            this.mainTable.add(this.fullscreenMode);

            addGamepadButtonOrder(this.fullscreenMode, fullScreenLabel);

            this.mainTable.row();
        }
        Table buttonTable = new Table();
        buttonTable.add(backBtn).padRight(4.0F);
        buttonTable.add(controlsBtn).padRight(4.0F);
        buttonTable.add(graphicsBtn).padRight(4.0F);
        buttonTable.add(modBtn).padRight(4.0F);
        buttonTable.pack();

        this.mainTable.add(buttonTable);
        this.mainTable.getCell(header).align(1).colspan(3).padBottom(8.0F);
        this.mainTable.getCell(buttonTable).colspan(2).padTop(8.0F);
        this.mainTable.pack();

        Table content = new Table();
        content.add(this.mainTable);
        content.pack();

        this.buttonOrder.add(backBtn);
        this.buttonOrder.add(controlsBtn);
        this.buttonOrder.add(graphicsBtn);
        this.buttonOrder.add(modBtn);

        return content;
    }

    public void tick(float delta)
    {
        if (((Gdx.input.isKeyJustPressed(131)) || (Gdx.input.isKeyJustPressed(4))) &&
                (this.mainTable.isVisible())) {
            saveAndClose();
        }
        Options.instance.hideUI = (!this.showUI.isChecked());

        updateValues();
        super.tick(delta);
    }

    public void updateValues()
    {
        Options.instance.uiSize = this.uiSize.getValue();
        Options.instance.musicVolume = this.musicVolume.getValue();
        Options.instance.sfxVolume = this.sfxVolume.getValue();
        Options.instance.graphicsDetailLevel = ((int)this.gfxQuality.getValue());
        if ((this.doForcedValuesUpdate) || (this.uiSizeLastValue != this.uiSize.getValue()))
        {
            this.uiSizeLastValue = this.uiSize.getValue();
            this.uiSizeValueLabel.setText(String.format("%5.0f", new Object[] { Float.valueOf(100.0F * this.uiSizeLastValue) }) + "%");
            if ((Game.instance != null) && (Game.instance.player != null)) {
                Game.RefreshUI();
            }
        }
        if ((this.doForcedValuesUpdate) || (this.sfxVolumeLastValue != this.sfxVolume.getValue()))
        {
            this.sfxVolumeLastValue = this.sfxVolume.getValue();
            Audio.updateLoopingSoundVolumes();
            this.sfxVolumeValueLabel.setText(String.format("%5.0f", new Object[] { Float.valueOf(100.0F * this.sfxVolumeLastValue) }) + "%");
        }
        if ((this.doForcedValuesUpdate) || (this.musicVolumeLastValue != this.musicVolume.getValue()))
        {
            if (Audio.music != null) {
                Audio.setMusicVolume(1.0F);
            }
            this.musicVolumeLastValue = this.musicVolume.getValue();
            this.musicVolumeValueLabel.setText(String.format("%5.0f", new Object[] { Float.valueOf(100.0F * this.musicVolumeLastValue) }) + "%");
        }
        if ((this.doForcedValuesUpdate) || (this.gfxQualityLastValue != this.gfxQuality.getValue()))
        {
            this.gfxQualityLastValue = this.gfxQuality.getValue();
            try
            {
                this.gfxQualityValueLabel.setText(this.graphicsLabelValues[((int)this.gfxQualityLastValue - 1)]);
            }
            catch (Exception ex)
            {
                this.gfxQualityValueLabel.setText("");
            }
        }
        this.doForcedValuesUpdate = false;
    }

    public void saveAndClose()
    {
        saveOptions();
        this.visible = false;
        OverlayManager.instance.clear();
    }

    public void saveOptions()
    {
        Options.instance.musicVolume = this.musicVolume.getValue();
        Options.instance.uiSize = this.uiSize.getValue();
        Options.instance.sfxVolume = this.sfxVolume.getValue();
        Options.instance.graphicsDetailLevel = ((int)this.gfxQuality.getValue());
        Options.instance.hideUI = (!this.showUI.isChecked());
        Options.instance.headBobEnabled = this.headBob.isChecked();
        if (this.fullscreenMode != null) {
            Options.instance.fullScreen = this.fullscreenMode.isChecked();
        }
        Options.saveOptions();
    }
}
