package ru.mcfine.mycolony.mycolony.regions;

import de.jeff_media.chestsort.api.ChestSortAPI;
import javafx.util.Pair;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.city.CityRegion;
import ru.mcfine.mycolony.mycolony.config.Lang;
import ru.mcfine.mycolony.mycolony.production.ProductionEntry;
import ru.mcfine.mycolony.mycolony.util.Utils;

import java.util.*;

public class Region {
    private Set<String> playerNames;
    private int level;
    private int x, y, z;
    private String worldName;
    private Set<String> playerUUIDs;
    private String regionName;
    private double timeElapsed = 0;
    private double maxTime = 10;
    private Location location;
    private double totalIncome;
    private RegionType regionType;
    private boolean requiresMaterials = false;
    private double income = 10;
    private boolean notified = false;
    private String uuid;
    private String selectedChain = null;
    private double bankDeposit = 0;
    private CityRegion cityRegion = null;
    private BlockFace blockFace = null;
    private Pair<Location, Location> corners = null;
    private String wgRegionName = null;

    private static MiniMessage mm = MiniMessage.miniMessage();


    public Region(Set<String> playerNames, int level, int x, int y, int z, String regionName, String worldName, Set<String> playerUUIDs, RegionType regionType, String uuid, String wgRegionName) {
        this.regionName = regionName;
        this.playerNames = playerNames;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.playerUUIDs = playerUUIDs;
        this.regionType = regionType;
        if (uuid == null) this.uuid = (UUID.randomUUID().toString());
        else this.uuid = uuid;
        this.wgRegionName = wgRegionName;

        this.location = new Location(Bukkit.getWorld(worldName), x, y, z);
        this.blockFace = ((Directional) location.getBlock().getBlockData()).getFacing();
        this.corners = Utils.getRegionCorners(location, blockFace, regionType);
    }

    public void incrementTime(double inc) {
        this.timeElapsed += inc;
        if (this.timeElapsed > maxTime - inc && this.timeElapsed <= maxTime) {
            if (MyColony.plugin.chestSortAPI)
                ChestSortAPI.sortInventory(((Chest) location.getBlock().getState()).getInventory());
            //System.out.println("Sorting...");
        } else if (this.timeElapsed > maxTime) {

            this.timeElapsed = 0;
            if (this.regionType.getProductionEntries() != null) {
                boolean success = false;
                if (selectedChain == null) {
                    for (ProductionEntry productionEntry : this.regionType.getProductionEntries()) {
                        //System.out.println(productionEntry);
                        var check = checkProductionConditions(productionEntry);
                        //System.out.println(check +" | "+check.getKey()+" | "+check.getValue().get(0));
                        if (check == null || !(check.getKey() && check.getValue().size() == 0)) continue;
                        takeConsumables(productionEntry);
                        putProduction(productionEntry);
                        notifyPlayer(2);
                        notified = false;
                        success = true;
                        break;
                    }
                }
            }
        }
    }


    // 0 - not enough slots
    // 1 - not enough mats
    // 2 - region produced stuff
    public void notifyPlayer(int reasonId) {
        ArrayList<Player> players = new ArrayList<>();
        for (String s : playerNames) {
            Player player = Bukkit.getPlayerExact(s);
            if (player != null) players.add(player);
        }
        if (players.size() == 0) return;
        if (reasonId == 0) {
            players.forEach(player -> player.sendMessage(Lang.get("notify.not-enough-slots")));
        } else if (reasonId == 1) {
            players.forEach(player -> player.sendMessage(Lang.get("notify.not-enough-materials")));
        } else if (reasonId == 2) {
            players.forEach(player -> player.sendMessage(Lang.get("notify.region-produced")));
        }
    }

    public boolean takeConsumables(ProductionEntry prod) {
        ArrayList<ProductionItem> input = new ArrayList<>(prod.getInput());
        Inventory inventory = null;
        try {
            inventory = ((Chest) location.getBlock().getState()).getInventory();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        if (input.size() == 0 || inventory == null) return true;


        for (int i = inventory.getSize() - 1; i >= 0; i--) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;

            List<ProductionItem> clear = new ArrayList<>();
            for (ProductionItem ct : input) {
                if(ct.type == ProductionItem.Type.MONEY) continue;
                if (ct.hasMaterial(itemStack.getType())) {
                    if(ct.type == ProductionItem.Type.TOOL || ct.type == ProductionItem.Type.TOOL_GROUP){
                        ItemMeta meta = itemStack.getItemMeta();
                        if(meta instanceof Damageable damageable){
                            int usesLeft = itemStack.getType().getMaxDurability() - damageable.getDamage();
                            if (usesLeft > ct.amount) {
                                damageable.setDamage(damageable.getDamage()+ct.amount);
                                itemStack.setItemMeta(damageable);
                                clear.add(ct);
                            } else if (usesLeft == ct.amount) {
                                inventory.setItem(i, new ItemStack(Material.AIR));
                                clear.add(ct);
                            } else {
                                inventory.setItem(i, new ItemStack(Material.AIR));
                                ct.amount = ct.amount - usesLeft;
                            }
                        } else {
                            System.out.println("Not damagable?");
                        }
                    }else {
                        //System.out.println(ct.amount+" am| "+(itemStack.getAmount() - ct.amount));
                        int newAmount = itemStack.getAmount() - ct.amount;
                        if (newAmount < 0) {
                            ct.amount = -newAmount;
                            inventory.setItem(i, new ItemStack(Material.AIR));
                        } else if (newAmount == 0) {
                            clear.add(ct);
                            inventory.setItem(i, new ItemStack(Material.AIR));
                        } else {
                            clear.add(ct);
                            itemStack.setAmount(newAmount);
                        }
                    }
                }
            }
            input.removeAll(clear);
        }

        //System.out.println(prod.getInput().get(0).amount);
        return true;
    }

    public boolean putProduction(ProductionEntry prod) {
        Inventory inventory = ((Chest) location.getBlock().getState()).getInventory();
        for (ProductionItem output : prod.getOutput()) {
            //System.out.println(output.material +" |out "+output.amount);
            if (output.type == ProductionItem.Type.MONEY) {
                this.bankDeposit += output.amount;
            } else {
                int amount = output.amount;
                while (amount > 0) {
                    if (output.group != null) {
                        inventory.addItem(new ItemStack(getRandom(output.group, Material.CAKE), Math.min(64, amount)));
                    } else if (output.material != null) {
                        inventory.addItem(new ItemStack(output.material, Math.min(64, amount)));
                    }
                    amount -= Math.min(64, amount);
                }
            }
        }
        return true;
    }

    private <T> T getRandom(Set<T> set, T def) {
        int id = (new Random()).nextInt(set.size());
        int counter = 0;
        for (var s : set) {
            if (counter == id) return s;
            counter++;
        }
        return def;
    }


    public Pair<Boolean, List<ProductionItem>> checkProductionConditions(ProductionEntry prod) {

        List<ProductionItem> input = new ArrayList<>(prod.getInput());
        List<ProductionItem> output = new ArrayList<>(prod.getOutput());
        Inventory inventory = null;
        try {
            inventory = ((Chest) location.getBlock().getState()).getInventory();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        int freeSlots = 0;
        for (int i = inventory.getSize() - 1; i >= 0; i--) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                freeSlots++;
                continue;
            }

            // Check if has mats
            List<ProductionItem> clear = new ArrayList<>();
            for (var in : input) {
                if (in.hasMaterial(itemStack.getType())) {
                    //System.out.println(in.type);
                    if (in.type == ProductionItem.Type.TOOL || in.type == ProductionItem.Type.TOOL_GROUP) {
                        ItemMeta meta = itemStack.getItemMeta();
                        if (meta instanceof Damageable damageable) {
                            int usesLeft = itemStack.getType().getMaxDurability() - damageable.getDamage();
                            //System.out.println(itemStack.getType().getMaxDurability()+" | "+damageable.getDamage());
                            if (usesLeft > in.amount) {
                                clear.add(in);
                            } else if (usesLeft == in.amount) {
                                freeSlots++;
                                clear.add(in);
                            } else {
                                freeSlots++;
                                in.amount = in.amount - usesLeft;
                            }
                        } else{
                            System.out.println("Not damagable tool?");
                        }
                    } else {
                        int newAmount = itemStack.getAmount() - in.amount;
                        if (newAmount < 0) {
                            freeSlots++;
                            in.amount = -newAmount;
                        } else if (newAmount == 0) {
                            freeSlots++;
                            clear.add(in);
                        } else {
                            clear.add(in);
                        }
                    }
                }
            }
            input.removeAll(clear);

            if (itemStack.getAmount() == 64) continue;

            // Check output will fit
            clear.clear();
            for (var out : output) {
                if (out.type == ProductionItem.Type.TOOL || out.type == ProductionItem.Type.TOOL_GROUP) continue;
                if (out.hasMaterial(itemStack.getType())) {
                    // amount of items in production left after sorting
                    int newAmount = out.amount - 64 + itemStack.getAmount();
                    if (newAmount < 0) {
                        freeSlots++;
                        clear.add(out);
                    } else if (newAmount == 0) {
                        freeSlots++;
                        clear.add(out);
                    } else {
                        out.amount = newAmount;
                    }
                }
            }
            output.removeAll(clear);
        }

        boolean fits = true;
        for (var out : output) {
            int slotNeeded = (int) Math.ceil(out.amount / 64.0);
            freeSlots -= slotNeeded;
            if (freeSlots < 0) {
                fits = false;
                break;
            }
        }

        return new Pair<>(fits, input);
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Set<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(Set<String> playerNames) {
        this.playerNames = playerNames;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public double getIncome() {
        return income;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public Set<String> getPlayerUUIDs() {
        return playerUUIDs;
    }

    public void setPlayerUUID(Set<String> playerUUIDs) {
        this.playerUUIDs = playerUUIDs;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public double getMaxTime() {
        return maxTime;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public String getUuid() {
        return uuid;
    }

    public double getBankDeposit() {
        return bankDeposit;
    }

    public void setBankDeposit(double bankDeposit) {
        this.bankDeposit = bankDeposit;
    }

    public void setCorners(Pair<Location, Location> corners) {
        this.corners = corners;
    }

    public Pair<Location, Location> getCorners() {
        return corners;
    }

    public Location getLocation() {
        return location;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public boolean isRequiresMaterials() {
        return requiresMaterials;
    }

    public boolean isNotified() {
        return notified;
    }

    public String getSelectedChain() {
        return selectedChain;
    }

    public CityRegion getCityRegion() {
        return cityRegion;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    public static MiniMessage getMm() {
        return mm;
    }

    public String getWgRegionName() {
        return wgRegionName;
    }
}
