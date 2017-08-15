package net.cotd.delverunlimited.helper;

public class ModSettings {

    public ModState modState;
    public String modPath;

    public ModSettings() { } // must have empty constructor for the serializer

    public ModSettings(ModState modState, String modPath) {
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
