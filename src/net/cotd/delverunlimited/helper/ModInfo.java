package net.cotd.delverunlimited.helper;

public class ModInfo {

    public String name;
    public String author;
    public String description;
    public String version;
    public String url;

    public ModInfo() {
        // must have empty constructor for the Serializer!!!
    } // must have empty constructor for the serializer

    public ModInfo(String name, String author, String description, String version, String url) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.version = version;
        this.url = url;
    }
}
