package ru.mcfine.mycolony.mycolony.guis;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.regions.RegionType;
import ru.mcfine.mycolony.mycolony.util.Utils;

public class ShopItemGui extends ChestGui {
    private ShopGroupGui parentMenu;
    public ShopItemGui(RegionType regionType, ShopGroupGui parentMenu) {
        super(6, regionType.getDisplayName());
        this.parentMenu = parentMenu;

        OutlinePane background = new OutlinePane(0,0,9,6, Pane.Priority.LOWEST);
        background.addItem(new GuiItem(Utils.getBackground(), event -> event.setCancelled(true)));
        background.setRepeat(true);
        this.addPane(background);

        StaticPane staticPane = new StaticPane(0, 0, 9, 6, Pane.Priority.HIGH);0
    }
}
