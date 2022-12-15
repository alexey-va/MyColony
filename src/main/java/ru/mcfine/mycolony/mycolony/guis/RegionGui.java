package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import ru.mcfine.mycolony.mycolony.city.CityRegion;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.tasks.TickerRunnable;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class RegionGui extends ChestGui{

    public StaticPane mainPane;
    public GuiItem clockItem;
    public GuiItem paperItem;
    public GuiItem bankItem;
    public Player p;
    public RegionGui(Block block, Region region, Player p) {
        super(5, Lang.translate(region.getRegionType().getDisplayName()));
        TickerRunnable.mainMenuGuis.put(this, region);
        this.p = p;

        String selectedChain = region.getSelectedChain();
        if(selectedChain == null) selectedChain="&7None selected";
        Map<String, String> rep = new HashMap<>();
        rep.put("%time%", (Math.ceil(region.getMaxTime() - region.getTimeElapsed()))+"");
        rep.put("%level%", region.getLevel()+"");
        rep.put("%produced_group%", Lang.translate(selectedChain));
        rep.put("%balance%", region.getBankDeposit()+"");
        boolean isInCity = region.getCityRegion()!=null;
        if(isInCity){
            rep.put("%is_in_city%", Lang.getString("common.word-yes", p));
            rep.put("%owner_name%", region.getCityRegion().getOwnerName());
        }
        else{
            rep.put("%is_in_city%", Lang.getString("common.word-no", p));
            rep.put("%owner_name%", Lang.getString("common.word-none", p));
        }
        if(region instanceof CityRegion cityRegion){
            rep.put("%population%", cityRegion.getCityPopulation()+"");
            rep.put("%chunk_amount%", cityRegion.getCityArea().getChunks().size()+"");
        }

        OutlinePane background = new OutlinePane(0, 0, 9, 5, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(Utils.getBackground(),event -> {
            event.setCancelled(true);
        }));
        background.setRepeat(true);
        this.addPane(background);

        OutlinePane background2 = new OutlinePane(1, 1, 7, 3, Pane.Priority.LOW);
        background2.addItem(new GuiItem(Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE),event -> {
            event.setCancelled(true);
        }));
        background2.setRepeat(true);
        this.addPane(background2);

        this.setOnClose(event -> {
            TickerRunnable.mainMenuGuis.remove(this);
        });

        mainPane = new StaticPane(1,1,7,3);

        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();
        meta.setDisplayName(Lang.getString("menu.chest-inventory-display", rep, p));
        meta.setLore(Lang.getStringList("menu.chest-inventory-lore", rep, p));
        chest.setItemMeta(meta);
        GuiItem chestItem = new GuiItem(chest, event ->{
            Chest chestState = (Chest)block.getState();
            chestState.setCustomName(Lang.translate(region.getRegionName()));
            event.getWhoClicked().openInventory(chestState.getInventory());
            event.setCancelled(true);
        });
        mainPane.addItem(chestItem, 0, 0);

        ItemStack timer = new ItemStack(Material.CLOCK);
        ItemMeta meta1 = timer.getItemMeta();
        meta1.setDisplayName(Lang.getString("menu.time-to-next-display", rep, p));
        meta1.setLore(Lang.getStringList("menu.time-to-next-lore", rep, p));
        timer.setItemMeta(meta1);
        clockItem = new GuiItem(timer, event ->{
            event.setCancelled(true);
        });
        mainPane.addItem(clockItem,2 ,0);

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta metaPaper = paper.getItemMeta();


        metaPaper.setDisplayName(Lang.getString("menu.info-display", rep, p));
        if(region.getRegionType().isCity()){
            metaPaper.setLore(Lang.getStringList("menu.city-info-lore", rep, p));
        } else metaPaper.setLore(Lang.getStringList("menu.info-lore", rep, p));
        paper.setItemMeta(metaPaper);
        paperItem = new GuiItem(paper, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
        });
        mainPane.addItem(paperItem, 4, 0);

        ItemStack bank = new ItemStack(Material.GOLD_INGOT);
        ItemMeta bankMeta = bank.getItemMeta();
        bankMeta.setDisplayName(Lang.getString("menu.bank-display", rep, p));
        bankMeta.setLore(Lang.getStringList("menu.bank-lore", rep, p));
        bank.setItemMeta(bankMeta);
        bankItem = new GuiItem(bank, inventoryClickEvent -> {
            BankGui bankGui = new BankGui(region, this, p);
            bankGui.show(inventoryClickEvent.getWhoClicked());
            inventoryClickEvent.setCancelled(true);
        });
        mainPane.addItem(bankItem, 6, 0);

        ItemStack members = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) members.getItemMeta();
        skullMeta.setOwningPlayer(p);
        skullMeta.setDisplayName(Lang.getString("menu.members-display", rep, p));
        skullMeta.setLore(Lang.getStringList("menu.members-lore", rep, p));
        members.setItemMeta(skullMeta);
        GuiItem membersItem = new GuiItem(members, inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            if(region.getRegionType().isCity()){
                CityMembersSelector selector = new CityMembersSelector(region, p);
                selector.show(inventoryClickEvent.getWhoClicked());
            }else {
                RegionMembersGui regionMembersGui = new RegionMembersGui(region, p, this, false);
                regionMembersGui.show(inventoryClickEvent.getWhoClicked());
            }
        });
        mainPane.addItem(membersItem,0, 2);


        this.addPane(mainPane);
    }

}
