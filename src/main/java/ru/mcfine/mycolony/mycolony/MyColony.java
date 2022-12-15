package ru.mcfine.mycolony.mycolony;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.mcfine.mycolony.mycolony.compat.LandsAPIHook;
import ru.mcfine.mycolony.mycolony.commands.GetRegion;
import ru.mcfine.mycolony.mycolony.compat.ColonyProtection;
import ru.mcfine.mycolony.mycolony.compat.Papi;
import ru.mcfine.mycolony.mycolony.config.Lang;
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
    public static Economy econ = null;
    public static Permission perms = null;
    public static Lang lang;
    public static Papi papi = null;
    public boolean useLands = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            papi = new Papi();
        }
        if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null){
            protection = new ColonyProtection();
        }
        if(Bukkit.getPluginManager().getPlugin("ChestSort") != null){
            this.chestSortAPI = true;
        }


        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();

        regionManager = new RegionManager();
        jsonStorage = new JsonStorage();
        config = new MyConfig();
        lang = new Lang();

        pm = Bukkit.getPluginManager();
        pm.registerEvents(new OpenChest(), this);
        pm.registerEvents(new PutChest(), this);
        pm.registerEvents(new BreakChest(), this);
        getCommand("mycolony").setExecutor(new GetRegion());
        tickerTask = new TickerRunnable(1).runTaskTimer(this, 20L, 20L);
        jsonStorage.loadData();

    }

    @Override
    public void onLoad(){
        plugin=this;
        this.useLands = getConfig().getBoolean("lands-integration.enable", true);
        if(Bukkit.getPluginManager().getPlugin("Lands") != null && useLands){
            this.landsHook  = new LandsAPIHook();
        }
    }

    @Override
    public void onDisable() {
        jsonStorage.saveRegions();
        jsonStorage.savePlayers();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        assert rsp != null;
        perms = rsp.getProvider();
    }

    public static RegionManager getRegionManager() {
        return regionManager;
    }

    public JsonStorage getJsonStorage() {
        return jsonStorage;
    }
}
