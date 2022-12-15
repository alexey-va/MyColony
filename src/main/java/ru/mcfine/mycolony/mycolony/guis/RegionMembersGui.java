package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.tasks.TickerRunnable;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegionMembersGui extends ChestGui {


    public RegionMembersGui(Region region, Player p, Gui parent, boolean cityMembers) {
        super(6, "dummy");
        int rows = Math.min(6, (int)Math.ceil(region.getPlayerNames().size() / 7.0)+2);
        this.setRows(rows);

        if(!cityMembers){
            this.setTitle(Lang.getString("menu.region-members.title", p));
        } else{
            this.setTitle(Lang.getString("menu.city-members.title", p));
        }

        OutlinePane background = new OutlinePane(0, 0, 9, rows, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(Utils.getBackground(), event -> {
            event.setCancelled(true);
        }));
        background.setRepeat(true);
        this.addPane(background);

        OutlinePane background2 = new OutlinePane(1, 1, 7, rows-2, Pane.Priority.LOW);
        background2.addItem(new GuiItem(Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE), event -> {
            event.setCancelled(true);
        }));
        background2.setRepeat(true);
        this.addPane(background2);


        PaginatedPane paginatedPane = new PaginatedPane(1,1,7,rows-2);
        List<GuiItem> playerItems = new ArrayList<>();
        if(cityMembers){
            for(String s : region.getCityRegion().getCityMembers()){
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
                PlayerItem playerItem = new PlayerItem(offlinePlayer, region, p, this);
                playerItems.add(playerItem);
            }
        } else {
            for (String s : region.getPlayerNames()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
                PlayerItem playerItem = new PlayerItem(offlinePlayer, region, p, this);
                playerItems.add(playerItem);
            }
        }
        paginatedPane.populateWithGuiItems(playerItems);
        this.addPane(paginatedPane);

        StaticPane nav = new StaticPane(0,rows-1,9,1);

        GuiItem prev = new GuiItem(Utils.getPrevPage(Material.ARROW, Lang.getString("menu.go-back-button-display", p)));
        prev.setAction(inventoryClickEvent -> {
            if(paginatedPane.getPage() == 0){
                if(parent instanceof RegionGui) {
                    RegionGui regionGui = new RegionGui(region.getLocation().getBlock(), region, p);
                    regionGui.show(inventoryClickEvent.getWhoClicked());
                } else parent.show(inventoryClickEvent.getWhoClicked());
            } else if(paginatedPane.getPage()>0){
                paginatedPane.setPage(paginatedPane.getPage()-1);
                if(paginatedPane.getPage() == 0){
                    ItemMeta meta = prev.getItem().getItemMeta();
                    meta.setDisplayName(Lang.getString("menu.go-back-button-display", p));
                    meta.setDisplayName(Lang.getString("menu.go-back-button-lore", p));
                    prev.getItem().setItemMeta(meta);
                }
                this.update();
            }
            inventoryClickEvent.setCancelled(true);
        });
        nav.addItem(prev,0,0);


        if(paginatedPane.getPages()>1) {
            GuiItem next = new GuiItem(Utils.getNextPage(Material.ARROW), inventoryClickEvent -> {
                if (paginatedPane.getPage() < paginatedPane.getPages() - 1) {
                    paginatedPane.setPage(paginatedPane.getPage() + 1);
                } else if (paginatedPane.getPage() == paginatedPane.getPages() - 1) {
                    paginatedPane.setPage(0);
                }
                if(paginatedPane.getPage() == 0){
                    ItemMeta meta = prev.getItem().getItemMeta();
                    meta.setDisplayName(Lang.getString("menu.go-back-button-display", p));
                    meta.setDisplayName(Lang.getString("menu.go-back-button-lore", p));
                    prev.getItem().setItemMeta(meta);
                }
                this.update();
                inventoryClickEvent.setCancelled(true);
            });
            nav.addItem(next, 8, 0);
        }

        ItemStack addPlayer = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) addPlayer.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures playerTextures = profile.getTextures();
        try {
            playerTextures.setSkin(new URL("http://textures.minecraft.net/texture/9aca891a7015cbba06e61c600861069fa7870dcf9b35b4fe15850f4b25b3ce0"));
        } catch (Exception exception){exception.printStackTrace();}
        profile.setTextures(playerTextures);
        skullMeta.setOwnerProfile(profile);
        skullMeta.setDisplayName(Lang.getString("menu.members.add-display", p));
        skullMeta.setLore(Lang.getStringList("menu.members.add-lore", null, p));
        addPlayer.setItemMeta(skullMeta);
        GuiItem addPlayerItem = new GuiItem(addPlayer);
        addPlayerItem.setAction(inventoryClickEvent -> {
           inventoryClickEvent.setCancelled(true);
           if(cityMembers && !region.hasPlayer(inventoryClickEvent.getWhoClicked().getName())){
               ItemMeta meta = addPlayerItem.getItem().getItemMeta();
               ItemMeta oldMeta = meta.clone();
               meta.setDisplayName(Lang.getString("menu.members.cant-add-to-city", p));
               meta.setLore(Lang.getStringList("menu.members.cant-add-to-city-lore", null, p));
               addPlayerItem.getItem().setItemMeta(meta);
               new BukkitRunnable() {
                   @Override
                   public void run() {
                       addPlayerItem.getItem().setItemMeta(oldMeta);
                   }
               }.runTaskLater(MyColony.plugin, 40L);
               return;
           }
            if((!cityMembers && !region.hasPlayer(inventoryClickEvent.getWhoClicked().getName())) && !(inventoryClickEvent.getWhoClicked().getName().equals(region.getCityRegion().getOwnerName()) && region.getCityRegion().getRegionType().isAbsolutePower())){
                ItemMeta meta = addPlayerItem.getItem().getItemMeta();
                ItemMeta oldMeta = meta.clone();
                meta.setDisplayName(Lang.getString("menu.members.cant-add-to-city", p));
                meta.setLore(Lang.getStringList("menu.members.cant-add-to-city-lore", null, p));
                addPlayerItem.getItem().setItemMeta(meta);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        addPlayerItem.getItem().setItemMeta(oldMeta);
                    }
                }.runTaskLater(MyColony.plugin, 40L);
                return;
            }
            AnvilGui anvilGui = new AnvilGui(Lang.getString("menu.members.anvil-title-add-player", p));
            anvilGui.setOnTopDrag(inventoryClickEvent1 -> inventoryClickEvent1.setCancelled(true));
            anvilGui.setOnBottomDrag(inventoryClickEvent1 -> inventoryClickEvent1.setCancelled(true));
            anvilGui.setOnClose(inventoryCloseEvent -> {
                RegionMembersGui regionMembersGui = new RegionMembersGui(region, p, parent, cityMembers);
                regionMembersGui.show(inventoryClickEvent.getWhoClicked());
            });
            OutlinePane anvilPane = new OutlinePane(0,0,3,1);
            ItemStack anvilStack = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta anvilMeta = anvilStack.getItemMeta();
            anvilMeta.setDisplayName(Lang.getString("menu.members.anvil-item-display", null, p));
            anvilMeta.setLore(Lang.getStringList("menu.members.anvil-item-lore", null, p));
            anvilStack.setItemMeta(anvilMeta);
            GuiItem anvilItem = new GuiItem(anvilStack, event -> {
                event.setCancelled(true);
                String s = anvilGui.getRenameText();
                String name = "";
                OfflinePlayer offlinePlayer;
                try{
                    name = s;
                    offlinePlayer = Bukkit.getOfflinePlayer(name);
                } catch (Exception ex){
                    anvilMeta.setDisplayName(Lang.getString("menu.members.wrong-name", p));
                    anvilStack.setItemMeta(anvilMeta);
                    anvilGui.update();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            anvilMeta.setDisplayName(Lang.getString("menu.members.anvil-item-display", null, p));
                            anvilStack.setItemMeta(anvilMeta);
                            anvilGui.update();
                        }
                    }.runTaskLater(MyColony.plugin, 40L);
                    return;
                }
                if(cityMembers){
                    region.getCityRegion().addCityMember(offlinePlayer.getName());
                } else{
                    if(!region.getCityRegion().hasPlayer(offlinePlayer.getName())){
                        if(region.getCityRegion().hasPlayer(inventoryClickEvent.getWhoClicked().getName())){
                            region.addMember(offlinePlayer.getName(), offlinePlayer.getUniqueId().toString());
                            region.getCityRegion().addMember(offlinePlayer.getName(), offlinePlayer.getUniqueId().toString());
                        } else{
                            ItemMeta oldAnvilMeta= anvilMeta.clone();
                            anvilMeta.setDisplayName(Lang.getString("menu.members.cant-add-to-city", p));
                            anvilMeta.setLore(Lang.getStringList("menu.members.cant-add-to-city-lore", null, p));
                            anvilStack.setItemMeta(anvilMeta);
                            anvilGui.update();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    anvilStack.setItemMeta(oldAnvilMeta);
                                    anvilGui.update();
                                }
                            }.runTaskLater(MyColony.plugin, 40L);
                            return;
                        }
                    } else{
                        region.addMember(offlinePlayer.getName(), offlinePlayer.getUniqueId().toString());
                    }
                    RegionMembersGui regionMembersGui = new RegionMembersGui(region, p, parent, cityMembers);
                    regionMembersGui.show(inventoryClickEvent.getWhoClicked());
                }
            });
            anvilPane.addItem(anvilItem);
            anvilPane.setRepeat(true);
            anvilGui.getFirstItemComponent().addPane(anvilPane);
            anvilGui.getResultComponent().addPane(anvilPane);
            anvilGui.show(inventoryClickEvent.getWhoClicked());
        });
        nav.addItem(addPlayerItem, 4, 0);

        //skullMeta.set

        this.addPane(nav);
    }
}
