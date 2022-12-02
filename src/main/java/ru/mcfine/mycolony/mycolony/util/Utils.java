package ru.mcfine.mycolony.mycolony.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.config.MyConfig;

public class Utils {

    private static MiniMessage mm = MiniMessage.miniMessage();

    public static ItemStack getBackground(){
        ConfigurationSection section = MyColony.plugin.getConfig().getConfigurationSection("background-item");
        Material material = null;
        Component displayName = Component.text(" ");
        int modelData = 0;
        if(section != null) {
            material = Material.matchMaterial(section.getString("material", "BLACK_STAINED_GLASS_PANE"));
            displayName = mm.deserialize(section.getString("display-name", " "));
            modelData = section.getInt("model-data", 0);
        }

        if(material == null) material = Material.BLACK_STAINED_GLASS_PANE;

        ItemStack bg = new ItemStack(material, 1);
        ItemMeta meta = bg.getItemMeta();
        meta.setCustomModelData(modelData);
        meta.displayName(displayName);
        bg.setItemMeta(meta);

        return bg;
    }

}
