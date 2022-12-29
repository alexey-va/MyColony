package ru.mcfine.mycolony.mycolony.compat;

import me.angeschossen.lands.api.flags.types.RoleFlag;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.mcfine.mycolony.mycolony.MyColony;

public class LandsAPIHook {

    private final LandsIntegration landsIntegration;
    private final RoleFlag createTown;

    public LandsAPIHook(){
        this.landsIntegration = new LandsIntegration(MyColony.plugin);
        this.createTown = new RoleFlag(MyColony.plugin, RoleFlag.Category.ACTION, "create_town", true, false);
        createTown.setDisplayName("Create Town");
        createTown.setIcon(new ItemStack(Material.CHEST));
        createTown.setDisplay(true);
        landsIntegration.registerFlag(createTown);
    }

    public boolean hasCreateFlag(Player player, Location location){
        if(getArea(location) != null) return getArea(location).hasFlag(player.getUniqueId(), createTown);
        return false;
    }

    public Area getArea(Location location){
        return landsIntegration.getAreaByLoc(location);
    }

    public Land getLand(Location location){
        return landsIntegration.getLandUnloaded(location.getWorld(), location.getChunk().getX(), location.getChunk().getZ());
    }

}
