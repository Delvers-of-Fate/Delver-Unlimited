package com.interrupt.dungeoneer.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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
import com.interrupt.dungeoneer.ui.UiSkin;
import com.interrupt.managers.ItemManager;

import java.util.HashMap;
import java.util.Map;

public class DebugOverlay
        extends WindowOverlay
{
    final Player player;
    protected TextButton doneBtn;
    protected final Color selectedValue = new Color(0.6F, 1.0F, 0.6F, 1.0F);
    protected final Color unselectedValue = new Color(0.6F, 0.6F, 0.6F, 1.0F);
    private boolean escapePressed = false;

    public DebugOverlay(Player player)
    {
        this.player = player;
    }

    public void onShow()
    {
        super.onShow();
    }

    public void onHide()
    {
        super.onHide();
    }

    protected void addItem(Table table, final String text, final HashMap<String, Array<Monster>> value)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContentFromMonsters(text, value));
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addItems(Table table, final String text, final HashMap<String, Array<Item>> value)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContentFromItems(text, value));
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addMonsters(Table table, final String text, final Array<Monster> value)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent(text, value));
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addLevelUpItem(Table table, String text)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                OverlayManager.instance.remove(thisOverlay);
                Game.instance.player.level += 1;
                OverlayManager.instance.push(new LevelUpOverlayMark2(Game.instance.player));
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addGoDownItem(Table table, String text)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                OverlayManager.instance.remove(thisOverlay);
                Game.instance.level.down.changeLevel(Game.instance.level);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addFlightItem(Table table, String text)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                OverlayManager.instance.remove(thisOverlay);
                Game.instance.player.floating = (!Game.instance.player.floating);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addNoClipItem(Table table, String text)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                OverlayManager.instance.remove(thisOverlay);
                Game.instance.player.isSolid = (!Game.instance.player.isSolid);
                Game.instance.player.floating = (!Game.instance.player.isSolid);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addDrawBoxOption(Table table, String text)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                Game.drawDebugBoxes = !Game.drawDebugBoxes;
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addNoTargetOption(Table table, String text)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                Game.instance.player.invisible = (!Game.instance.player.invisible);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addSuicideItem(Table table)
    {
        final Label name = new Label("DIE", (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                OverlayManager.instance.remove(thisOverlay);
                Game.instance.player.die();
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addRefreshItem(Table table)
    {
        final Label name = new Label("REFRESH", (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                OverlayManager.instance.remove(thisOverlay);
                DebugOverlay.this.refreshData();
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addHealItem(Table table) {
        final Label name = new Label("REFRESH", (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                player.hp = player.getMaxHp();
                player.clearStatusEffects();
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addItems(Table table, final String category, final String text, final Array<Item> items)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        Overlay thisOverlay = this;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContentFromItems(category != "" ? category + "/" + text : text, items));
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addItem(Table table, String text, Item item)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        final Item value = item;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                Item i = ItemManager.Copy(value.getClass(), value);
                if (i != null) {
                    ItemManager.setItemLevel(Integer.valueOf(Game.instance.player.level), i);
                }
                Game.instance.player.dropItem(i, Game.instance.level, 0.2F);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    protected void addItem(Table table, String text, Monster monster)
    {
        final Label name = new Label(text.toUpperCase(), (Label.LabelStyle)this.skin.get("input", Label.LabelStyle.class));

        final Overlay thisOverlay = this;

        final Monster value = monster;

        name.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                Monster copy = Game.instance.monsterManager.Copy(value.getClass(), value);
                Player p = Game.instance.player;

                float projx = (0.0F * (float)Math.cos(p.rot) + 1.0F * (float)Math.sin(p.rot)) * 1.0F;
                float projy = (1.0F * (float)Math.cos(p.rot) - 0.0F * (float)Math.sin(p.rot)) * 1.0F;

                copy.isActive = true;
                copy.x = (p.x + 0.5F + projx * 2.0F);
                copy.y = (p.y + 0.5F + projy * 2.0F);
                copy.z = (p.z + 0.35F);
                copy.xa = (projx * 0.3F);
                copy.ya = (projy * 0.3F);
                copy.za = 0.01F;

                copy.Init(Game.instance.level, DebugOverlay.this.player.level);

                Game.instance.level.entities.add(copy);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("inputover", Label.LabelStyle.class));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
            {
                name.setStyle((Label.LabelStyle)DebugOverlay.this.skin.get("input", Label.LabelStyle.class));
            }
        });
        this.buttonOrder.add(name);

        table.add(name).align(8);

        table.row();
    }

    public Table makeContent()
    {
        this.buttonOrder.clear();

        final Overlay thisOverlay = this;

        this.doneBtn = new TextButton("DONE", (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);

        this.doneBtn.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                OverlayManager.instance.remove(thisOverlay);
            }
        });
        Table contentTable = new Table();
        Label title = new Label("DEBUG!", (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();

        Array<Item> currency = new Array();

        currency.add(new Gold());
        currency.add(new Gold(5));
        currency.add(new Gold(10));

        Array<Item> wands = new Array();
        wands.addAll(Game.instance.itemManager.wands);

        Array<Item> food = new Array();
        food.addAll(Game.instance.itemManager.food);

        Array<Item> scrolls = new Array();
        scrolls.addAll(Game.instance.itemManager.scrolls);

        Array<Item> potions = new Array();
        potions.addAll(Game.instance.itemManager.potions);

        Array<Item> uniques = new Array();
        if (Game.instance.itemManager.unique != null) {
            uniques.addAll(Game.instance.itemManager.unique);
        }
        HashMap<String, Array<Item>> armors = new HashMap();
        for (Map.Entry<String, Array<Armor>> entry : Game.instance.itemManager.armor.entrySet())
        {
            Array<Item> items = new Array();
            items.addAll((Array)entry.getValue());
            armors.put(entry.getKey(), items);
        }
        HashMap<String, Array<Item>> melee = new HashMap();
        for (Map.Entry<String, Array<Sword>> entry : Game.instance.itemManager.melee.entrySet())
        {
            Array<Item> items = new Array();
            items.addAll((Array)entry.getValue());
            melee.put(entry.getKey(), items);
        }
        HashMap<String, Array<Item>> ranged = new HashMap();
        for (Map.Entry<String, Array<Item>> entry : Game.instance.itemManager.ranged.entrySet())
        {
            Array<Item> items = new Array();
            items.addAll((Array)entry.getValue());
            ranged.put(entry.getKey(), items);
        }
        Array<Item> junk = new Array();
        junk.addAll(Game.instance.itemManager.junk);

        addItem(contentTable, "MONSTERS", Game.instance.monsterManager.monsters);
        addItems(contentTable, "", "WANDS", wands);
        addItems(contentTable, "ARMOR", armors);
        addItems(contentTable, "MELEE", melee);
        addItems(contentTable, "RANGED", ranged);

        addItems(contentTable, "", "", new Array()); // seperator

        addItems(contentTable, "", "FOOD", food);
        addItems(contentTable, "", "SCROLLS", scrolls);
        addItems(contentTable, "", "POTIONS", potions);
        addItems(contentTable, "", "UNIQUES", uniques);
        addItems(contentTable, "", "CURRENCY", currency);
        addItems(contentTable, "", "JUNK", junk);
        addItem(contentTable, "ORB", new QuestItem());

        addItems(contentTable, "", "", new Array()); // seperator

        addFlightItem(contentTable, "TOGGLE FLIGHT");
        addNoClipItem(contentTable, "TOGGLE NOCLIP");
        addNoTargetOption(contentTable, "TOGGLE NOTARGET");
        addDrawBoxOption(contentTable, "TOGGLE DRAWBOX");

        addItems(contentTable, "", "", new Array()); // seperator

        addLevelUpItem(contentTable, "LEVEL UP!");
        addHealItem(contentTable);
        addRefreshItem(contentTable);
        addSuicideItem(contentTable);

        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);

        this.buttonOrder.add(this.doneBtn);

        return contentTable;
    }

    protected Table makeContentFromMonsters(String titleText, HashMap<String, Array<Monster>> objects)
    {
        this.buttonOrder.clear();

        final Overlay thisOverlay = this;

        this.doneBtn = new TextButton("BACK", (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);

        this.doneBtn.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent());
            }
        });
        Table contentTable = new Table();
        Label title = new Label(titleText, (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        for (Map.Entry<String, Array<Monster>> entry : objects.entrySet()) {
            addMonsters(contentTable, (String)entry.getKey(), (Array)entry.getValue());
        }
        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);

        this.buttonOrder.add(this.doneBtn);

        return contentTable;
    }

    protected Table makeContentFromItems(String titleText, HashMap<String, Array<Item>> objects)
    {
        this.buttonOrder.clear();

        final Overlay thisOverlay = this;

        this.doneBtn = new TextButton("BACK", (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);

        this.doneBtn.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent());
            }
        });
        Table contentTable = new Table();
        Label title = new Label(titleText, (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        for (Map.Entry<String, Array<Item>> entry : objects.entrySet()) {
            addItems(contentTable, titleText, (String)entry.getKey(), (Array)entry.getValue());
        }
        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);

        this.buttonOrder.add(this.doneBtn);

        return contentTable;
    }

    protected Table makeContent(Entity[] objects)
    {
        this.buttonOrder.clear();

        final Overlay thisOverlay = this;

        this.doneBtn = new TextButton("BACK", (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);

        this.doneBtn.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent());
            }
        });
        Table contentTable = new Table();
        Label title = new Label("DEBUG!", (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        for (Entity entry : objects) {
            if ((entry instanceof Item)) {
                addItem(contentTable, ((Item)entry).GetName(), (Item)entry);
            } else if ((entry instanceof Monster)) {
                addItem(contentTable, ((Monster)entry).name, (Monster)entry);
            }
        }
        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);

        this.buttonOrder.add(this.doneBtn);

        return contentTable;
    }

    protected Table makeContentFromItems(String titleText, Array<Item> objects)
    {
        this.buttonOrder.clear();

        final Overlay thisOverlay = this;

        this.doneBtn = new TextButton("BACK", (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);

        this.doneBtn.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent());
            }
        });
        Table contentTable = new Table();
        Label title = new Label(titleText, (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        for (Item entry : objects) {
            addItem(contentTable, entry.GetName(), entry);
        }
        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);

        this.buttonOrder.add(this.doneBtn);

        return contentTable;
    }

    protected Table makeContent(String titleText, Array<Monster> objects)
    {
        this.buttonOrder.clear();

        final Overlay thisOverlay = this;

        this.doneBtn = new TextButton("BACK", (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);

        this.doneBtn.addListener(new ClickListener()
        {
            public void clicked(InputEvent event, float x, float y)
            {
                DebugOverlay.this.makeLayout(DebugOverlay.this.makeContent());
            }
        });
        Table contentTable = new Table();
        Label title = new Label(titleText, (Label.LabelStyle)this.skin.get(Label.LabelStyle.class));
        contentTable.add(title).colspan(2).padBottom(4.0F);
        contentTable.row();
        for (Monster entry : objects) {
            addItem(contentTable, entry.name, entry);
        }
        contentTable.add(this.doneBtn).padTop(4.0F).align(1).colspan(2);

        this.buttonOrder.add(this.doneBtn);

        return contentTable;
    }

    public void refreshData()
    {
        try
        {
            Art.KillCache();
            Game.instance.loadManagers();
            GameManager.renderer.initTextures();
            GameManager.renderer.initShaders();
            for (Entity e : Game.GetLevel().entities) {
                e.resetDrawable();
            }
            for (Entity e : Game.GetLevel().static_entities) {
                e.resetDrawable();
            }
            for (Entity e : Game.GetLevel().non_collidable_entities) {
                e.resetDrawable();
            }
            GameManager.renderer.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Game.GetLevel().isDirty = true;

            UiSkin.loadSkin();
        }
        catch (Exception ex)
        {
            Gdx.app.log("Delver", "Could not refresh: " + ex.getMessage());
        }
    }

    public void tick(float delta) {
        if (this.running) {
            this.timer += delta;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            OverlayManager.instance.remove(DebugOverlay.this);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            OverlayManager.instance.remove(DebugOverlay.this);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {

            // fix for instant close
            if (escapePressed) {
                OverlayManager.instance.remove(DebugOverlay.this);
            } else {
                escapePressed = true;
            }
        }
    }
}
