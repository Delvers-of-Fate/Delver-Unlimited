package com.interrupt.dungeoneer.overlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.managers.StringManager;

import java.text.MessageFormat;
import java.util.Iterator;

public class LevelUpOverlay extends WindowOverlay {
    String selectedAttribute = "";
    final Player player;
    protected TextButton doneBtn;
    protected ArrayMap<String, Integer> startingValues = new ArrayMap();
    protected ArrayMap<String, Label> valueLabels = new ArrayMap();
    protected final Color selectedValue = new Color(0.6F, 1.0F, 0.6F, 1.0F);
    protected final Color unselectedValue = new Color(0.6F, 0.6F, 0.6F, 1.0F);

    public LevelUpOverlay(Player player) {
        this.player = player;
    }

    public void onShow() {
        super.onShow();
        Audio.setMusicVolume(0.3F);
        Audio.playSound("music/levelup.mp3", 0.4F);
    }

    public void onHide() {
        super.onHide();
        Audio.setMusicVolume(1.0F);
        Audio.playSound("/ui/ui_dialogue_close.mp3", 0.35F);
    }

    protected void addAttribute(Table table, final String text, Integer currentValue) {
        boolean selected = this.selectedAttribute.equals(text);
        String translatedText = StringManager.get("overlays.LevelUpOverlay." + text.toUpperCase());
        final Label attributeName = new Label(translatedText, this.skin.get("input", LabelStyle.class));
        final Label value = new Label(selected ? Integer.valueOf(currentValue + 1).toString() : currentValue.toString(), this.skin.get(LabelStyle.class));
        this.valueLabels.put(text, value);
        this.startingValues.put(text, currentValue);
        if (!selected) {
            value.setColor(this.unselectedValue);
        } else {
            value.setColor(this.selectedValue);
        }

        attributeName.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Iterator var4 = LevelUpOverlay.this.valueLabels.entries().iterator();

                while(var4.hasNext()) {
                    Entry<String, Label> entry = (Entry)var4.next();
                    ((Label)entry.value).setColor(LevelUpOverlay.this.unselectedValue);
                    ((Label)entry.value).setText(((Integer)LevelUpOverlay.this.startingValues.get(entry.key)).toString());
                }

                LevelUpOverlay.this.selectedAttribute = text;
                LevelUpOverlay.this.doneBtn.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                Integer startingValue = ((Integer)LevelUpOverlay.this.startingValues.get(LevelUpOverlay.this.selectedAttribute)).intValue() + 1;
                value.setColor(LevelUpOverlay.this.selectedValue);
                value.setText(startingValue.toString());
                Audio.playSound("ui/ui_statincrease.mp3", 0.35F);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                attributeName.setStyle((LabelStyle)LevelUpOverlay.this.skin.get("inputover", LabelStyle.class));
                if (!LevelUpOverlay.this.selectedAttribute.equals(text)) {
                    value.setColor(0.7F, 0.7F, 0.7F, 1.0F);
                }

            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                attributeName.setStyle((LabelStyle)LevelUpOverlay.this.skin.get("input", LabelStyle.class));
                if (!LevelUpOverlay.this.selectedAttribute.equals(text)) {
                    value.setColor(0.6F, 0.6F, 0.6F, 1.0F);
                }

            }
        });
        this.buttonOrder.add(attributeName);
        table.add(attributeName).align(8).padBottom(2.0F);
        table.add(value).align(16).padLeft(20.0F).padBottom(2.0F);
        table.row();
    }

    protected void applyStats() {
        Player p = Game.instance.player;
        p.maxHp = (int)((float)p.stats.END * ((float)p.stats.END / 3.0F)) + 4;
        p.hp = p.maxHp;
    }

    protected Table makeContent() {
        String paddedButtonText = " {0} ";

        this.buttonOrder.clear();
        this.valueLabels.clear();
        this.startingValues.clear();
        this.doneBtn = new TextButton(MessageFormat.format(paddedButtonText, StringManager.get("overlays.LevelUpOverlay.done")), this.skin.get(TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);
        if (this.selectedAttribute.equals("")) {
            this.doneBtn.setColor(1.0F, 1.0F, 1.0F, 0.5F);
        }

        this.doneBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                String chosenAttribute = LevelUpOverlay.this.selectedAttribute.toUpperCase();
                if (!chosenAttribute.equals("")) {
                    if (chosenAttribute.equals("ATTACK")) {
                        ++LevelUpOverlay.this.player.stats.ATK;
                    } else if (chosenAttribute.equals("SPEED")) {
                        ++LevelUpOverlay.this.player.stats.SPD;
                    } else if (chosenAttribute.equals("HEALTH")) {
                        ++LevelUpOverlay.this.player.stats.END;
                    } else if (chosenAttribute.equals("MAGIC")) {
                        ++LevelUpOverlay.this.player.stats.MAG;
                    } else if (chosenAttribute.equals("AGILITY")) {
                        ++LevelUpOverlay.this.player.stats.DEX;
                    } else if (chosenAttribute.equals("DEFENSE")) {
                        ++LevelUpOverlay.this.player.stats.DEF;
                    }

                    LevelUpOverlay.this.applyStats();
                    OverlayManager.instance.remove(LevelUpOverlay.this);
                }

            }
        });
        Table contentTable = new Table();
        Label title = new Label(StringManager.get("overlays.LevelUpOverlay.levelUpLabel"), (LabelStyle)this.skin.get(LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        Label text = new Label(StringManager.get("overlays.LevelUpOverlay.chooseYourFateLabel"), (LabelStyle)this.skin.get(LabelStyle.class));
        text.setFontScale(0.6F);
        contentTable.add(text).colspan(2).padBottom(8.0F);
        contentTable.row();
        Array<LevelUpOverlay.Stat> allStats = this.getAllStats();
        allStats.shuffle();

        for(int i = 0; i < 3; ++i) {
            this.addAttribute(contentTable, ((LevelUpOverlay.Stat)allStats.get(i)).name, ((LevelUpOverlay.Stat)allStats.get(i)).stat);
        }

        contentTable.add(this.doneBtn).padTop(6.0F).align(1).colspan(2);
        this.buttonOrder.add(this.doneBtn);
        return contentTable;
    }

    public Array<LevelUpOverlay.Stat> getAllStats() {
        Array<LevelUpOverlay.Stat> stats = new Array();
        stats.add(new LevelUpOverlay.Stat("Attack", this.player.stats.ATK));
        stats.add(new LevelUpOverlay.Stat("Speed", this.player.stats.SPD));
        stats.add(new LevelUpOverlay.Stat("Health", this.player.stats.END));
        stats.add(new LevelUpOverlay.Stat("Magic", this.player.stats.MAG));
        stats.add(new LevelUpOverlay.Stat("Agility", this.player.stats.DEX));
        stats.add(new LevelUpOverlay.Stat("Defense", this.player.stats.DEF));
        return stats;
    }

    protected class Stat {
        String name;
        int stat;

        public Stat(String name, int stat) {
            this.name = name;
            this.stat = stat;
        }
    }
}
