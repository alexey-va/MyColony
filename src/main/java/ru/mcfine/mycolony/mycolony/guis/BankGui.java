package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.regions.Region;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class BankGui extends ChestGui {

    private final BankGui thisGui;
    private final Gui parentGui;
    private GuiItem info;

    public BankGui(Region region, Gui parentGui, Player p) {
        super(3, Lang.getString("menu.bank.title", p));

        this.parentGui = parentGui;
        thisGui=this;
        String selectedChain = region.getSelectedChain();
        if(selectedChain == null) selectedChain="&7None selected";
        Map<String, String> rep = new HashMap<>();
        rep.put("%time%", (Math.ceil(region.getMaxTime() - region.getTimeElapsed()))+"");
        rep.put("%level%", region.getLevel()+"");
        rep.put("%produced_group%", Lang.translate(selectedChain));
        rep.put("%balance%", region.getBankDeposit()+"");

        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(Utils.getBackground(), event -> event.setCancelled(true)));
        background.setRepeat(true);
        this.addPane(background);

        OutlinePane background2 = new OutlinePane(1, 1, 7, 1, Pane.Priority.LOW);
        background2.addItem(new GuiItem(Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE), event -> event.setCancelled(true)));
        background2.setRepeat(true);
        this.addPane(background2);

        StaticPane staticPane = new StaticPane(0,0,9,3, Pane.Priority.NORMAL);

        ItemStack addMoney = new ItemStack(Material.GREEN_WOOL);
        ItemMeta addMeta = addMoney.getItemMeta();
        addMeta.setDisplayName(Lang.getString("menu.bank.add-display", rep, p));
        addMeta.setLore(Lang.getStringList("menu.bank.add-lore", rep, p));
        addMoney.setItemMeta(addMeta);
        GuiItem addItem = new GuiItem(addMoney, inventoryClickEvent -> {
            AnvilGui anvilGui = new AnvilGui(Lang.getString("menu.bank.anvil-title-deposit", p));
            anvilGui.setOnTopDrag(inventoryClickEvent1 -> inventoryClickEvent1.setCancelled(true));
            anvilGui.setOnBottomDrag(inventoryClickEvent1 -> inventoryClickEvent1.setCancelled(true));
            anvilGui.setOnClose(inventoryCloseEvent -> thisGui.show(inventoryCloseEvent.getPlayer()));
            OutlinePane anvilPane = new OutlinePane(0,0,3,1);
            ItemStack anvilStack = new ItemStack(Material.GOLD_INGOT);
            ItemMeta anvilMeta = anvilStack.getItemMeta();
            anvilMeta.setDisplayName(Lang.getString("menu.bank.anvil-item-display-deposit", rep, p));
            anvilMeta.setLore(Lang.getStringList("menu.bank.anvil-item-lore-deposit", rep, p));
            anvilStack.setItemMeta(anvilMeta);
            GuiItem anvilItem = new GuiItem(anvilStack, event -> {
                event.setCancelled(true);
                String s = anvilGui.getRenameText();
                double amount = 0;
                try{
                    amount = Double.parseDouble(s);
                } catch (Exception ex){
                    anvilMeta.setDisplayName(Lang.getString("menu.bank.wrong-number", p));
                    anvilStack.setItemMeta(anvilMeta);
                    anvilGui.update();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            anvilMeta.setDisplayName(Lang.getString("menu.bank.anvil-item-display-deposit", rep, p));
                            anvilStack.setItemMeta(anvilMeta);
                            anvilGui.update();
                        }
                    }.runTaskLater(MyColony.plugin, 40L);
                    return;
                }
                EconomyResponse response = MyColony.econ.withdrawPlayer(Bukkit.getOfflinePlayer(event.getWhoClicked().getName()), amount);
                if(response.transactionSuccess()) {
                    region.setBankDeposit(region.getBankDeposit()+amount);
                    rep.put("%balance%", region.getBankDeposit()+"");
                    ItemMeta meta = info.getItem().getItemMeta();
                    meta.setDisplayName(Lang.getString("menu.bank.bank-info-display", rep, p));
                    meta.setLore(Lang.getStringList("menu.bank.bank-info-lore", rep, p));
                    info.getItem().setItemMeta(meta);
                    thisGui.update();
                    thisGui.show(event.getWhoClicked());
                } else{
                    anvilMeta.setDisplayName(Lang.getString("menu.bank.not-enough-money", p));
                    anvilStack.setItemMeta(anvilMeta);
                    anvilGui.update();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            anvilMeta.setDisplayName(Lang.getString("menu.bank.anvil-item-display-deposit", rep, p));
                            anvilStack.setItemMeta(anvilMeta);
                            anvilGui.update();
                        }
                    }.runTaskLater(MyColony.plugin, 40L);
                }
            });
            anvilPane.addItem(anvilItem);
            anvilPane.setRepeat(true);
            anvilGui.getFirstItemComponent().addPane(anvilPane);
            anvilGui.getResultComponent().addPane(anvilPane);
            inventoryClickEvent.setCancelled(true);
            anvilGui.show(inventoryClickEvent.getWhoClicked());
        });
        staticPane.addItem(addItem, 2,1);

        ItemStack takeMoney = new ItemStack(Material.RED_WOOL);
        ItemMeta takeMeta = takeMoney.getItemMeta();
        takeMeta.setDisplayName(Lang.getString("menu.bank.take-display", rep, p));
        takeMeta.setLore(Lang.getStringList("menu.bank.take-lore", rep, p));
        takeMoney.setItemMeta(takeMeta);
        GuiItem takeItem = new GuiItem(takeMoney, inventoryClickEvent -> {
            AnvilGui anvilGui = new AnvilGui(Lang.getString("menu.bank.anvil-title-withdraw", p));
            anvilGui.setOnTopDrag(inventoryClickEvent1 -> inventoryClickEvent1.setCancelled(true));
            anvilGui.setOnBottomDrag(inventoryClickEvent1 -> inventoryClickEvent1.setCancelled(true));
            anvilGui.setOnClose(inventoryCloseEvent -> thisGui.show(inventoryCloseEvent.getPlayer()));
            OutlinePane anvilPane = new OutlinePane(0,0,3,1);
            ItemStack anvilStack = new ItemStack(Material.GOLD_INGOT);
            ItemMeta anvilMeta = anvilStack.getItemMeta();
            anvilMeta.setDisplayName(Lang.getString("menu.bank.anvil-item-display-withdraw", rep, p));
            anvilMeta.setLore(Lang.getStringList("menu.bank.anvil-item-lore-withdraw", rep, p));
            anvilStack.setItemMeta(anvilMeta);
            GuiItem anvilItem = new GuiItem(anvilStack, event -> {
                event.setCancelled(true);
                String s = anvilGui.getRenameText();
                double amount = 0;
                try{
                    amount = Double.parseDouble(s);
                } catch (Exception ex){
                    anvilMeta.setDisplayName(Lang.getString("menu.bank.wrong-number", p));
                    anvilStack.setItemMeta(anvilMeta);
                    anvilGui.update();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            anvilMeta.setDisplayName(Lang.getString("menu.bank.anvil-item-display-withdraw", rep, p));
                            anvilStack.setItemMeta(anvilMeta);
                            anvilGui.update();
                        }
                    }.runTaskLater(MyColony.plugin, 40L);
                    return;
                }
                if(region.getBankDeposit() >= amount) {
                    EconomyResponse response = MyColony.econ.withdrawPlayer(Bukkit.getOfflinePlayer(event.getWhoClicked().getName()), amount);
                    if(response.transactionSuccess()) region.setBankDeposit(region.getBankDeposit()-amount);
                    rep.put("%balance%", region.getBankDeposit()+"");
                    ItemMeta meta = info.getItem().getItemMeta();
                    meta.setDisplayName(Lang.getString("menu.bank.bank-info-display", rep, p));
                    meta.setLore(Lang.getStringList("menu.bank.bank-info-lore", rep, p));
                    info.getItem().setItemMeta(meta);
                    thisGui.update();
                    thisGui.show(event.getWhoClicked());
                } else{
                    anvilMeta.setDisplayName(Lang.getString("menu.bank.not-enough-money", p));
                    anvilStack.setItemMeta(anvilMeta);
                    anvilGui.update();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            anvilMeta.setDisplayName(Lang.getString("menu.bank.anvil-item-display-withdraw", rep, p));
                            anvilStack.setItemMeta(anvilMeta);
                            anvilGui.update();
                        }
                    }.runTaskLater(MyColony.plugin, 40L);
                }
            });
            anvilPane.addItem(anvilItem);
            anvilPane.setRepeat(true);
            anvilGui.getFirstItemComponent().addPane(anvilPane);
            anvilGui.getResultComponent().addPane(anvilPane);
            inventoryClickEvent.setCancelled(true);
            anvilGui.show(inventoryClickEvent.getWhoClicked());
        });
        staticPane.addItem(takeItem, 6, 1);

        ItemStack bankInfo = new ItemStack(Material.PAPER);
        ItemMeta bankMeta = bankInfo.getItemMeta();
        bankMeta.setDisplayName(Lang.getString("menu.bank.bank-info-display", rep, p));
        bankMeta.setLore(Lang.getStringList("menu.bank.bank-info-lore", rep, p));
        bankInfo.setItemMeta(bankMeta);
        info = new GuiItem(bankInfo, inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
        staticPane.addItem(info, 4, 1);

        GuiItem prevPage = new GuiItem(Utils.getPrevPage(Material.ARROW, Lang.getString("menu.go-back-button-display", p)), inventoryClickEvent -> {
           RegionGui regionGui = new RegionGui(region.getLocation().getBlock(), region, p);
           inventoryClickEvent.setCancelled(true);
           regionGui.show(inventoryClickEvent.getWhoClicked());
        });
        staticPane.addItem(prevPage, 0, 2);

        this.addPane(staticPane);
    }
}
