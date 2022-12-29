package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.regions.Region;

import java.util.HashMap;
import java.util.Map;

public class PlayerItem extends GuiItem {
    Gui parentGui;
    Player p;
    OfflinePlayer offlinePlayer;
    public PlayerItem(OfflinePlayer offlinePlayer, Region region, Player p, Gui parentGui) {
        super(new ItemStack(Material.PLAYER_HEAD), event -> event.setCancelled(true));
        this.offlinePlayer=offlinePlayer;
        this.parentGui=parentGui;
        this.p=p;

        Map<String, String> rep = new HashMap<>();
        rep.put("%name%", offlinePlayer.getName());

        SkullMeta meta = (SkullMeta) this.getItem().getItemMeta();
        meta.setOwningPlayer(offlinePlayer);
        meta.setDisplayName(Lang.getString("menu.region-members.head-display", rep, p));
        meta.setLore(Lang.getStringList("menu.region-members.head-lore", rep, p));
        this.getItem().setItemMeta(meta);
        this.setAction(inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            PlayerActionGui playerActionGui = new PlayerActionGui(offlinePlayer, p, parentGui);
            playerActionGui.show(inventoryClickEvent.getWhoClicked());
        });

    }
}
