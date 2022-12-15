package ru.mcfine.mycolony.mycolony.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import ru.mcfine.mycolony.mycolony.MyColony;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lang {

    private static File langFile;
    private static FileConfiguration fileConfiguration;
    private static Map<String, Object> lang = new HashMap<>();
    private static String langSymbol = "en_US";

    public Lang() {
        langSymbol = MyColony.plugin.getConfig().getString("language", "en_US");
        langFile = new File(MyColony.plugin.getDataFolder() + File.separator + "language" +
                File.separator + langSymbol + ".yml");
        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            copy(MyColony.class.getResourceAsStream((File.separator + "language" + File.separator + langSymbol + ".yml").replace("\\", "/")),
                    langFile.getAbsolutePath());
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(langFile);

        readLocale(fileConfiguration);
    }

    private static void copy(InputStream source, String destination) {
        try {
            File file = new File(destination);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                Files.copy(source, Paths.get(destination));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', "&f" + s);
    }


    public static List<String> getStringList(String path, Map<String, String> map, Player p) {
        try {
            List<String> strings = fileConfiguration.getStringList(path);
            String str = fileConfiguration.getString(path, null);
            if (strings.size() == 0 && str == null) {
                fileConfiguration.createSection(path);
                fileConfiguration.set(path, "no value: " + path + ". " + phString(map));
                fileConfiguration.save(langFile);
                return new ArrayList<>();
            }
            if (str != null && strings.size() == 0) strings.add(str);
            List<String> newStrings = new ArrayList<>();
            for (String s : strings) {
                if(map != null) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        s = s.replace(entry.getKey(), entry.getValue());
                    }
                }
                if (MyColony.papi != null) s = MyColony.papi.parse(s, p);
                newStrings.add(translate(s));
            }
            return newStrings;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    public void readLocale(FileConfiguration fc) {
        try {
            lang = fc.getConfigurationSection("").getValues(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //public static Component get(String path, Map<String, String> replace) {
    //    try {
    //        String s = (String) lang.get(path);
    //        if (s == null) {
    //            fileConfiguration.createSection(path);
    //            fileConfiguration.set(path, "no value: " + path + ". " + phString(replace));
    //            fileConfiguration.save(langFile);
    //            return Component.text(path);
    //        }
    //        if (replace != null) for (Map.Entry<String, String> entry : replace.entrySet())
    //            s = s.replace(entry.getKey(), entry.getValue());
    //        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    //    } catch (Exception ex) {
    //        ex.printStackTrace();
    //        return Component.text(path);
    //    }
    //}

    //public static Component get(String path) {
    //    return get(path, null);
    //}

    //public static List<Component> getList(String path, Map<String, String> map) {
    //    try {
    //        List<String> strings = fileConfiguration.getStringList(path);
    //        if (strings.size() == 0) {
    //            fileConfiguration.createSection(path);
    //            fileConfiguration.set(path, "no value: " + path + ". " + phString(map));
    //            fileConfiguration.save(langFile);
    //            return new ArrayList<>();
    //        }
    //        if (map != null) {
    //            for (String s : strings) {
    //                for (Map.Entry<String, String> entry : map.entrySet()) {
    //                    s = s.replace(entry.getKey(), entry.getValue());
    //                }
    //            }
    //        }
    //        List<Component> components = new ArrayList<>();
    //        for (String s : strings) {
    //            components.add(LegacyComponentSerializer.legacyAmpersand().deserialize("&f" + s));
    //        }
    //        return components;
    //    } catch (Exception ex) {
    //        ex.printStackTrace();
    //    }
    //    return null;
    //}

    public static String getString(String path, Player p) {
        return getString(path, null, p);
    }

    public static String getString(String path, Map<String, String> replace, Player p) {
        try {
            String s = (String) lang.get(path);
            if (s == null) {
                fileConfiguration.createSection(path);
                fileConfiguration.set(path, "no value: " + path + ". " + phString(replace));
                fileConfiguration.save(langFile);
                return path;
            }
            if (replace != null) {
                for (Map.Entry<String, String> entry : replace.entrySet()) {
                    s = s.replace(entry.getKey(), entry.getValue());
                }
            }
            if (MyColony.papi != null) s = MyColony.papi.parse(s, p);
            return translate(s);
        } catch (Exception ex) {
            ex.printStackTrace();
            return path;
        }
    }

    private static String phString(Map<String, String> map) {
        if (map == null || map.size() == 0) return "";
        StringBuilder s = new StringBuilder("(Placeholders: ");
        for (var tmp : map.keySet()) {
            s.append(tmp).append(", ");
        }
        s = new StringBuilder(s.substring(0, s.length() - 3));
        s.append(")");
        return s.toString();
    }

}
