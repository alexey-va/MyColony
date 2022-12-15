package ru.mcfine.mycolony.mycolony.players;

import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.regions.Region;

import java.util.ArrayList;
import java.util.List;

public class ColonyPlayer {

    private String playerName;
    private String uuid;
    private List<Region> regions = new ArrayList<>();

    public ColonyPlayer(String playerName, String uuid, List<Region> regions) {
        this.playerName = playerName;
        this.uuid = uuid;
        if(regions != null) this.regions = regions;
    }

    public ColonyPlayer(PlayerMock p){
        this.playerName = p.playerName;
        this.uuid = p.uuid;
        for(Region r : MyColony.regionManager.getAllRegions()){
            if(p.regions.contains(r.getUuid())) this.regions.add(r);
        }
    }




    public String getPlayerName() {
        return playerName;
    }

    public String getUuid() {
        return uuid;
    }

    public List<Region> getRegions() {
        return regions;
    }
}
