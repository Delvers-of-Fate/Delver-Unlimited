package com.interrupt.dungeoneer.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.interrupt.dungeoneer.Art;
import com.interrupt.dungeoneer.GameManager;
import com.interrupt.dungeoneer.entities.Entity;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.entities.Monster;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.entities.items.Armor;
import com.interrupt.dungeoneer.entities.items.Gold;
import com.interrupt.dungeoneer.entities.items.QuestItem;
import com.interrupt.dungeoneer.entities.items.Sword;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.gfx.GlRenderer;
import com.interrupt.dungeoneer.gfx.Tesselator;
import com.interrupt.dungeoneer.ui.UiSkin;
import com.interrupt.managers.ItemManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class DebugOverlay extends WindowOverlay {
    final Player player;
    protected TextButton doneBtn;
    protected final Color selectedValue = new Color(0.6F, 1.0F, 0.6F, 1.0F);
    protected final Color unselectedValue = new Color(0.6F, 0.6F, 0.6F, 1.0F);
    private boolean escapePressed = false;

    public DebugOverlay(Player player) {
        this.player = player;
    }

    public void onShow() {
        super.onShow();
    }

    public void onHide() {
        super.onHide();
    }

    protected void addItem(Table table, final String text, final HashMap<String, Array<Monster>> value) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContentFromMonsters(text, value));
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addItems(Table table, final String text, final HashMap<String, Array<Item>> value) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContentFromItems(text, value));
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addMonsters(Table table, final String text, final Array<Monster> value) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent(text, value));
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addLevelUpItem(Table table, String text) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //OverlayManager.instance.remove(DebugOverlay.this);
                ++Game.instance.player.level;
                OverlayManager.instance.push(new LevelUpOverlayMark2(Game.instance.player));
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addGoDownItem(Table table, String text) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //OverlayManager.instance.remove(DebugOverlay.this);
                Game.instance.level.down.changeLevel(Game.instance.level);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addFlightItem(Table table, String text) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //OverlayManager.instance.remove(DebugOverlay.this);
                Game.instance.player.floating = !Game.instance.player.floating;
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addNoClipItem(Table table, String text) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //OverlayManager.instance.remove(DebugOverlay.this);
                Game.instance.player.isSolid = !Game.instance.player.isSolid;
                Game.instance.player.floating = !Game.instance.player.isSolid;
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addGodModeOption(Table table, String text) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //OverlayManager.instance.remove(DebugOverlay.this);
                Game.instance.player.godMode = !Game.instance.player.godMode;
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addNoTargetOption(Table table, String text) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //OverlayManager.instance.remove(DebugOverlay.this);
                Game.instance.player.invisible = !Game.instance.player.invisible;
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addSuicideItem(Table table) {
        final Label name = new Label("DIE", (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //OverlayManager.instance.remove(DebugOverlay.this);
                Game.instance.player.die();
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addRefreshItem(Table table) {
        final Label name = new Label("REFRESH", (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                //OverlayManager.instance.remove(DebugOverlay.this);
                DebugOverlay.this.refreshData();
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addItems(Table table, final String category, final String text, final Array<Item> items) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContentFromItems(category != "" ? category + "/" + text : text, items));
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addItem(Table table, String text, final Item item) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Item i = ItemManager.Copy(item.getClass(), item);
                if (i != null) {
                    ItemManager.setItemLevel(Game.instance.player.level, i);
                }

                Game.instance.player.dropItem(i, Game.instance.level, 0.2F);
                //OverlayManager.instance.remove(DebugOverlay.this);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    protected void addItem(Table table, String text, final Monster monster) {
        final Label name = new Label(text.toUpperCase(), (LabelStyle)this.skin.get("input", LabelStyle.class));
        name.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Monster copy = Game.instance.monsterManager.Copy(monster.getClass(), monster);
                Player p = Game.instance.player;
                float projx = (0.0F * (float)Math.cos((double)p.rot) + 1.0F * (float)Math.sin((double)p.rot)) * 1.0F;
                float projy = (1.0F * (float)Math.cos((double)p.rot) - 0.0F * (float)Math.sin((double)p.rot)) * 1.0F;
                copy.isActive = true;
                copy.x = p.x + 0.5F + projx * 2.0F;
                copy.y = p.y + 0.5F + projy * 2.0F;
                copy.z = p.z + 0.35F;
                copy.xa = projx * 0.3F;
                copy.ya = projy * 0.3F;
                copy.za = 0.01F;
                copy.Init(Game.instance.level, DebugOverlay.this.player.level);
                Game.instance.level.entities.add(copy);
                //OverlayManager.instance.remove(DebugOverlay.this);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("inputover", LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                name.setStyle((LabelStyle)DebugOverlay.this.skin.get("input", LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);
        table.add(name).align(8);
        table.row();
    }

    public Table makeContent() {
        this.buttonOrder.clear();
        this.doneBtn = new TextButton("DONE", (TextButtonStyle)this.skin.get(TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);
        this.doneBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                OverlayManager.instance.remove(DebugOverlay.this);
            }
        });
        Table contentTable = new Table();
        Label title = new Label("DEBUG!", (LabelStyle)this.skin.get(LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        Array<Item> wands = new Array();
        wands.addAll(Game.instance.itemManager.wands);
        Array<Item> food = new Array();
        food.addAll(Game.instance.itemManager.food);
        Array<Item> scrolls = new Array();
        scrolls.addAll(Game.instance.itemManager.scrolls);
        Array<Item> potions = new Array();
        potions.addAll(Game.instance.player.shuffledPotions);
        Array<Item> uniques = new Array();
        if (Game.instance.itemManager.unique != null) {
            uniques.addAll(Game.instance.itemManager.unique);
        }

        HashMap<String, Array<Item>> armors = new HashMap();
        Iterator var10 = Game.instance.itemManager.armor.entrySet().iterator();

        Array junk;
        while(var10.hasNext()) {
            Entry<String, Array<Armor>> entry = (Entry)var10.next();
            junk = new Array();
            junk.addAll((Array)entry.getValue());
            armors.put(entry.getKey(), junk);
        }

        HashMap<String, Array<Item>> melee = new HashMap();
        Iterator var16 = Game.instance.itemManager.melee.entrySet().iterator();

        while(var16.hasNext()) {
            Entry<String, Array<Sword>> entry = (Entry)var16.next();
            Array<Item> items = new Array();
            items.addAll((Array)entry.getValue());
            melee.put(entry.getKey(), items);
        }

        HashMap<String, Array<Item>> ranged = new HashMap();
        Iterator var19 = Game.instance.itemManager.ranged.entrySet().iterator();

        while(var19.hasNext()) {
            Entry<String, Array<Item>> entry = (Entry)var19.next();
            Array<Item> items = new Array();
            items.addAll((Array)entry.getValue());
            ranged.put(entry.getKey(), items);
        }

        junk = new Array();
        junk.addAll(Game.instance.itemManager.junk);
        this.addItem(contentTable, "MONSTERS", Game.instance.monsterManager.monsters);
        this.addItems(contentTable, "", "WANDS", wands);
        this.addItems(contentTable, "ARMOR", armors);
        this.addItems(contentTable, "MELEE", melee);
        this.addItems(contentTable, "RANGED", ranged);
        this.addItems(contentTable, "", "FOOD", food);
        this.addItems(contentTable, "", "SCROLLS", scrolls);
        this.addItems(contentTable, "", "POTIONS", potions);
        this.addItems(contentTable, "", "UNIQUES", uniques);
        this.addItems(contentTable, "", "JUNK", junk);
        this.addItem(contentTable, "ORB", (Item)(new QuestItem()));
        this.addItem(contentTable, "Gold", (Item)(new Gold(200)));
        this.addLevelUpItem(contentTable, "LEVEL UP!");
        this.addFlightItem(contentTable, "TOGGLE FLIGHT");
        this.addNoClipItem(contentTable, "TOGGLE NOCLIP");
        this.addGodModeOption(contentTable, "TOGGLE GODMODE");
        this.addNoTargetOption(contentTable, "TOGGLE NOTARGET");
        this.addRefreshItem(contentTable);
        this.addSuicideItem(contentTable);
        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);
        this.buttonOrder.add(this.doneBtn);
        return contentTable;
    }

    protected Table makeContentFromMonsters(String titleText, HashMap<String, Array<Monster>> objects) {
        this.buttonOrder.clear();
        this.doneBtn = new TextButton("BACK", (TextButtonStyle)this.skin.get(TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);
        this.doneBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent());
            }
        });
        Table contentTable = new Table();
        Label title = new Label(titleText, (LabelStyle)this.skin.get(LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        Iterator var6 = objects.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, Array<Monster>> entry = (Entry)var6.next();
            this.addMonsters(contentTable, (String)entry.getKey(), (Array)entry.getValue());
        }

        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);
        this.buttonOrder.add(this.doneBtn);
        return contentTable;
    }

    protected Table makeContentFromItems(String titleText, HashMap<String, Array<Item>> objects) {
        this.buttonOrder.clear();
        this.doneBtn = new TextButton("BACK", (TextButtonStyle)this.skin.get(TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);
        this.doneBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent());
            }
        });
        Table contentTable = new Table();
        Label title = new Label(titleText, (LabelStyle)this.skin.get(LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        Iterator var6 = objects.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<String, Array<Item>> entry = (Entry)var6.next();
            this.addItems(contentTable, titleText, (String)entry.getKey(), (Array)entry.getValue());
        }

        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);
        this.buttonOrder.add(this.doneBtn);
        return contentTable;
    }

    protected Table makeContent(Entity[] objects) {
        this.buttonOrder.clear();
        this.doneBtn = new TextButton("BACK", (TextButtonStyle)this.skin.get(TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);
        this.doneBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent());
            }
        });
        Table contentTable = new Table();
        Label title = new Label("DEBUG MENU", (LabelStyle)this.skin.get(LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        Entity[] var5 = objects;
        int var6 = objects.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Entity entry = var5[var7];
            if (entry instanceof Item) {
                this.addItem(contentTable, ((Item)entry).GetName(), (Item)entry);
            } else if (entry instanceof Monster) {
                this.addItem(contentTable, ((Monster)entry).name, (Monster)entry);
            }
        }

        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);
        this.buttonOrder.add(this.doneBtn);
        return contentTable;
    }

    protected Table makeContentFromItems(String titleText, Array<Item> objects) {
        this.buttonOrder.clear();
        this.doneBtn = new TextButton("BACK", (TextButtonStyle)this.skin.get(TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);
        this.doneBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent());
            }
        });
        Table contentTable = new Table();
        Label title = new Label(titleText, (LabelStyle)this.skin.get(LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        Iterator var6 = objects.iterator();

        while(var6.hasNext()) {
            Item entry = (Item)var6.next();
            this.addItem(contentTable, entry.GetName(), entry);
        }

        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);
        this.buttonOrder.add(this.doneBtn);
        return contentTable;
    }

    protected Table makeContent(String titleText, Array<Monster> objects) {
        this.buttonOrder.clear();
        this.doneBtn = new TextButton("BACK", (TextButtonStyle)this.skin.get(TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);
        this.doneBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent());
            }
        });
        Table contentTable = new Table();
        Label title = new Label(titleText, (LabelStyle)this.skin.get(LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        Iterator var6 = objects.iterator();

        while(var6.hasNext()) {
            Monster entry = (Monster)var6.next();
            this.addItem(contentTable, entry.name, entry);
        }

        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);
        this.buttonOrder.add(this.doneBtn);
        return contentTable;
    }

    public void refreshData() {
        try {
            Art.KillCache();
            Game.instance.loadManagers();
            GameManager.renderer.initTextures();
            GameManager.renderer.initShaders();
            GlRenderer.staticMeshPool.resetAndDisposeAllMeshes();
            Tesselator.tesselatorMeshPool.resetAndDisposeAllMeshes();
            Iterator var1 = Game.GetLevel().entities.iterator();

            Entity e;
            while(var1.hasNext()) {
                e = (Entity)var1.next();
                if (e.drawable != null) {
                    e.drawable.refresh();
                    e.drawable.update(e);
                }
            }

            var1 = Game.GetLevel().static_entities.iterator();

            while(var1.hasNext()) {
                e = (Entity)var1.next();
                if (e.drawable != null) {
                    e.drawable.refresh();
                    e.drawable.update(e);
                }
            }

            var1 = Game.GetLevel().non_collidable_entities.iterator();

            while(var1.hasNext()) {
                e = (Entity)var1.next();
                if (e.drawable != null) {
                    e.drawable.refresh();
                    e.drawable.update(e);
                }
            }

            GameManager.renderer.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Game.GetLevel().isDirty = true;
            UiSkin.loadSkin();
        } catch (Exception var3) {
            Gdx.app.log("Delver", "Could not refresh: " + var3.getMessage());
        }

    }

    public void tick(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            OverlayManager.instance.remove(DebugOverlay.this);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            OverlayManager.instance.remove(DebugOverlay.this);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {

            // fix for instant close
            if(escapePressed) {
                OverlayManager.instance.remove(DebugOverlay.this);
            } else {
                escapePressed = true;
            }
        }
    }
}
