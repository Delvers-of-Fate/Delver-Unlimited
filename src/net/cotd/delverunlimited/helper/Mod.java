package net.cotd.delverunlimited.helper;

public class Mod {

    public String name;
    public String author;
    public String description;
    public String version;
    public String url;
    public ModSettings.ModState modState;
    public String modPath;

    public Mod() { } // must have no-arg constructor for serializer

    public Mod(String name, String author, String description, String version, String url, ModSettings.ModState modState, String modPath) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.version = version;
        this.url = url;
        this.modState = modState;
        this.modPath = modPath;
    }

    // We want to output the mod name by default if it's in an array
    @Override
    public String toString() {
        return name;
    }

}