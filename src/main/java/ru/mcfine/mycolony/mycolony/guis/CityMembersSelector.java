package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.util.Utils;

public class CityMembersSelector extends ChestGui {
    public CityMembersSelector(Region region, Player player) {
        super(3, Lang.getString("menu.members.city-selector.title", player));


        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(Utils.getBackground(), event -> event.setCancelled(true)));
        background.setRepeat(true);
        this.addPane(background);

        //OutlinePane background2 = new OutlinePane(1, 1, 7, 1, Pane.Priority.LOW);
        //background2.addItem(new GuiItem(Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE), event -> {
        //    event.setCancelled(true);
        //}));
        //background2.setRepeat(true);
        //this.addPane(background2);


        StaticPane staticPane = new StaticPane(0, 1, 9, 2);

        ItemStack regionMembersStack = new ItemStack(Material.COAL_BLOCK);
        ItemMeta meta1 = regionMembersStack.getItemMeta();
        meta1.setDisplayName(Lang.getString("menu.members.city-selector.region-members-title", player));
        meta1.setLore(Lang.getStringList("menu.members.city-selector.region-members-lore", null, player));
        regionMembersStack.setItemMeta(meta1);
        GuiItem regionMembers = new GuiItem(regionMembersStack, inventoryClickEvent -> {
           inventoryClickEvent.setCancelled(true);
           RegionMembersGui regionMembersGui = new RegionMembersGui(region, player, this, false);
           regionMembersGui.show(inventoryClickEvent.getWhoClicked());
        });
        staticPane.addItem(regionMembers, 2, 0);

        ItemStack cityMembersStack = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta meta2 = cityMembersStack.getItemMeta();
        meta2.setDisplayName(Lang.getString("menu.members.city-selector.city-members-title", player));
        meta2.setLore(Lang.getStringList("menu.members.city-selector.city-members-lore", null, player));
        cityMembersStack.setItemMeta(meta2);
        GuiItem cityMembers = new GuiItem(cityMembersStack, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            RegionMembersGui regionMembersGui = new RegionMembersGui(region, player, this, true);
            regionMembersGui.show(inventoryClickEvent.getWhoClicked());
        });
        staticPane.addItem(cityMembers, 6, 0);

        GuiItem prevPage = new GuiItem(Utils.getPrevPage(Material.ARROW), inventoryClickEvent -> {
            RegionGui regionGui = new RegionGui(region.getLocation().getBlock(), region, player);
            inventoryClickEvent.setCancelled(true);
            regionGui.show(inventoryClickEvent.getWhoClicked());
        });
        staticPane.addItem(prevPage, 0, 1);
        this.addPane(staticPane);

    }
}
