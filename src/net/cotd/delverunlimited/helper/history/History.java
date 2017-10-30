package net.cotd.delverunlimited.helper.history;

public class History {

    // could not use File cause a missing no-arg constructor :/
    public String levelName;
    public String levelAbsolutePath;

    public History() { } // must have no-arg constructor for serializer

    public History(String levelName, String levelAbsolutePath) {
        this.levelName = levelName;
        this.levelAbsolutePath = levelAbsolutePath;
    }

    // We want to output the map name
    @Override
    public String toString() {
        return levelName;
    }
}
