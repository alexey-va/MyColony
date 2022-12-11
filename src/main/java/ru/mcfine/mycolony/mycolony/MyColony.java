package ru.mcfine.mycolony.mycolony;

import de.jeff_media.chestsort.api.ChestSortAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ru.mcfine.mycolony.mycolony.city.LandsAPIHook;
import ru.mcfine.mycolony.mycolony.commands.GetRegion;
import ru.mcfine.mycolony.mycolony.compat.ColonyProtection;
import ru.mcfine.mycolony.mycolony.config.MyConfig;
import ru.mcfine.mycolony.mycolony.listeners.BreakChest;
import ru.mcfine.mycolony.mycolony.listeners.OpenChest;
import ru.mcfine.mycolony.mycolony.listeners.PutChest;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;
import ru.mcfine.mycolony.mycolony.tasks.TickerRunnable;
import ru.mcfine.mycolony.mycolony.util.JsonStorage;

public final class MyColony extends JavaPlugin {

    public static MyColony plugin;
    public static RegionManager regionManager;
    private PluginManager pm;
    public BukkitTask tickerTask = null;
    public MyConfig config;
    private JsonStorage jsonStorage;
    public boolean chestSortAPI = false;
    public LandsAPIHook landsHook = null;
    public static ColonyProtection protection;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        regionManager = new RegionManager();
        jsonStorage = new JsonStorage();

        pm = Bukkit.getPluginManager();
        pm.registerEvents(new OpenChest(), this);
        pm.registerEvents(new PutChest(), this);
        pm.registerEvents(new BreakChest(), this);
        getCommand("mycolony").setExecutor(new GetRegion());
        tickerTask = new TickerRunnable(1).runTaskTimer(this, 20L, 20L);
        this.config = new MyConfig();
        jsonStorage.loadData();
        protection = new ColonyProtection();

        if(Bukkit.getPluginManager().getPlugin("ChestSort") != null){
            this.chestSortAPI = true;
        }
        if(Bukkit.getPluginManager().getPlugin("Lands") != null){
            this.landsHook  = new LandsAPIHook();
        }
    }

    @Override
    public void onDisable() {
        jsonStorage.saveDataSync();
    }

    public static RegionManager getRegionManager() {
        return regionManager;
    }

    public JsonStorage getJsonStorage() {
        return jsonStorage;
    }
}
