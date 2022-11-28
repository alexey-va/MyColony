package ru.mcfine.mycolony.mycolony;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.mcfine.mycolony.mycolony.commands.GetRegion;
import ru.mcfine.mycolony.mycolony.config.MyConfig;
import ru.mcfine.mycolony.mycolony.listeners.OpenChest;
import ru.mcfine.mycolony.mycolony.listeners.PutChest;
import ru.mcfine.mycolony.mycolony.regions.RegionManager;
import ru.mcfine.mycolony.mycolony.tasks.TickerRunnable;

public final class MyColony extends JavaPlugin {

    public static MyColony plugin;
    public static RegionManager regionManager;
    private PluginManager pm;
    private BukkitTask tickerTask = null;
    public MyConfig config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        regionManager = new RegionManager();
        pm = Bukkit.getPluginManager();
        pm.registerEvents(new OpenChest(), this);
        pm.registerEvents(new PutChest(), this);
        getCommand("mycolony").setExecutor(new GetRegion());
        tickerTask = new TickerRunnable(3).runTaskTimer(this, 40L, 40L);
        this.config = new MyConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
