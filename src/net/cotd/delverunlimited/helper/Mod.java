package net.cotd.delverunlimited.helper;

public class Mod {

    public String name;
    public String author;
    public String description;
    public String version;
    public String url;
    public ModState modState;
    public String modPath;

    public Mod() {
        // must have empty constructor for the Serializer!!!
    }

    public Mod(String name, String author, String description, String version, String url) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.version = version;
        this.url = url;
        this.modState = ModState.Enabled;
    }

    public Mod(String name, String author, String description, String version, String url, ModState modState, String modPath) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.version = version;
        this.url = url;
        this.modState = modState;
        this.modPath = modPath;
    }

    public static enum ModState {
        Enabled,
        Disabled;

        ModState() {
        }
    }
}
