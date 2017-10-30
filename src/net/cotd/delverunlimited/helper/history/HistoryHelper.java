package net.cotd.delverunlimited.helper.history;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

import java.io.File;
import java.io.FileReader;

public class HistoryHelper {

    public static String HISTORY_FILE_PATH = "save" + File.separator + "history.json";

    private static File historyFile = new File(HISTORY_FILE_PATH);
    private static Json gson = new Json();

    private void HistoryHelper() {}

    public static void addHistory(History history)
    {
        isDuplicate(history.levelAbsolutePath);
    }

    @Deprecated
    public static void addHistory(File file)
    {

    }

    @Deprecated
    public static void addHistory(String levelName, String levelPath)
    {

    }

    private static boolean isDuplicate(String levelAbsolutePath)
    {
        Gdx.app.log("HistoryHelper", levelAbsolutePath);

        /* Does the file exist? */
        if (!historyFile.exists()) {
            return false;
        }

        /* Check against the file */

        return false;
    }

    private static void add()
    {

    }

    /** Parses the json and returns its object in Java code
     *
     * @return the history file parsed from json
     * @throws NullPointerException when the file doesn't exist, and / or if the file is empty
     */
    public static History[] getObjectJson() throws NullPointerException
    {

        /* Does the file exist? */
        if (!historyFile.exists()) throw new NullPointerException();

        /* Parse json */
        History[] object = null;
        try {
            object = gson.fromJson(History[].class, new FileReader(HISTORY_FILE_PATH));
        } catch (Exception ex) {
            Gdx.app.error("HistoryHelper", ex.getMessage());
        }

        if (object == null) throw new NullPointerException();

        return object;
    }
}
