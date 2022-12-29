package ru.mcfine.mycolony.mycolony.config;

public class RegionGroup {

    private String displayName;
    private String id;

    public RegionGroup(String id, String displayName) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId(){
        return id;
    }
}
