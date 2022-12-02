package ru.mcfine.mycolony.mycolony.regions;

import de.jeff_media.chestsort.api.ChestSortAPI;
import javafx.util.Pair;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.mcfine.mycolony.mycolony.MyColony;
import ru.mcfine.mycolony.mycolony.config.Lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Region {
    private ArrayList<String> playerNames;
    private int level;
    private int x, y, z;
    private String worldName;
    private ArrayList<String> playerUUIDs;
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

    private static MiniMessage mm = MiniMessage.miniMessage();


    public Region(ArrayList<String> playerNames, int level, int x, int y, int z, String regionName, String worldName, ArrayList<String> playerUUIDs, RegionType regionType, String uuid) {
        this.regionName = regionName;
        this.playerNames = playerNames;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
        this.playerUUIDs = playerUUIDs;
        this.regionType = regionType;
        if(uuid == null) this.uuid = (UUID.randomUUID().toString());
        else this.uuid = uuid;

        this.location = new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public void incrementTime(double inc)
    {
        this.timeElapsed += inc;
        if(this.timeElapsed > maxTime - inc && this.timeElapsed <= maxTime){
            if(MyColony.plugin.chestSortAPI) ChestSortAPI.sortInventory(((Chest) location.getBlock().getState()).getInventory());
            System.out.println("Sorting...");
        }else if(this.timeElapsed > maxTime){
            this.timeElapsed = 0;
            if(this.regionType.getProductionList() != null){
                Pair<Boolean, ArrayList<ConsumableType>> slotsAndConsumables = ifHasMaterials();
                if(slotsAndConsumables == null) return;
                if(slotsAndConsumables.getKey() && slotsAndConsumables.getValue().size() == 0){
                    takeConsumables();
                    putProduction();
                    notifyPlayer(2);
                    notified = false;
                }
            }
        } else if(this.timeElapsed > maxTime/2.0 && !notified){
            if(this.regionType.getProductionList() != null || this.regionType.getIncome() != 0.0){
                Pair<Boolean, ArrayList<ConsumableType>> slotsAndConsumables = ifHasMaterials();
                if(slotsAndConsumables == null) return;
                if(!slotsAndConsumables.getKey()){
                    notified = true;
                    notifyPlayer(0);
                } else if (slotsAndConsumables.getValue().size() != 0) {
                    notified = true;
                    notifyPlayer(1);
                }
            }
        }
    }


    // 0 - not enough slots
    // 1 - not enough mats
    // 2 - region produced stuff
    public void notifyPlayer(int reasonId){
        ArrayList<Player> players = new ArrayList<>();
        for(String s : playerNames){
            Player player = Bukkit.getPlayerExact(s);
            if(player != null) players.add(player);
        }
        if(players.size() == 0) return;
        if(reasonId == 0){
            players.forEach(player -> player.sendMessage(Lang.get("notify.not-enough-slots")));
        } else if(reasonId == 1){
            players.forEach(player -> player.sendMessage(Lang.get("notify.not-enough-materials")));
        } else if (reasonId == 2) {
            players.forEach(player -> player.sendMessage(Lang.get("notify.region-produced")));
        }
    }

    public boolean takeConsumables(){
        Block block = location.getBlock();
        if(block.getType() == Material.CHEST){
            Chest chest = (Chest) block.getState();
            Inventory inventory = chest.getInventory();
            HashMap<Material, Integer> toRemove = new HashMap<>();
            for(ConsumableType consumableType : this.regionType.getConsumableList()){
                toRemove.put(consumableType.material, consumableType.amount);
            }
                for(int i =inventory.getSize()-1; i>=0; i--){
                    ItemStack itemStack = inventory.getItem(i);
                    if(itemStack == null|| itemStack.getType() == Material.AIR) continue;
                    if(toRemove.containsKey(itemStack.getType())){
                        int amount = toRemove.get(itemStack.getType());
                        int newAmount = Math.max(itemStack.getAmount()-amount, 0);
                        if(newAmount == 0){
                            toRemove.put(itemStack.getType(), amount- itemStack.getAmount());
                            inventory.setItem(i, new ItemStack(Material.AIR));
                        } else{
                            inventory.setItem(i, new ItemStack(itemStack.getType(), newAmount));
                            toRemove.remove(itemStack.getType());
                        }
                    }
                }
        } else {
            System.out.println("Not chest! "+block);
        }
        return true;
    }

    public boolean putProduction(){

        if(this.regionType.getProductionList() == null || this.regionType.getProductionList().size() == 0) return false;
        Block block = location.getBlock();
        if(block.getType() == Material.CHEST){
            Chest chest = (Chest) block.getState();
            Inventory inventory = chest.getInventory();
            for(ConsumableType consumableType : this.regionType.getProductionList()){
                int amount = consumableType.amount;
                while(amount > 0){
                    inventory.addItem(new ItemStack(consumableType.material, Math.min(64, amount)));
                    amount-=Math.min(64, amount);
                }
            }
        } else {
            System.out.println("Not chest! "+block);
        }
        return true;
    }


    public Pair<Boolean,ArrayList<ConsumableType>> ifHasMaterials(){
        ArrayList<ConsumableType> consumables = this.regionType.getConsumableList();
        if(consumables == null) consumables = new ArrayList<>();
        if(location == null || !(location.getBlock().getType() == Material.CHEST)) return null;

        ArrayList<ConsumableType> productList = this.regionType.getProductionList();
        if(productList == null) productList = new ArrayList<>();

        Inventory inventory = ((Chest)location.getBlock().getState()).getInventory();

        HashMap<Material, Integer> needs = new HashMap<>();
        for(ConsumableType consumableType : consumables){
            needs.put(consumableType.material, consumableType.amount);
        }
        HashMap<Material, Integer> products = new HashMap<>();
        for(ConsumableType consumableType : productList){
            products.put(consumableType.material, consumableType.amount);
        }
        int freeSlots = 0;

        for(int i=inventory.getSize()-1 ;i>=0;i--){
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack == null || itemStack.getType() == Material.AIR){
                freeSlots++;
                continue;
            }
            if(needs.containsKey(itemStack.getType())){
                int amount = needs.get(itemStack.getType());
                int newAmount = itemStack.getAmount() - amount;

                if(newAmount <= 0){
                    freeSlots++;
                    if(newAmount == 0) needs.remove(itemStack.getType());
                    else needs.put(itemStack.getType(), amount-itemStack.getAmount());
                } else {
                    needs.remove(itemStack.getType());
                }
            }
            if(products.containsKey(itemStack.getType())){
                int space = 64 - itemStack.getAmount();
                if(space < 0 ) {
                }
                else if(space > 0){
                    int newAmount = products.get(itemStack.getType()) - space;
                    if(newAmount <= 0){
                        products.remove(itemStack.getType());
                    } else{
                        products.put(itemStack.getType(), newAmount);
                    }
                }
            }
        }


        ArrayList<ConsumableType> result = new ArrayList<>();
        for(Map.Entry<Material, Integer> entry : needs.entrySet()){
            result.add(new ConsumableType(entry.getKey(), entry.getValue()));
        }

        boolean fits = true;
        for(Map.Entry<Material, Integer> entry : products.entrySet()){
            int slotNeeded = (int)Math.ceil(entry.getValue()/64.0);
            freeSlots-=slotNeeded;
            if(freeSlots<0){
                fits = false;
                break;
            }
        }
        return new Pair<>(fits, result);
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public ArrayList<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(ArrayList<String> playerNames) {
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

    public ArrayList<String> getPlayerUUIDs() {
        return playerUUIDs;
    }

    public void setPlayerUUID(ArrayList<String> playerUUIDs) {
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
}
