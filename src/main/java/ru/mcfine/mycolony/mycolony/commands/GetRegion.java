package ru.mcfine.mycolony.mycolony.commands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.guis.ShopMenu;
import ru.mcfine.mycolony.mycolony.players.ColonyPlayer;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;

public class GetRegion implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player player){
            if(args.length == 0){
                ShopMenu shopMenu = new ShopMenu(player);
                shopMenu.show(player);
            }
            else if(args.length == 1){
                if(args[0].equalsIgnoreCase("list")){
                    ColonyPlayer colonyPlayer = RegionManager.colonyPlayers.get(player.getName());
                    if(colonyPlayer == null || colonyPlayer.getRegions() == null || colonyPlayer.getRegions().size() == 0) player.sendMessage("No regions!");
                    else{
                        for(Region region : colonyPlayer.getRegions()){
                            System.out.println(region);
                        }
                    }
                    return true;
                } else {
                    String regionName = args[0];
                    ItemStack chest = new ItemStack(Material.CHEST);
                    ItemMeta meta = chest.getItemMeta();
                    meta.setDisplayName(regionName);
                    meta.getPersistentDataContainer().set(new NamespacedKey(MyColony.plugin, "region_name"), PersistentDataType.STRING, regionName);
                    chest.setItemMeta(meta);

                    System.out.println(player.getInventory().firstEmpty());
                    player.getInventory().addItem(chest);
                    return true;
                }
            }
        }

        return false;
    }
}
