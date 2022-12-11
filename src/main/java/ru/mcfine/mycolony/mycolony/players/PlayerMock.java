package ru.mcfine.mycolony.mycolony.players;

import ru.mcfine.mycolony.mycolony.regions.Region;

import java.util.ArrayList;
import java.util.List;

public class PlayerMock {

    public String playerName;
    public String uuid;
    public List<String> regions;

    public PlayerMock(ColonyPlayer colonyPlayer){
        this.playerName = colonyPlayer.getPlayerName();
        this.uuid = colonyPlayer.getUuid();
        this.regions = new ArrayList<>();
        for(Region region : colonyPlayer.getRegions()){
            this.regions.add(region.getUuid());
        }
    }

}
