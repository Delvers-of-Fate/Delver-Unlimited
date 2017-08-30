package com.interrupt.dungeoneer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.GameApplication;
import com.interrupt.dungeoneer.game.Colors;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.managers.StringManager;
import net.cotd.delverunlimited.helper.Mod;
import net.cotd.delverunlimited.helper.ModSettings;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

public class ModManagerScreen extends BaseScreen {

    private Table fullTable = null;
    private Table buttonTable = null;

    private Mod selectedMod = null;
    private static String baseString = "screens.ModManagerScreen.";

    private TextButton enableButton;
    private TextButton disableButton;
    private TextButton wwwButton;
    private TextButton backButton;

    private Label nameLabel;
    private Label authorLabel;
    private Label descLabel;
    private Label versionLabel;
    private Label stateLabel;

    public ModManagerScreen() {
        this.screenName = "ConfirmExitScreen";

        this.viewport.setWorldWidth(Gdx.graphics.getWidth() / this.uiScale);
        this.viewport.setWorldHeight(Gdx.graphics.getHeight() / this.uiScale);
        this.ui = new Stage(this.viewport);

        this.fullTable = new Table(this.skin);
        this.fullTable.setFillParent(true);
        this.fullTable.align(2);
        this.uiScale = 1f;
        this.buttonTable = new Table(this.skin);

        this.ui.addActor(this.fullTable);
        Gdx.input.setInputProcessor(this.ui);
    }

    public void show() {
        super.show();

        if (Game.instance != null) {
            Game.instance.clearMemory();
        }

        makeContent();

    }

    private void makeContent() {
        String paddedButtonText = " {0} ";

        enableButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get(baseString + "enableButton")), this.skin);
        enableButton.setWidth(200.0F);
        enableButton.setHeight(50.0F);
        enableButton.setColor(Colors.PLAY_BUTTON);
        enableButton.setVisible(false);
        enableButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                enableMod();
            }
        });

        disableButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get(baseString + "disableButton")), this.skin);
        disableButton.setWidth(200.0F);
        disableButton.setHeight(50.0F);
        disableButton.setColor(Colors.ERASE_BUTTON);
        disableButton.setVisible(false);
        disableButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                disableMod();
            }
        });

        wwwButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get(baseString + "wwwButton")), this.skin);
        wwwButton.setWidth(200.0F);
        wwwButton.setHeight(50.0F);
        wwwButton.setColor(Colors.ICE);
        wwwButton.setVisible(false);
        wwwButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                visitSite(selectedMod.url);
            }
        });

        backButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get(baseString + "backButton")), this.skin);
        backButton.setWidth(200.0F);
        backButton.setHeight(50.0F);
        backButton.setColor(Colors.EXPLOSION);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                GameApplication.SetScreen(new OptionsScreen());
            }
        });

        fullTable.row();
        fullTable.add(StringManager.get(baseString + "modsLabel")).align(Align.left).padTop(14.0F).padBottom(6.0F);
        fullTable.row();

        buttonTable.add(enableButton);
        buttonTable.add(disableButton);
        buttonTable.add(wwwButton);
        buttonTable.add(backButton);
        fullTable.add(buttonTable);
        fullTable.row();

        // texture
        Texture tfBackground = new Texture(Gdx.files.internal("ui/tfbackground.png"));
        Texture scroll_horizontal = new Texture(Gdx.files.internal("ui/scroll_horizontal.png"));
        Texture knob_scroll = new Texture(Gdx.files.internal("ui/knob_scroll.png"));

        //ScrollPane
        ScrollPane.ScrollPaneStyle sps = new ScrollPane.ScrollPaneStyle();
        sps.vScroll = new TextureRegionDrawable(new TextureRegion(scroll_horizontal));
        sps.vScrollKnob = new TextureRegionDrawable(new TextureRegion(knob_scroll));

        // list stuff
        List.ListStyle listS = new List.ListStyle();
        listS.font = font;
        listS.fontColorSelected = Color.BLACK;
        listS.fontColorUnselected = Color.GRAY;
        listS.selection = new TextureRegionDrawable(new TextureRegion(tfBackground));
        List list2 = new List(listS);

        Array<Mod> arrayMods = new Array<Mod>();
        arrayMods.addAll(Game.modManager.modList);

        list2.setItems(arrayMods);
        list2.pack();
        ScrollPane scrollPane = new ScrollPane(list2, sps);
        scrollPane.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
               selectMod((Mod)list2.getSelected());
            }
        });

        nameLabel = new Label(null, skin);
        authorLabel = new Label(null, skin);
        descLabel = new Label(null, skin);
        versionLabel = new Label(null, skin);
        stateLabel = new Label(null, skin);

        fullTable.add(nameLabel).align(Align.left).row();
        fullTable.add(authorLabel).align(Align.left).row();
        fullTable.add(descLabel).align(Align.left).row();
        fullTable.add(versionLabel).align(Align.left).row();
        fullTable.add(stateLabel).align(Align.left).row();

        fullTable.add(scrollPane).align(Align.center);

        fullTable.pack();
    }

    private void selectMod(Mod mod) {
        nameLabel.setText(StringManager.get(baseString + "name") + mod.name);
        authorLabel.setText(StringManager.get(baseString + "author") + mod.author);
        descLabel.setText(StringManager.get(baseString + "description") + mod.description);
        versionLabel.setText(StringManager.get(baseString + "version") + mod.version);
        stateLabel.setText(StringManager.get(baseString + "modState") + mod.modState);


        selectedMod = mod;

        switch (mod.modState) {
            case Enabled:
                this.disableButton.setVisible(true);
                this.enableButton.setVisible(false);
                break;

            case Disabled:
                this.enableButton.setVisible(true);
                this.disableButton.setVisible(false);
                break;
        }
        if (selectedMod.url != null && !selectedMod.url.trim().isEmpty()) {
            this.wwwButton.setVisible(true);
        } else {
            this.wwwButton.setVisible(false);
        }

    }

    public void draw(float delta) {
        super.draw(delta);

        this.ui.draw();
    }

    public void tick(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            GameApplication.SetScreen(new OptionsScreen());
        }

        super.tick(delta);
    }

    private void enableMod() {
        this.enableButton.setVisible(false);
        ModSettings newSettingsMod = new ModSettings(selectedMod.modState, selectedMod.modPath);

        newSettingsMod.modState = ModSettings.ModState.Enabled;

        String modFile = Game.toJson(newSettingsMod, ModSettings.class);

        try {
            Files.write(Paths.get(newSettingsMod.modPath + File.separator + Game.modManager.MOD_SETTINGS_FILE), modFile.getBytes());
        } catch (Exception ex) {
            Gdx.app.error("ModManager", ex.getMessage());
        }
    }

    private void disableMod() {
        this.disableButton.setVisible(false);
        ModSettings newSettingsMod = new ModSettings(selectedMod.modState, selectedMod.modPath);

        newSettingsMod.modState = ModSettings.ModState.Disabled;

        String modFile = Game.toJson(newSettingsMod, ModSettings.class);

        try {
            Files.write(Paths.get(newSettingsMod.modPath + File.separator + Game.modManager.MOD_SETTINGS_FILE), modFile.getBytes());
        } catch (Exception ex) {
            Gdx.app.error("ModManager", ex.getMessage());
        }
    }

    private void visitSite(String www) {
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(www));
            } catch (Exception ex) {
                Gdx.app.error("ModManagerScreen", ex.getMessage());
            }
        }
    }
}
