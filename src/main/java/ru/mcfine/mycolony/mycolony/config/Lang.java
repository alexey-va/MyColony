package ru.mcfine.mycolony.mycolony.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.mcfine.mycolony.mycolony.MyColony;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Lang {

    private File langFile;
    private FileConfiguration fileConfiguration;
    private static Map<String, Object> lang = new HashMap<>();
    private static MiniMessage mm = MiniMessage.miniMessage();
    private String langSymbol = "en_US";

    public Lang(){
        langSymbol = MyColony.plugin.getConfig().getString("language", "en_US");
        langFile = new File(MyColony.plugin.getDataFolder()+File.separator+"language"+
                File.separator+langSymbol+".yml");
        if(!langFile.exists()){
            langFile.getParentFile().mkdirs();
            MyColony.plugin.config.copy(MyColony.class.getResourceAsStream(File.separator+"language"+File.separator+langSymbol+".yml"),
                    langFile.getAbsolutePath());
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(langFile);

        readLocale(fileConfiguration);
    }

    public void readLocale(FileConfiguration fc){
        try {
            lang = fc.getConfigurationSection("").getValues(true);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static Component get(String path){
        try {
            String s = (String) lang.get(path);
            if(s == null) return Component.text(path);
            return mm.deserialize(s);
        } catch (Exception ex){
            ex.printStackTrace();
            return Component.text(path);
        }
    }

    public static String getString(String path){
        try{
            String s = (String) lang.get(path);
            if(s==null) return path;
            return LegacyComponentSerializer.legacyAmpersand().serialize(mm.deserialize(s));
        } catch (Exception ex){
            ex.printStackTrace();
            return path;
        }
    }

}
