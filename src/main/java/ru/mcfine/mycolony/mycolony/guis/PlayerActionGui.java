package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class PlayerActionGui extends ChestGui {
    public PlayerActionGui(OfflinePlayer offlinePlayer, Player p, Gui parent) {
        super(3, "dummy");
        Map<String, String> rep = new HashMap<>();
        rep.put("%name%", offlinePlayer.getName());

        this.setTitle(Lang.getString("menu.member-action.title", rep, p));

        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(Utils.getBackground(), event -> {
            event.setCancelled(true);
        }));
        background.setRepeat(true);
        this.addPane(background);

        OutlinePane background2 = new OutlinePane(1, 1, 7, 1, Pane.Priority.LOW);
        background2.addItem(new GuiItem(Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE), event -> {
            event.setCancelled(true);
        }));
        background2.setRepeat(true);
        this.addPane(background2);


        GuiItem prev = new GuiItem(Utils.getPrevPage(Material.ARROW, Lang.getString("menu.go-back-button-display", rep, p)), inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);
            parent.show(inventoryClickEvent.getWhoClicked());
        });
        StaticPane staticPane = new StaticPane(0, 2, 9, 1);
        staticPane.addItem(prev, 0 , 0);


        this.addPane(staticPane);
    }
}
