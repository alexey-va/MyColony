package ru.mcfine.mycolony.mycolony;

import de.jeff_media.chestsort.api.ChestSortAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
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
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;

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

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
    }

    @Override
    public void onDisable() {
        jsonStorage.saveDataSync();
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

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        assert rsp != null;
        chat = rsp.getProvider();
        return true;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        assert rsp != null;
        perms = rsp.getProvider();
        return true;
    }

    public static RegionManager getRegionManager() {
        return regionManager;
    }

    public JsonStorage getJsonStorage() {
        return jsonStorage;
    }
}
