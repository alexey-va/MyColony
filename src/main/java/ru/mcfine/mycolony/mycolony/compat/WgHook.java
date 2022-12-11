package ru.mcfine.mycolony.mycolony.compat;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WgHook {

    public RegionContainer container;

    public WgHook(){
        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

}
