package com.interrupt.dungeoneer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.GameApplication;
import com.interrupt.dungeoneer.game.Colors;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.managers.StringManager;
import net.cotd.delverunlimited.helper.Mod;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

public class ModManagerScreen extends BaseScreen {

    private Table fullTable = null;
    Array<Table> selectedUI = new Array();
    private Mod selectedMod = null;
    private static String baseString = "screens.ModManagerScreen.";

    private TextButton enableButton;
    private TextButton disableButton;
    private TextButton wwwButton;

    public ModManagerScreen() {
        this.screenName = "ConfirmExitScreen";

        this.viewport.setWorldWidth(Gdx.graphics.getWidth() / this.uiScale);
        this.viewport.setWorldHeight(Gdx.graphics.getHeight() / this.uiScale);
        this.ui = new Stage(this.viewport);

        this.fullTable = new Table(this.skin);
        this.fullTable.setFillParent(true);
        this.fullTable.align(2);

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
        float fontScale = 1.0F;
        float rowHeight = 15.0F;

        enableButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get(baseString + "enableButton")), this.skin);
        enableButton.setWidth(200.0F);
        enableButton.setHeight(50.0F);
        enableButton.setColor(Colors.PLAY_BUTTON);
        enableButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                enableMod();
            }
        });

        disableButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get(baseString + "disableButton")), this.skin);
        disableButton.setWidth(200.0F);
        disableButton.setHeight(50.0F);
        disableButton.setColor(Colors.ERASE_BUTTON);
        disableButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                disableMod();
            }
        });

        wwwButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get(baseString + "wwwButton")), this.skin);
        wwwButton.setWidth(200.0F);
        wwwButton.setHeight(50.0F);
        wwwButton.setColor(Colors.ICE);
        wwwButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                visitSite(selectedMod.url);
            }
        });

        NinePatchDrawable fileSelectBg = new NinePatchDrawable(new NinePatch(this.skin.getRegion("save-select"), 1, 1, 1, 5));
        for (Mod mod : Game.modManager.modList) {
            final Table t = new Table(this.skin);
            t.add("Name: " + mod.name);
            t.setBackground(fileSelectBg);
            t.center();
            t.row();

            Table t2 = new Table(this.skin);
            t2.add("Author: " + mod.author);
            t2.row();
            t2.add("Description: " + mod.description);
            t2.row();
            t2.add("Version: " + mod.version + " | Mod State: " + mod.modState);
            t2.pack();

            t.add(t2);
            t.pack();

            t.setTouchable(Touchable.enabled);
            t.addListener(new ClickListener()
            {
                public void clicked(InputEvent event, float x, float y)
                {
                    selectMod(t, mod);
                }
            });
            t.setColor(Color.GRAY);
            selectedUI.add(t);

            this.fullTable.add(t).padTop(4.0F).colspan(2);
            this.fullTable.row();
        }

        this.fullTable.row();

        Table buttonTable = new Table();
        buttonTable.add(enableButton);
        buttonTable.add(disableButton);
        buttonTable.add(wwwButton);
        buttonTable.getCell(enableButton).padRight(4.0F);
        buttonTable.getCell(disableButton).padRight(4.0F);
        buttonTable.getCell(wwwButton).padRight(4.0F);

        this.fullTable.add(buttonTable);
       // this.fullTable.getCell(header).align(1).colspan(3).padBottom(8.0F);
        this.fullTable.getCell(buttonTable).colspan(2).padTop(8.0F);

        this.fullTable.pack();

        this.enableButton.setVisible(false);
        this.disableButton.setVisible(false);
        this.wwwButton.setVisible(false);
    }

    private void selectMod(Table table, Mod mod) {
        selectedMod = mod;
        for (int i = 0; i < this.selectedUI.size; i++) {
            this.selectedUI.get(i).setColor(Color.GRAY);
            switch (mod.modState) {
                case Enabled:
                    this.disableButton.setVisible(true);
                    break;

                case Disabled:
                    this.enableButton.setVisible(true);
                    break;
            }
            if(mod.url != null) {
                this.wwwButton.setVisible(true);
            }
        }

        if (table != null) {
            table.setColor(Color.WHITE);
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
        Mod newMod = selectedMod;

        newMod.modState = Mod.ModState.Enabled;

        String modFile = Game.toJson(newMod, Mod.class);

        try {
            Files.write(Paths.get(newMod.modPath + File.separator + "mod.json"), modFile.getBytes());
        } catch (Exception ex) {
            Gdx.app.error("ModManager", ex.getMessage());
        }
    }

    private void disableMod() {
        this.disableButton.setVisible(false);
        Mod newMod = selectedMod;

        newMod.modState = Mod.ModState.Disabled;

        String modFile = Game.toJson(newMod, Mod.class);

        try {
            Files.write(Paths.get(newMod.modPath + File.separator + "mod.json"), modFile.getBytes());
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
