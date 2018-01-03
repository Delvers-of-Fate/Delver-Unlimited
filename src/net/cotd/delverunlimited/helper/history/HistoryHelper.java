package net.cotd.delverunlimited.helper.history;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.interrupt.dungeoneer.game.Game;

import java.io.File;
import java.io.FileReader;

public class HistoryHelper {

    public static String HISTORY_FILE_PATH = "save" + File.separator + "history.json";

    private static File historyFile = new File(HISTORY_FILE_PATH);
    private static Json json = new Json();

    private void HistoryHelper() {}

    public static void addHistory(History history)
    {
        if(!isDuplicate(history.levelAbsolutePath)) {
            add(history);
        }
    }

    public static void addHistory(File file)
    {
        if (!isDuplicate(file.getAbsolutePath())) {
            add(new History(file.getName(), file.getAbsolutePath()));
        }
    }

    private static boolean isDuplicate(String levelAbsolutePath)
    {
        History object[] = getObjectJson();

        /* Is the file empty? */
        if (object == null) return false;

        /* Check against json file */
        for (History history : object) {
            if (history.levelAbsolutePath.equals(levelAbsolutePath)) {
                return true; // it wont add to the history
            }
        }

        return false;
    }

    private static void add(History history)
    {
        Array<History> historyArray = new Array<>(); // LibGDX array (supports resizing array etc)
        historyArray.add(history); // add the new history

        History object[] = getObjectJson();

        /* Add the existing history */
        if (object != null) {
            for (History history1 : object) {
                historyArray.add(history1);
            }
        }

        /* Write to json */
        try {
            Game.toJson(historyArray, new FileHandle(HISTORY_FILE_PATH));
        } catch (Exception ex) {
            Gdx.app.error("HistoryHelper", ex.getMessage());
        }

       // EditorUi.updateHistory(false);

    }

    /** Parses the json and returns its object in Java code
     *
     * @return the history file parsed from json
     */
    public static History[] getObjectJson()
    {

        /* Does the file exist? */
        if (!historyFile.exists()) return null;

        /* Parse json */
        History[] object = null;
        try {
            object = json.fromJson(History[].class, new FileReader(HISTORY_FILE_PATH));
        } catch (Exception ex) {
            Gdx.app.error("HistoryHelper", ex.getMessage());
        }

        /* Is it empty? */
        if (object == null) return null;

        /* Verify the paths */
        for (History history : object) {
            FileHandle file = Gdx.files.absolute(history.levelAbsolutePath);

            if (!file.exists()) {
                history.error = true;
            }
        }

        return object;
    }
}
