package com.interrupt.dungeoneer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.interrupt.dungeoneer.GameApplication;
import com.interrupt.dungeoneer.game.Colors;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.managers.StringManager;

import java.text.MessageFormat;

public class ConfirmExitScreen extends BaseScreen {

    private TextButton exitButton;
    private TextButton declineButton;
    private Table buttonTable = null;
    private Table fullTable = null;
    private static String baseString = "screens.ConfirmExitScreen.";

    public ConfirmExitScreen() {
        this.screenName = "ConfirmExitScreen";

        this.viewport.setWorldWidth(Gdx.graphics.getWidth() / this.uiScale);
        this.viewport.setWorldHeight(Gdx.graphics.getHeight() / this.uiScale);
        this.ui = new Stage(this.viewport);

        this.fullTable = new Table(this.skin);
        this.fullTable.setFillParent(true);
        this.fullTable.align(2);

        this.buttonTable = new Table();

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

        // exit btn
        this.exitButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.MainMenuScreen.exitButton")), this.skin);
        this.exitButton.setColor(Colors.ERASE_BUTTON);
        this.exitButton.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                Gdx.app.exit();
            }
        });

        // decline btn
        this.declineButton = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("screens.ConfirmScreen.declineButton")), this.skin);
        this.declineButton.setColor(Colors.PLAY_BUTTON);
        this.declineButton.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                GameApplication.SetScreen(new MainMenuScreen());
            }
        });

        this.fullTable.add().height(this.uiScale * 25);
        this.fullTable.add(StringManager.get(baseString + "message")).align(8).padTop(14.0F).padBottom(6.0F);
        this.fullTable.row();

        this.buttonTable.clearChildren();


        // Render the tables
        this.buttonTable.add(this.declineButton).height(20.0F);
        this.buttonTable.add().width(70F);
        this.buttonTable.add(this.exitButton).height(20.0F);

        this.buttonTable.pack();

        this.fullTable.row();
        this.fullTable.add(this.buttonTable).colspan(2).height(30.0F).fill(true, false).align(1);
        this.fullTable.row();
        this.fullTable.add().colspan(2).height(60.0F);

        this.fullTable.pack();

        this.fullTable.addAction(Actions.sequence(Actions.fadeOut(1.0E-4F), Actions.fadeIn(0.2F)));

    }

    public void draw(float delta)
    {
        super.draw(delta);
        this.ui.draw();
    }

    public void tick(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            GameApplication.SetScreen(new MainMenuScreen());
        }

        super.tick(delta);
    }


}
