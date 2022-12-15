package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Chest;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.requirements.Requirement;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.ArrayList;
import java.util.List;


public class RequirementGui extends ChestGui {

    private List<GuiItem> guiItemList = new ArrayList<>();

    public RequirementGui(List<Requirement> requirements) {
        super(3, "Not satis");

        OutlinePane background = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(Utils.getBackground(), event -> {
            event.setCancelled(true);
        }));
        background.setRepeat(true);
        this.addPane(background);

        OutlinePane background2 = new OutlinePane(1,1,7,3, Pane.Priority.LOW);
        background2.addItem(new GuiItem(Utils.getBackground(Material.GRAY_STAINED_GLASS_PANE), event -> event.setCancelled(true)));
        background2.setRepeat(true);
        this.addPane(background2);

        PaginatedPane staticPane = new PaginatedPane(1,1, 7, 1, Pane.Priority.HIGH);

        for(Requirement req : requirements){
            ItemStack itemStack = new ItemStack(Material.RED_WOOL);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName((req.getReq().toString()));
            itemStack.setItemMeta(itemMeta);
            GuiItem guiItem = new GuiItem(itemStack, inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
            guiItemList.add(guiItem);
        }
        staticPane.populateWithGuiItems(guiItemList);
        this.addPane(staticPane);

    }
}
