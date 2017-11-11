package net.cotd.delverunlimited.helper.history;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class History {

    // could not use File cause a missing no-arg constructor :/
    public String levelName;
    public String levelAbsolutePath;
    public boolean error = false;

    public History() { } // must have no-arg constructor for serializer

    public History(String levelName, String levelAbsolutePath) {
        this.levelName = levelName;
        this.levelAbsolutePath = levelAbsolutePath;
    }

    public boolean verify() {
        /* Is it empty? */
        if (levelName.isEmpty() || levelAbsolutePath.isEmpty()) {
            return false;
        }

        /* Is there an error reported by HistoryHelper? */
        if (error) {
            return false;
        }

        /* Does file exist? */
        FileHandle file = Gdx.files.absolute(levelAbsolutePath);
        if (!file.exists()) {
            return false;
        }

        return true;
    }

    // We want to output the map name
    @Override
    public String toString() {
        return levelName;
    }
}
