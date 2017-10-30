package com.interrupt.dungeoneer.editor.ui.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class Scene2dMenu extends Group {
    public Array<Actor> items = new Array();
    public MenuItem parentMenuItem = null;
    Table menuTable;
    Skin skin;
    private boolean dirty = true;

    public Scene2dMenu(Skin skin) {
        this.skin = skin;
        this.setZIndex(10);
    }

    public void addItem(MenuItem item) {
        this.dirty = true;
        item.skin = this.skin;
        this.items.add(item);
    }

    public void removeAll()
    {
        items.clear();
    }

    public void addSeparator() {
        this.dirty = true;
        Image image = new Image(this.skin, "menu-separator");
        this.items.add(image);
    }

    public void act(float delta) {
        if (this.dirty) {
            this.refreshDrawables();
            this.dirty = false;
        }

        super.act(delta);
        Actor parent = this.getParent();
        boolean hasParent = false;
        if (parent != null && parent instanceof Scene2dMenu) {
            Table theirTable = ((Scene2dMenu)parent).menuTable;
            if (theirTable != null) {
                if (parent instanceof Scene2dMenuBar && this.parentMenuItem != null) {
                    this.setX(this.parentMenuItem.getX() - 1.0F);
                } else {
                    this.setX(theirTable.getWidth() - 1.0F);
                }
            }

            hasParent = true;
        }

        if (!hasParent && this.menuTable != null && this.getY() + this.menuTable.getHeight() > this.getStage().getHeight()) {
            this.setY(this.getStage().getHeight() - this.menuTable.getHeight());
        }

    }

    public void setExpanded(MenuItem item) {
        Iterator var2 = this.items.iterator();

        while(var2.hasNext()) {
            Actor a = (Actor)var2.next();
            if (a instanceof MenuItem) {
                MenuItem i = (MenuItem)a;
                if (item == i) {
                    if (i.subMenu != null) {
                        i.subMenu.open();
                    }

                    i.updateStyle(true);
                } else {
                    if (i.subMenu != null) {
                        i.subMenu.close();
                    }

                    i.updateStyle(false);
                }
            }
        }

    }

    public void close() {
        this.setVisible(false);
        Iterator var1 = this.items.iterator();

        while(var1.hasNext()) {
            Actor a = (Actor)var1.next();
            if (a instanceof MenuItem) {
                MenuItem i = (MenuItem)a;
                i.updateStyle(false);
                if (i.subMenu != null) {
                    i.subMenu.close();
                }
            }
        }

    }

    public void open() {
        this.setVisible(true);
    }

    public void pack() {
        if (this.menuTable != null) {
            this.menuTable.pack();
        }

    }

    protected void refreshDrawables() {
        this.clearChildren();
        this.menuTable = new Table();
        this.menuTable.setZIndex(10);
        this.menuTable.setOrigin(0.0F, 50.0F);
        this.addActor(this.menuTable);
        Iterator var1 = this.items.iterator();

        Actor a;
        while(var1.hasNext()) {
            a = (Actor)var1.next();
            this.menuTable.row();
            this.menuTable.add(a).align(8).fill();
        }

        this.menuTable.pack();
        var1 = this.items.iterator();

        while(var1.hasNext()) {
            a = (Actor)var1.next();
            if (a instanceof MenuItem) {
                MenuItem i = (MenuItem)a;
                i.setParentMenu(this);
                if (i.subMenu != null) {
                    this.addActor(i.subMenu);
                }
            }
        }

    }
}
