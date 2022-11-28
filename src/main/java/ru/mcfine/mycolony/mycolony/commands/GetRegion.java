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

public class GetRegion implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player player){
            if(args.length == 0)return true;
            else if(args.length == 1){
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

        return false;
    }
}
